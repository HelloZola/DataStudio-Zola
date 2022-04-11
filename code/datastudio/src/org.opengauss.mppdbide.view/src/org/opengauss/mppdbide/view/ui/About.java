/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.view.ui;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.view.core.AboutMPPDBIDEDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class About.
 *
 * @since 3.0.0
 */
public class About {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        AboutMPPDBIDEDialog aboutDialog = new AboutMPPDBIDEDialog(Display.getDefault().getActiveShell());
        aboutDialog.open();
    }

}
