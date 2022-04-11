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

package org.opengauss.mppdbide.view.visualexplainplanpropertyview;

import java.util.ArrayList;

import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.presentation.IResultDisplayUIManager;
import org.opengauss.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import org.opengauss.mppdbide.presentation.resultsetif.IResultConfig;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.AbstractPropertiesContext;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanPropertiesContext.
 *
 * @since 3.0.0
 */
public class VisualExplainPlanPropertiesContext extends AbstractPropertiesContext {
    private VisualExplainPlanNodePropertiesUIManager uiManager;
    private VisualExplainPlanPropertyResultConfig resultConfig;

    /**
     * Instantiates a new visual explain plan properties context.
     *
     * @param core2 the core 2
     * @param uiManager2 the ui manager 2
     */
    public VisualExplainPlanPropertiesContext(PropertyHandlerCore core2,
            VisualExplainPlanNodePropertiesUIManager uiManager2) {
        super(core2);
        this.uiManager = uiManager2;

    }

    /**
     * Gets the context name.
     *
     * @return the context name
     */
    @Override
    public String getContextName() {
        return MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_JOB_RESULT_DETAILS);
    }

    /**
     * Gets the result config.
     *
     * @return the result config
     */
    @Override
    public IResultConfig getResultConfig() {
        if (null == this.resultConfig) {
            this.resultConfig = new VisualExplainPlanPropertyResultConfig();
        }

        return this.resultConfig;
    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    @Override
    public IResultDisplayUIManager getResultDisplayUIManager() {
        return this.uiManager;
    }

    /**
     * Handle execution exception.
     *
     * @param exception the exception
     */
    @Override
    public void handleExecutionException(Exception exception) {
        MPPDBIDELoggerUtility.none("handle Exception for visual explain plan context");
    }

    /**
     * gets the input values
     */
    @Override
    public ArrayList<DefaultParameter> getInputValues() {
        return null;
    }

}
