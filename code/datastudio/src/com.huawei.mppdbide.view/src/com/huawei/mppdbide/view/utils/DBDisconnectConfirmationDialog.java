/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBDisconnectConfirmationDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DBDisconnectConfirmationDialog extends MessageDialog {

    /**
     * Instantiates a new DB disconnect confirmation dialog.
     *
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param dialogTitleImage the dialog title image
     * @param dialogMessage the dialog message
     * @param dialogImageType the dialog image type
     * @param subMessage the sub message
     * @param defaultIndex the default index
     */
    public DBDisconnectConfirmationDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage,
            String dialogMessage, int dialogImageType, String[] subMessage, int defaultIndex) {

        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, subMessage, defaultIndex);

    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        return super.createDialogArea(parent);
    }

    

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setImage(IconUtility.getIconImage(IiconPath.ICO_DISCONNECTED_DB, this.getClass()));
    }

    /**
     * Checks if is resizable.
     *
     * @return true, if is resizable
     */
    public boolean isResizable() {
        return true;
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        Button yesButton = null;
        Button noButton = null;

        final String labelYesButton = "     " + MessageConfigLoader.getProperty(IMessagesConstants.YES_OPTION)
                + "     ";

        final String labelNoButton = "     " + MessageConfigLoader.getProperty(IMessagesConstants.NO_OPTION) + "     ";

        yesButton = createButton(parent, UIConstants.OK_ID, labelYesButton, false);
        yesButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CONTINUE_001");

        noButton = createButton(parent, UIConstants.CANCEL_ID, labelNoButton, true);
        noButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECTION_CANCEL_001");

    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        setReturnCode(buttonId);
        close();
    }

}