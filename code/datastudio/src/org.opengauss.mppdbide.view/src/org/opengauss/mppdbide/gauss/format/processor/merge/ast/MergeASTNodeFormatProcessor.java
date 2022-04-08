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

package org.opengauss.mppdbide.gauss.format.processor.merge.ast;

import org.opengauss.mppdbide.gauss.format.processor.listimpl.AddEmptyPreTextFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.NewlineFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.NewlineWithOffsetSqlNodeFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.NoPreTextFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.listimpl.WhereListFormatProcessorListener;
import org.opengauss.mppdbide.gauss.format.processor.select.AbstractASTNodeFormatProcessor;
import org.opengauss.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.merge.TMergeASTNode;

/**
 * Title: WithASTNodeFormatProcessor
 *
 * @since 3.0.0
 */
public class MergeASTNodeFormatProcessor extends AbstractASTNodeFormatProcessor {
    /**
     * adds listeners to the node before process
     */
    public void beforeProcess(TBasicASTNode node) {
        super.beforeProcess(node);
        // add listener to the startnode
        TMergeASTNode mergeNode = getMergeAstNode(node);

        addFormatProcessListener(mergeNode.getSrcTable(), new AddEmptyPreTextFormatProcessorListener());

        addFormatProcessListener(mergeNode.getUsing(), new NewlineWithOffsetSqlNodeFormatProcessorListener());

        addFormatProcessListener(mergeNode.getDestTable(), new AddEmptyPreTextFormatProcessorListener());

        addFormatProcessListener(mergeNode.getOn(), new NewlineFormatProcessorListener());

        addFormatProcessListener(mergeNode.getOnEndBracket(), new NoPreTextFormatProcessorListener());

        addFormatProcessListener(node.getItemList(), new WhereListFormatProcessorListener());
    }

    private TMergeASTNode getMergeAstNode(TBasicASTNode node) {
        if (node instanceof TMergeASTNode) {
            return (TMergeASTNode) node;
        }
        throw new GaussDBSQLParserException("Unable to typecase the statement");
    }
}
