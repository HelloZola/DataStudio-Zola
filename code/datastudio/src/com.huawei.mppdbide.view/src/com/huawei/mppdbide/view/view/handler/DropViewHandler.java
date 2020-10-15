/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.view.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import com.huawei.mppdbide.bl.serverdatacache.IViewMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.view.handler.ViewWorkerJob.VIEWOPTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropViewHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DropViewHandler {

    /**
     * Execute.
     *
     * @param isCascade the is cascade
     */
    @Execute
    public void execute(@Optional @Named("iscascade") String isCascade) {
        IViewMetaData view = IHandlerUtilities.getSelectedIViewObject();
        if (null != view) {
            String name = view.getViewPathQualifiedName();

            String title = MessageConfigLoader.getProperty(IMessagesConstants.DROP_VIEW_CONFIRM_TITLE);
            String msg = "";
            boolean isAppendCascade = "true".equals(isCascade);

            if (isAppendCascade) {
                msg = MessageConfigLoader.getProperty(IMessagesConstants.DROP_VIEW_CASCADE_CONFIRM_MSG, name);
            } else {
                msg = MessageConfigLoader.getProperty(IMessagesConstants.DROP_VIEW_CONFIRM_MSG, name);
            }

            int userChoice = MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true, title, msg);

            if (UIConstants.OK_ID != userChoice) {
                return;
            }

            ViewWorkerJob job = new ViewWorkerJob("Drop View", VIEWOPTYPE.DROP_VIEW, "", view, isCascade, this);

            job.schedule();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        IViewMetaData viewgroup = IHandlerUtilities.getSelectedIViewObject();
        if (null != viewgroup) {
            return viewgroup.isDbConnected();
        }
        return false;
    }

}
