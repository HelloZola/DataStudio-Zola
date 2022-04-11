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

package org.opengauss.mppdbide.gauss.format.processor.listimpl;

import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.sqlparser.exception.GaussDBSQLParserException;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: NewlineWithOffsetSqlNodeFormatProcessorListener
 *
 * @since 3.0.0
 */
public class NewlineWithOffsetSqlNodeFormatProcessorListener extends NewlineFormatProcessorListener {
    /**
     * Format process.
     *
     * @param nextNode the next node
     * @param options the options
     * @param pData the data
     */
    public void formatProcess(TParseTreeNode nextNode, FmtOptionsIf options, OptionsProcessData pData) {
        super.formatProcess(nextNode, options, pData);
        pData.addOffSet(getTSqlNode(nextNode).getNodeText().length());
    }

    private TSqlNode getTSqlNode(TParseTreeNode nextNode) {
        if (!(nextNode instanceof TSqlNode)) {
            throw new GaussDBSQLParserException("Unable to Cast the AST Node");
        }
        return (TSqlNode) nextNode;
    }
}