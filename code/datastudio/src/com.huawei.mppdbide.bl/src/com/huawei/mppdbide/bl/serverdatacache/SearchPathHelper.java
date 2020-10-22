/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.util.BLUtils;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class SearchPathHelper.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class SearchPathHelper {

    ConnectionManager connM = null;

    private List<String> searchPath;

    /**
     * Instantiates a new search path helper.
     *
     * @param connectionManager the connection manager
     */
    public SearchPathHelper(ConnectionManager connectionManager) {
        connM = connectionManager;

        searchPath = new ArrayList<String>();
    }

    /**
     * Gets the search path.
     *
     * @return the search path
     */
    public List<String> getSearchPath() {
        return searchPath;
    }

    /**
     * Removes the from search path.
     *
     * @param namespace the namespace
     */
    public void removeFromSearchPath(String namespace) {
        searchPath.remove(namespace);
    }

    /**
     * Fetch user search path.
     *
     * @param username the username
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void fetchUserSearchPath(String username) throws DatabaseOperationException, DatabaseCriticalException {
        String qry = MPPDBIDEConstants.SHOW_SEARCHPATH_QUERY;
        ResultSet rs = null;
        boolean hasMoreRs = false;

        try {
            rs = connM.execSelectAndReturnRsOnObjBrowserConn(qry);
            searchPath.clear();
            hasMoreRs = rs.next();
            if (hasMoreRs) {
                addToSearchPath(username, rs);
            }
        } catch (SQLException exception) {
            try {
                GaussUtils.handleCriticalException(exception);
            } catch (DatabaseCriticalException dc) {
                throw dc;
            }
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            connM.closeRSOnObjBrowserConn(rs);
        }

    }

    /**
     * Adds the to search path.
     *
     * @param username the username
     * @param rs the rs
     * @throws SQLException the SQL exception
     */
    private void addToSearchPath(String username, ResultSet rs) throws SQLException {
        String searchPathStr = rs.getString(1);
        if (searchPathStr != null) {
            String[] searchPathList = searchPathStr.replace("$user", username).split(",");
            for (String str : searchPathList) {
                String strTrim = str.trim();
                searchPath.add(BLUtils.getUnQuotedIdentifier(strTrim, "\""));
            }
        } else {
            searchPath.add(MPPDBIDEConstants.PUBLIC_SCHEMA_NAME);
        }
    }
}