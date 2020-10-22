/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.internal.WorkbenchPlugin;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.ui.autosave.IAutoSaveObject;
import com.huawei.mppdbide.view.ui.connectiondialog.TransactionDialog;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSTransactionPreferencePage.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
@SuppressWarnings("restriction")
public class DSTransactionPreferencePage extends PreferencePage {
    private IPreferenceStore preferenceStore;
    private Button enableAutoCommitBtn;
    private Button disableAutoCommitBtn;
    private static final String SPACE = " ";
    private static final String TAB = "      ";

    /**
     * The Constant IS_SHOW_AUTOCOMMIT.
     */
    public static final String IS_SHOW_AUTOCOMMIT = "sqlterminal.showautocommit";

    /**
     * The Constant CONN_AUTOCOMMIT_PREF.
     */
    public static final String CONN_AUTOCOMMIT_PREF = "sqlterminal.autocommit";

    /**
     * Instantiates a new DS transaction preference page.
     */
    public DSTransactionPreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.PREF_TRANSACTION_SETTING));
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        preferenceStore = getPreferenceStore();
        if (preferenceStore == null) {
            preferenceStore = WorkbenchPlugin.getDefault().getPreferenceStore();
        }
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout(1, false));
        Group autoCommitGrp = new Group(comp, SWT.NONE);
        autoCommitGrp.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_TRANSACTION_AUTOCOMMIT));
        autoCommitGrp.setLayout(new GridLayout());
        autoCommitGrp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        enableAutoCommitBtn = new Button(autoCommitGrp, SWT.RADIO);
        enableAutoCommitBtn.setText(SPACE + MessageConfigLoader.getProperty(IMessagesConstants.AUTOCOMMIT_ENABLE));
        Label enableAssistantLabel = new Label(autoCommitGrp, SWT.NONE);
        enableAssistantLabel.setText(TAB + MessageConfigLoader.getProperty(IMessagesConstants.AUTOCOMMIT_ENABLE_DESC));
        disableAutoCommitBtn = new Button(autoCommitGrp, SWT.RADIO);
        disableAutoCommitBtn.setText(SPACE + MessageConfigLoader.getProperty(IMessagesConstants.AUTOCOMMIT_DISABLE));
        Label disableAssistantLabel = new Label(autoCommitGrp, SWT.NONE);
        disableAssistantLabel
                .setText(TAB + MessageConfigLoader.getProperty(IMessagesConstants.AUTOCOMMIT_DISABLE_DESC));
        if (preferenceStore.getBoolean(CONN_AUTOCOMMIT_PREF)) {
            enableAutoCommitBtn.setSelection(true);
        } else {
            disableAutoCommitBtn.setSelection(true);
        }
        MouseListener selectListener = commitBtnMouseListener();
        enableAutoCommitBtn.addMouseListener(selectListener);
        disableAutoCommitBtn.addMouseListener(selectListener);
        return comp;
    }

    private MouseListener commitBtnMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {

            }

            @Override
            public void mouseDown(MouseEvent e) {

            }

            @Override
            public void mouseUp(MouseEvent e) {
                if (enableAutoCommitBtn.getSelection()) {
                    if (getAllSQLTerminalTransactionStatus()) {
                        disableAutoCommitBtn.setSelection(true);
                        enableAutoCommitBtn.setSelection(false);
                    }
                }
                if (!enableAutoCommitBtn.getSelection() == preferenceStore
                        .getBoolean(DSTransactionPreferencePage.CONN_AUTOCOMMIT_PREF)) {
                    getApplyButton().setEnabled(true);
                }
            }
        };
    }

    /**
     * Creates the control.
     *
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        getDefaultsButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_DEFAULT));
        getApplyButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_APPLY));
        getApplyButton().setEnabled(false);
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        if (preferenceStore != null) {
            setupAllSQLTerminalTransactionStatus(enableAutoCommitBtn.getSelection());
            if (preferenceStore.getBoolean(CONN_AUTOCOMMIT_PREF) != enableAutoCommitBtn.getSelection()) {
                MPPDBIDELoggerUtility.operationInfo(
                        String.format(Locale.ENGLISH, "Autocommit in preferences has been set from %b to %b",
                                preferenceStore.getBoolean(CONN_AUTOCOMMIT_PREF), enableAutoCommitBtn.getSelection()));
            }
            preferenceStore.setValue(CONN_AUTOCOMMIT_PREF, enableAutoCommitBtn.getSelection());
        }
        return true;
    }

    /**
     * Perform apply.
     */
    @Override
    protected void performApply() {
        performOk();
        getApplyButton().setEnabled(false);
    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        boolean status = getAllSQLTerminalTransactionStatus();
        disableAutoCommitBtn.setSelection(status);
        enableAutoCommitBtn.setSelection(!status);
        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility.operationInfo("Autocommit in preferences is set to default: true");
    }

    /**
     * Sets the default preferences.
     *
     * @param preferenceStore the new default preferences
     */
    public static void setDefaultPreferences(IPreferenceStore preferenceStore) {
        preferenceStore.setDefault(CONN_AUTOCOMMIT_PREF, true);
        preferenceStore.setDefault(IS_SHOW_AUTOCOMMIT, false);
    }

    /**
     * Gets the all SQL terminal transaction status.
     *
     * @return the all SQL terminal transaction status
     */
    private boolean getAllSQLTerminalTransactionStatus() {
        StringBuilder terminalLableSB = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        boolean hasOpenTransaction = false;
        List<IAutoSaveObject> list = UIElement.getInstance().getAllOpenTerminals();
        Iterator<IAutoSaveObject> iter = list.iterator();
        while (iter.hasNext()) {
            IAutoSaveObject autoSaveObject = iter.next();
            if (autoSaveObject instanceof SQLTerminal) {
                SQLTerminal terminal = (SQLTerminal) autoSaveObject;
                try {
                    DBConnection connection = terminal.getTermConnection().getConnection();
                    if (null != connection && connection.isTransactionOpen(terminal.getServerVersion())) {
                        hasOpenTransaction = true;
                        terminalLableSB.append(terminal.getPartLabel());
                        terminalLableSB.append(System.lineSeparator());
                    }
                } catch (SQLException exception) {
                    MPPDBIDELoggerUtility.error("Get terminal status exception", exception);
                    MPPDBIDEDialogs.generateDSErrorDialog(
                            MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_OPERATION_EXCEPTION_TITLE),
                            MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_RESET_BUTTIONS_EXCEPTION),
                            exception.getMessage(), exception);
                    return true;
                }
            }
        }
        if (hasOpenTransaction) {
            TransactionDialog transactionDialog = new TransactionDialog(String.valueOf(terminalLableSB),
                    Display.getDefault().getActiveShell());
            transactionDialog.open();
        }
        return hasOpenTransaction;
    }

    /**
     * Sets the up all SQL terminal transaction status.
     *
     * @param status the new up all SQL terminal transaction status
     */
    private void setupAllSQLTerminalTransactionStatus(boolean status) {
        List<IAutoSaveObject> list = UIElement.getInstance().getAllOpenTerminals();
        Iterator<IAutoSaveObject> iter = list.iterator();
        while (iter.hasNext()) {
            IAutoSaveObject autoSaveObject = iter.next();
            if (autoSaveObject instanceof SQLTerminal) {
                SQLTerminal terminal = (SQLTerminal) autoSaveObject;
                terminal.changeAutoComitStatus(status);
            }
        }
    }
}