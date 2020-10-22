/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DatabaseHelper;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserNamespaceObjectGroup;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.LoadLevel1Objects;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.GUISM;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateSchema.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CreateSchema {
    private StatusMessage statusMessage;
    private CreateSchemaWorker createSchemaWorker;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        final UserNamespaceObjectGroup userNsGroup = (UserNamespaceObjectGroup) IHandlerUtilities
                .getObjectBrowserSelectedObject();
        if (userNsGroup != null) {
            Database db = userNsGroup.getDatabase();

            if (null != db && (db.getServer().isServerInProgress()
                    || db.getServer().getDatabaseGroup().isLoadingDatabaseGroupInProgress()
                    || db.isLoadingNamespaceInProgress())) {
                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.REFRESH_IN_PROGRESS),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXECTION_IN_PROGRESS, GUISM.REFRESH));
                return;
            }
            UserInputDialog createSchemaDialog = new CreateSchemaInner(shell, db, db);

            createSchemaDialog.open();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        UserNamespaceObjectGroup userNsGroup = (UserNamespaceObjectGroup) IHandlerUtilities
                .getObjectBrowserSelectedObject();
        if (userNsGroup == null) {
            return false;
        } else {
            Database selectedDb = userNsGroup.getDatabase();
            return selectedDb.isConnected();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CreateSchemaInner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class CreateSchemaInner extends UserInputDialog {

        /**
         * Instantiates a new creates the schema inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param selectedDb the selected db
         */
        private CreateSchemaInner(Shell parent, Object serverObject, Database selectedDb) {
            super(parent, serverObject);
        }

        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_NAMESPACE, this.getClass());
        }

        @Override
        public void performOkOperation() {
            createSchemaWorker = null;

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            Database db = (Database) getObject();
            StatusMessage statMessage = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CREATE_SCHEMA));

            String schemaName = getUserInput();

            if ("".equals(schemaName)) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.ENTER_SCHEMA_NAME_TO_CONTINUE),
                        false);
                if (bottomStatusBar != null) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                return;
            }

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_CREATING_SCHEMA), true);

            createSchemaWorker = new CreateSchemaWorker(db, schemaName, this, statMessage);
            setStatusMessage(statMessage);
            StatusMessageList.getInstance().push(statMessage);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            createSchemaWorker.schedule();
            enableCancelButton();
        }

        @Override
        protected void cancelPressed() {
            performCancelOperation();
        }

        @Override
        protected void performCancelOperation() {
            if (createSchemaWorker != null && createSchemaWorker.getState() == Job.RUNNING) {
                int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_OPERATION_MSG));

                if (0 == returnValue) {
                    createSchemaWorker.cancelJob();
                    createSchemaWorker = null;
                } else {
                    enableCancelButton();
                }
            } else {
                close();
            }

        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.CREATE_SCHEMA);
        }

        @Override
        protected String getHeader() {
            return MessageConfigLoader.getProperty(IMessagesConstants.ENTER_SCHMEA_NAME);
        }

        @Override
        public void onSuccessUIAction(Object obj) {

        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {

        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {

        }

        @Override
        public void onPresetupFailureUIAction(MPPDBIDEException exception) {

        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CreateSchemaWorker.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class CreateSchemaWorker extends UserInputDialogUIWorkerJob {

        private Database db;
        private String schemaName;

        /**
         * Instantiates a new creates the schema worker.
         *
         * @param db the db
         * @param schemaName the schema name
         * @param dialog the dialog
         * @param statusMessage the status message
         */
        private CreateSchemaWorker(Database db, String schemaName, UserInputDialog dialog,
                StatusMessage statusMessage) {
            super("Create Schema", null, dialog, statusMessage, schemaName,
                    IMessagesConstants.ERROR_WHILE_CREATING_SCHEMA,
                    IMessagesConstants.CONNECTION_ERROR_DURING_SCHEMA_CREATION);
            this.db = db;
            this.schemaName = schemaName;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            setConnInfra(PromptPrdGetConnection.getConnection(db));
            DatabaseHelper.createNewSchema(schemaName, db);

            LoadLevel1Objects load = new LoadLevel1Objects(db.getUserNamespaceGroup(), statusMsg);
            load.loadObjects();
            MPPDBIDELoggerUtility.info("New schema created ");
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObjectInUIThread(db);
            }
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                    .getProperty(IMessagesConstants.CREATE_SCHEMA_SUCCESS, db.getName(), schemaName)));
        }
    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMessage = statMessage;
    }

}