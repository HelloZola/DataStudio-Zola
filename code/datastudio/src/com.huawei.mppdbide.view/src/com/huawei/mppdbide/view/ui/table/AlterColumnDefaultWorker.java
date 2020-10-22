/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.view.handler.connection.AbstractDialogWindowOperationUIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class AlterColumnDefaultWorker.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class AlterColumnDefaultWorker extends AbstractDialogWindowOperationUIWorkerJob {
    private ColumnMetaData selectedColumn;
    private String expression;
    private boolean isFunc;

    /**
     * Instantiates a new alter column default worker.
     *
     * @param name the name
     * @param column the column
     * @param exp the exp
     * @param isFunction the is function
     * @param msg the msg
     * @param dialogWorkerInteraction the dialog worker interaction
     */
    public AlterColumnDefaultWorker(String name, ColumnMetaData column, String exp, boolean isFunction, String msg,
            IDialogWorkerInteraction dialogWorkerInteraction) {
        super(name, column, msg, dialogWorkerInteraction, MPPDBIDEConstants.CANCELABLEJOB);
        this.selectedColumn = column;
        this.expression = exp;
        this.isFunc = isFunction;
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
        selectedColumn.execAlterDefault(expression, isFunc, conn);
        selectedColumn.getParentTable().refresh(conn);
        return null;
    }

    /**
     * Gets the success msg for OB status bar.
     *
     * @return the success msg for OB status bar
     */
    @Override
    protected String getSuccessMsgForOBStatusBar() {

        return null;
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