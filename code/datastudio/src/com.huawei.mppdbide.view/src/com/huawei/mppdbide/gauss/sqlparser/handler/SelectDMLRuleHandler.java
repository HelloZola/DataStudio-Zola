/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.handler;

import java.util.List;

import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.DMLParamScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.bean.pos.RuleBean;
import com.huawei.mppdbide.gauss.sqlparser.bean.pos.SQLScriptElement;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;

/**
 * Title: SelectDMLRuleHandler Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Aug-2019]
 * @since 19-Aug-2019
 */
public class SelectDMLRuleHandler extends AbstractDMLRuleHandler {

    /**
     * Checks if is nested.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is nested
     */
    public boolean isNested(ScriptBlockInfo curBlock, IToken token) {

        String lastKnownToken = curBlock.getLastKnownToken();

        if (curBlock.isNested()
                && (isSelectStmtNested(token, lastKnownToken) || isCaseStmtNested(token) || isUnionStmtNested(token))) {

            return true;
        }

        return false;
    }

    /**
     * Checks if is union stmt nested.
     *
     * @param token the token
     * @return true, if is union stmt nested
     */
    protected boolean isUnionStmtNested(IToken token) {
        return null != getSQLToken(token) && getSQLToken(token).getType() == SQLTokenConstants.T_SQL_KEYWORK_UNION;
    }

    private boolean isSelectStmtNested(IToken token, String lastKnownToken) {
        return getSQLToken(token).getType() == SQLTokenConstants.T_SQL_DML_SELECT
                && (SQLFoldingConstants.SQL_BRACKET_START.equalsIgnoreCase(lastKnownToken)
                        || SQLFoldingConstants.SQL_KEYWORD_UNION.equalsIgnoreCase(lastKnownToken)
                        || SQLFoldingConstants.SQL_KEYWORD_INTERSECT.equalsIgnoreCase(lastKnownToken)
                        || SQLFoldingConstants.SQL_KEYWORD_EXCEPT.equalsIgnoreCase(lastKnownToken)
                        || SQLFoldingConstants.SQL_KEYWORD_MINUS.equalsIgnoreCase(lastKnownToken));
    }

    /**
     * Handle stmt custom end.
     *
     * @param curBlock the cur block
     * @param token the token
     * @param lRuleBean the l rule bean
     * @param lretList the lret list
     * @param ruleManager the rule manager
     */
    protected void handleStmtCustomEnd(ScriptBlockInfo curBlock, SQLToken token, RuleBean lRuleBean,
            List<SQLScriptElement> lretList, ISQLTokenData ruleManager) {
        if (SQLFoldingConstants.SQL_KEYWORD_UNION.equalsIgnoreCase(curBlock.getCurrentKnownToken())
                || SQLFoldingConstants.SQL_KEYWORD_INTERSECT.equalsIgnoreCase(curBlock.getCurrentKnownToken())
                || SQLFoldingConstants.SQL_KEYWORD_EXCEPT.equalsIgnoreCase(curBlock.getCurrentKnownToken())
                || SQLFoldingConstants.SQL_KEYWORD_MINUS.equalsIgnoreCase(curBlock.getCurrentKnownToken())
                || isInMergeStmt(curBlock, token)) {
            unreadAndEndScriptBlock(curBlock, lRuleBean, lretList, ruleManager);
        }
    }

    /**
     * Checks if is rule handler valid.
     *
     * @param currentRuleHandler the current rule handler
     * @param curBlock the cur block
     * @return true, if is rule handler valid
     */
    public boolean isRuleHandlerValid(RuleHandlerIf currentRuleHandler, ScriptBlockInfo curBlock, SQLToken token) {

        if (super.isRuleHandlerValid(currentRuleHandler, curBlock, token)) {
            if (currentRuleHandler instanceof UpdateDMLRuleHandler && ("for".equalsIgnoreCase(curBlock.getLastToken())
                    || "key".equalsIgnoreCase(curBlock.getLastToken()))) {
                return false;
            }
            return true;
        } else {
            return false;
        }

    }

    /**
     * Checks if is stop parent script block.
     *
     * @param curBlock the cur block
     * @param token the token
     * @return true, if is stop parent script block
     */
    public boolean isStopParentScriptBlock(ScriptBlockInfo curBlock, IToken token) {

        if (curBlock.getAbstractRuleHandler() instanceof AbstractDMLRuleHandler && null != curBlock.getParent()) {

            if (curBlock.getParent().getAbstractRuleHandler() instanceof AbstractDMLRuleHandler) {
                return true;
            }

            else if (curBlock.getParent().getAbstractRuleHandler() instanceof AbstractCreateHandler) {

                AbstractCreateStmt<DMLParamScriptBlockInfo> abstractCreateStmt = ((AbstractCreateHandler) curBlock
                        .getParent().getAbstractRuleHandler()).getAbstractCreateStmt(curBlock.getParent());
                if (abstractCreateStmt instanceof TableCreateStmt || abstractCreateStmt instanceof ViewCreateStmt) {
                    return true;
                }
            }

            return false;

        }

        return false;
    }

    /**
     * Checks if is end block by this token.
     *
     * @param token the token
     * @param curBlock the cur block
     * @return true, if is end block by this token
     */
    protected boolean isEndBlockByThisToken(IToken token, ScriptBlockInfo curBlock) {

        if ((null != curBlock.getParent()
                && curBlock.getParent().getAbstractRuleHandler() instanceof SQLForLoopRuleHandler
                && getSQLToken(token).getType() == SQLTokenConstants.T_SQL_LOOP)
                || (getSQLToken(token).getType() == SQLTokenConstants.T_SQL_LOOP
                        && curBlock.getRecentNewLineCount() >= 1)) {
            return true;
        }

        return false;
    }

}
