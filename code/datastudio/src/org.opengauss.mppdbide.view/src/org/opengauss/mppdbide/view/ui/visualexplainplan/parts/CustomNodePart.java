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

package org.opengauss.mppdbide.view.ui.visualexplainplan.parts;

import org.eclipse.gef.zest.fx.parts.NodePart;

import javafx.scene.Group;

/**
 * Title: CustomNodePart
 * 
 * Description:CustomNodePart
 * 
 * @since 3.0.0
 */
public class CustomNodePart extends NodePart {

    /**
     * Do create visual.
     *
     * @return the group
     */
    @Override
    protected Group doCreateVisual() {

       return null;
    }

    private Group addSpecialComponensts() {
       return null;
    }

    private String getTwoSpace() {
        return "  ";
    }

}
