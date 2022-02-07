package com.huawei.mppdbide.mock.bl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import com.huawei.mppdbide.adapter.AbstractConnectionDriver;
import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.adapter.driver.DBMSDriverManager;
import com.huawei.mppdbide.adapter.gauss.DBConnection;
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
 * Description: The Class DBConnectionForTestSupport.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class DBConnectionForTestSupport {

    /**
     * Creates the mock connections.
     *
     * @param connObjects the conn objects
     * @param props the props
     * @param url the url
     * @param dbType the db type
     */
    public static void createMockConnections(DBConnection[] connObjects, Properties props, String url, String dbType) {
        for (DBConnection c : connObjects) {
            try {
                if (dbType != null) {
                    c.setDriver(getDriver(dbType));
                }
                c.setConnection(dbConnect(props, url));
            } catch (DatabaseOperationException | DatabaseCriticalException e) {
                MPPDBIDELoggerUtility.info("Mock Connections Failed");
            }
        }
    }

    /**
     * Db connect.
     *
     * @param props the props
     * @param url the url
     * @return the connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static Connection dbConnect(Properties props, String url)
            throws DatabaseOperationException, DatabaseCriticalException {
        Connection conn = null;
        try {
            MPPDBIDELoggerUtility.info("ADAPTER: Sending connection request");
            conn = DriverManager.getConnection(url, props);
            MPPDBIDELoggerUtility.info("ADAPTER: Successfully connected");
        } catch (SQLException exp) {
            handleSQLException(exp);
        }

        return conn;
    }

    private static void handleSQLException(SQLException ex)
            throws DatabaseOperationException, DatabaseCriticalException {
        if (MessageConfigLoader.getProperty(MPPDBIDEConstants.PROTOCOL_VERSION_ERROR)
                .equalsIgnoreCase(ex.getMessage())) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.DOMAINNAME_REDIRECT_FAILURE,
                    MessageConfigLoader.getProperty(MPPDBIDEConstants.PROTOCOL_VERSION_ERROR)
                            + MPPDBIDEConstants.LINE_SEPARATOR));
            throw new DatabaseOperationException(IMessagesConstants.DOMAINNAME_REDIRECT_FAILURE,
                    MessageConfigLoader.getProperty(MPPDBIDEConstants.PROTOCOL_VERSION_ERROR)
                            + MPPDBIDEConstants.LINE_SEPARATOR);
        } else {
            GaussUtils.handleCriticalException(ex);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED, ex);
        }
    }

    /**
     * Gets the driver.
     *
     * @param dbType the db type
     * @return the driver
     */
    public static IConnectionDriver getDriver(String dbType) {
        ArrayList<AbstractConnectionDriver> olapDriverInstance = DBMSDriverManager
                .getOLAPDriverInstance(BLUtils.getInstance().getInstallationLocation());
        return olapDriverInstance.get(0);
    }

}