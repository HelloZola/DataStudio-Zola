/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.prefernces.KeyBindingWrapper;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.ui.connectiondialog.DBConnectionDialog;
import com.huawei.mppdbide.view.utils.PasswordExpiryNotifier;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class NewDbConnection.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class NewDbConnection {

    @Optional
    @Inject
    private IWorkbench workbench;
    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    /**
     * Instantiates a new new db connection.
     */
    public NewDbConnection() {
    }

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        MPPDBIDELoggerUtility
                .info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_NEWDBCONNECTION_OPEN_CONNECTION_WIZARD));
        DBConnectionDialog connDialog = new DBConnectionDialog(Display.getDefault().getActiveShell(), modelService,
                application, false);

        IExecTimer timer = new ExecTimer("Initial Connection");
        timer.start();
        connDialog.open();
        try {
            timer.stop();
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Error getting time", exception);

        }
        if (PreferenceWrapper.getInstance().isPreferenceValid()) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, false,
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_LOAD_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_LOAD_MSG));
            
            PreferenceWrapper.getInstance().setPreferenceValid(false);
            return;
        }
        boolean flag = connDialog.isFlag();
        if (flag) {
            Database database = DBConnProfCache.getInstance().getDbForProfileId(connDialog.getProfileId());
            PasswordExpiryNotifier passwordExpiryNotifier = new PasswordExpiryNotifier(database);
            passwordExpiryNotifier.checkAndNotifyPasswordExpiry();
            connDialog.setFlag(false);
        }
        if (connDialog.isExceptionOccured()) {

            workbench.close();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        // DTS2014102907592 satrt
        return UIVersionHandler.isVersionCompatible();
        // DTS2014102907592 end
    }
}