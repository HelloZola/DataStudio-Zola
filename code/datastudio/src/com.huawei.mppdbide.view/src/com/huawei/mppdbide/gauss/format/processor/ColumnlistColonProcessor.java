/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

import java.util.List;

import com.huawei.mppdbide.gauss.format.consts.ListItemOptionsEnum;
import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.option.OptionsProcessData;
import com.huawei.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;

/**
 * Title: ColumnlistColonProcessor Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, Dec 5, 2019]
 * @since Dec 5, 2019
 */

public class ColumnlistColonProcessor extends ColumnlistCommaProcessor {
    /**
     * handle one item per line
     */
    protected int handleOneItemPerLine(FmtOptionsIf options, HandleOneItemPerLineParameter parameterObject,
            TListItem parseTreeNode) {
        int runningSize = parameterObject.getRunningSize();
        if (null != getSeperator(parseTreeNode) && !parameterObject.isLastIndex()) {
            ProcessorUtils.addNewLineAfter(getSeperator(parseTreeNode), parameterObject.getOffset() + 1, options);
            runningSize = parameterObject.getOffset() + 1;

        }
        return runningSize;
    }

    /**
     * return seperator for parseTree node
     * 
     * @param parseTreeNode list item to which seperatoe to handle
     * @return returns the node which is seperator
     */
    protected TParseTreeNode getSeperator(TListItem parseTreeNode) {
        return null != parseTreeNode.getSeperator() ? parseTreeNode.getSeperator()
                : (null != parseTreeNode.getEndNode() ? parseTreeNode.getEndNode() : parseTreeNode.getItemListNode());
    }

    /**
     * return true if there is only one item per line
     */
    protected boolean isOneItemPerLine(ListItemOptionsEnum itemOption, TListItem parseTreeNode) {
        return ListItemOptionsEnum.ONEITEMPERLINE == itemOption;
    }

    /**
     * Gets the item option.
     *
     * @param columns the columns
     * @param options the options
     * @param pData the data
     * @param expContainStmt the exp contain stmt
     * @return the item option
     */
    protected ListItemOptionsEnum getItemOption(TParseTreeNodeList<TListItem> columns, FmtOptionsIf options,
            OptionsProcessData pData, boolean expContainStmt) {
        return ListItemOptionsEnum.ONEITEMPERLINE;
    }

    /**
     * return true is columns are aligned
     * 
     * @param options the options
     * @param pData the data
     * @param parameterObject combined object to pass the parameters
     * @param itemOption the item option
     * @return true is columns are aligned
     */
    protected boolean isAlignColumns(FmtOptionsIf options, OptionsProcessData pData,
            AlignColumnsParameter parameterObject, ListItemOptionsEnum itemOption) {
        return options.isAlignDeclaration();
    }

    /**
     * Gets the item size.
     *
     * @param runningSize the running size
     * @param pDataClone the data clone
     * @param expContainStmt the exp contain stmt
     * @param parseNode the parse node
     * @return the item size
     */
    protected int getItemSize(int runningSize, OptionsProcessData pDataClone, boolean expContainStmt,
            TParseTreeNode parseNode) {
        if (expContainStmt) {
            if (parseNode instanceof TExpression) {
                TExpression stmtExpression = (TExpression) parseNode;
                List<TExpressionNode> expList = stmtExpression.getExpList();
                for (TExpressionNode expNode : expList) {
                    TParseTreeNode customStmt = expNode.getCustomStmt();
                    if (null != customStmt && customStmt instanceof TCustomSqlStatement) {
                        return 0;
                    }
                }
            }
        }
        return pDataClone.getOffSet() - runningSize;
    }

}
