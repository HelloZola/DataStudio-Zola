/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor.update.ast;

import com.huawei.mppdbide.gauss.format.processor.listimpl.ResultListFormatProcessorListener;
import com.huawei.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;

/**
 * 
 * Title: UpdateSetASTNodeFormatProcessor
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class UpdateSetASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {

    /**
     * Before process.
     *
     * @param node the node
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode
        addFormatProcessListener(node.getItemList(), new ResultListFormatProcessorListener());

    }

}
