/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.connection.AbstractModalLessWindowOperationUIWokerJob;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ReindexTableWorker.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ReindexTableWorker extends AbstractModalLessWindowOperationUIWokerJob {
    private TableMetaData selectedTable;

    /**
     * Instantiates a new reindex table worker.
     *
     * @param name the name
     * @param tab the tab
     */
    public ReindexTableWorker(String name, TableMetaData tab) {
        super(name, tab, MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_REINDEX_TABLE),
                MPPDBIDEConstants.CANCELABLEJOB);
        selectedTable = tab;
    }

    /**
     * Gets the success msg for OB status bar.
     *
     * @return the success msg for OB status bar
     */
    @Override
    protected String getSuccessMsgForOBStatusBar() {
        return MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE, selectedTable.getNamespace().getName(),
                selectedTable.getName());
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws Exception the exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        selectedTable.execReindex(conn);
        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(getSuccessMsgForOBStatusBar()));
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE,
                        selectedTable.getNamespace().getName(), selectedTable.getName()));
        super.additionalDoJobhandling();
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_CONN_ERROR,
                        selectedTable.getNamespace().getName(), selectedTable.getName(), exception.getServerMessage()));
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_FAIL,
                        selectedTable.getNamespace().getName(), selectedTable.getName())));
        MPPDBIDELoggerUtility.error("ReindexTableWorker: Reindexing table failed.", exception);
    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_ERROR,
                        selectedTable.getNamespace().getName(), selectedTable.getName(), exception.getServerMessage()));
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_FAIL,
                        selectedTable.getNamespace().getName(), selectedTable.getName())));
        MPPDBIDELoggerUtility.error("ReindexTableWorker: Reindexing table failed.", exception);

    }

    /**
     * Gets the object browser refresh item.
     *
     * @return the object browser refresh item
     */
    @Override
    protected ServerObject getObjectBrowserRefreshItem() {

        return null;
    }

}
