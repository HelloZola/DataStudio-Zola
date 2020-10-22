/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.synonym.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.synonym.olap.InitializeCreateSynonymWorker;

/**
 * 
 * Title: Class
 * 
 * Description: The Class CreateSynonymHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author gWX773294
 * @version
 * @since Nov 5, 2019
 */
public class CreateSynonymHandler {
    /**
     * Execute.
     *
     * @param shell the shell
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Execute
    public void execute(final Shell shell) throws MPPDBIDEException {

        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (null != obj) {
            if (obj instanceof SynonymObjectGroup) {
                InitializeCreateSynonymWorker createSynonymWorker = new InitializeCreateSynonymWorker(shell,
                        IMessagesConstants.CREATE_NEW_SYNONYM, MPPDBIDEConstants.CANCELABLEJOB,
                        IMessagesConstants.CREATE_NEW_SYNONYM);
                createSynonymWorker.schedule();
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
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj == null) {
            return false;
        }
        if (obj instanceof SynonymObjectGroup) {
            return true;
        }
        return false;
    }
}