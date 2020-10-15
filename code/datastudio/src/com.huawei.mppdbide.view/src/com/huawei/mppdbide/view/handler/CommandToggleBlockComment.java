/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;

import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandToggleBlockComment.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CommandToggleBlockComment {

    /**
     * Execute.
     *
     * @param activePart the active part
     */
    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_PART) @Optional MPart activePart) {
        if (null == activePart) {
            return;
        }

        Object partObject = activePart.getObject();
        if (partObject instanceof PLSourceEditor) {
            ((PLSourceEditor) partObject).getSourceEditorCore().toggleBlockComment();
        } else if (partObject instanceof SQLTerminal) {
            ((SQLTerminal) partObject).getTerminalCore().toggleBlockComment();
        }
    }

    /**
     * Can execute.
     *
     * @param activePart the active part
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute(@Named(IServiceConstants.ACTIVE_PART) @Optional MPart activePart) {
        if (null == activePart) {
            return false;
        }

        Object partObjt = activePart.getObject();
        if (partObjt instanceof SQLTerminal) {
            SQLTerminal terminal = (SQLTerminal) partObjt;
            return terminal.getTerminalCore().getSourceViewer().isEditable();
        } else if (partObjt instanceof PLSourceEditor) {
            PLSourceEditor sourceEditor = (PLSourceEditor) partObjt;
            return sourceEditor.getSourceEditorCore() != null
                    && sourceEditor.getSourceEditorCore().getSourceViewer().isEditable();
        }
        return false;
    }
}
