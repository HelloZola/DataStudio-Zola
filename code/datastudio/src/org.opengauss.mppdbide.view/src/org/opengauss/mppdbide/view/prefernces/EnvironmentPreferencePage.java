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

package org.opengauss.mppdbide.view.prefernces;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class EnvironmentPreferencePage.
 *
 * @since 3.0.0
 */
public class EnvironmentPreferencePage extends PreferencePage {

    /**
     * Instantiates a new environment preference page.
     */
    public EnvironmentPreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.ENVIRONMENT_NODE));
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        Label lable = new Label(parent, SWT.NONE);
        lable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
        lable.setText(MessageConfigLoader.getProperty(IMessagesConstants.ENVIRONMENT_NODE_LABLE));
        return null;
    }

    /**
     * Creates the control.
     *
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {

        super.createControl(parent);
        getDefaultsButton().setVisible(false);
        getApplyButton().setVisible(false);

    }

}
