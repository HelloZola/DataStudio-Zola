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

package org.opengauss.mppdbide.view.handler.debug.ui;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import org.opengauss.mppdbide.view.ui.WindowBase;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class RefreshAll.
 *
 * @since 3.0.0
 */
public class RefreshAll implements Runnable {
    private String[] partIdArray = {"org.opengauss.mppdbide.part.id.stack",
            "org.opengauss.mppdbide.part.id.variable",
            "org.opengauss.mppdbide.part.id.breakpoint"};

    /**
     * Instantiates a new clear all.
     */
    public RefreshAll () {
    }

    /**
     * Run.
     */
    @Override
    public void run() {
        for (int i = 0; i < partIdArray.length; i++) {
            String partId = partIdArray[i];
            MPart part = UIElement.getInstance().getPartService().findPart(partId);
            if (!(part.getObject() instanceof WindowBase<?>)) {
                return;
            }
            WindowBase<?> windowBase = (WindowBase<?>) part.getObject();
            windowBase.refresh();
        }
    }
}