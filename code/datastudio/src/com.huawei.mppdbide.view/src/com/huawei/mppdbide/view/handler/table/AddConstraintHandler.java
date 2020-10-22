/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.TableOrientation;
import com.huawei.mppdbide.bl.serverdatacache.groups.ConstraintList;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.table.AddConstraint;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class AddConstraintHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class AddConstraintHandler {

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        ConstraintList constraintList = IHandlerUtilities.getSelectedConstraintGroup();
        if (constraintList != null) {
            AddConstraint addColumnDlg = new AddConstraint(shell, constraintList.getParent());
            if (constraintList.getParent().getOrientation() == TableOrientation.COLUMN) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader
                                .getProperty(IMessagesConstants.CONSTRAINT_INDEXES_HANDLER_NOT_SUPPORTED_TITLE),
                        MessageConfigLoader
                                .getProperty(IMessagesConstants.CONSTRAINT_HANDLER_FOR_COLUMN_ORIENTATION_BODY));
            } else {
                addColumnDlg.open();
            }
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ConstraintList constraintList = IHandlerUtilities.getSelectedConstraintGroup();

        if (null != constraintList && (constraintList.getParent() instanceof ForeignTable)) {
            return false;
        } else {
            return null != constraintList;
        }
    }
}