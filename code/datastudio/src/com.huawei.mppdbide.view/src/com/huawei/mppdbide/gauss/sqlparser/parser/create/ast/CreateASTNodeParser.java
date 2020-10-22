/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.create.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.condition.ConditionBreakIf;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.select.nodelist.SelectResultListParser;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.create.TCreateASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * Title: CreateASTNodeParser Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
 */
public class CreateASTNodeParser extends BasicASTNodeParser {

    /**
     * Gets the keyword token.
     *
     * @param listIterator the list iterator
     * @return the keyword token
     */
    protected TSqlNode getKeywordToken(ListIterator<ISQLTokenData> listIterator) {

        return ParserUtils.handleToken(listIterator, Arrays.asList(SQLFoldingConstants.SQL_CREATE,
                SQLFoldingConstants.SQL_FUNCTION, SQLFoldingConstants.SQL_PROCEDURE));
    }

    /**
     * Prepare AST other stmt object.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTOtherStmtObject(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {
        TCreateASTNode tCreateASTNode = (TCreateASTNode) fromAstNode;

        if (!(SQLFoldingConstants.SQL_FUNCTION.equalsIgnoreCase(fromAstNode.getKeywordNode().getNodeText())
                || SQLFoldingConstants.SQL_PROCEDURE.equalsIgnoreCase(fromAstNode.getKeywordNode().getNodeText()))) {
            Set<String> interMediateText = new HashSet<String>(Arrays.asList(SQLFoldingConstants.SQL_PROCEDURE,
                    SQLFoldingConstants.SQL_FUNCTION, SQLFoldingConstants.SQL_PACKAGE));

            TExpression parseAndGetExpression = NodeExpressionConverter.parseAndGetExpression(listIterator,
                    interMediateText);

            tCreateASTNode.setIntermediateText(parseAndGetExpression);

            TSqlNode procName = ParserUtils.handleToken(listIterator, Arrays.asList(SQLFoldingConstants.SQL_PROCEDURE,
                    SQLFoldingConstants.SQL_FUNCTION, SQLFoldingConstants.SQL_PACKAGE));

            tCreateASTNode.setProcOrFunction(procName);
        }

        Set<String> endTableName = new HashSet<String>(Arrays.asList(SQLFoldingConstants.SQL_BRACKET_START));

        endTableName.addAll(getKeywordList());

        tCreateASTNode.setProcOrFuncName(
                NodeExpressionConverter.parseAndGetExpression(listIterator, endTableName, new ConditionBreakIf() {
                    @Override
                    public boolean isBreakCondition(ISQLTokenData next, ListIterator<ISQLTokenData> listIterator) {
                        if (null != next.getSubTokenBean()) {
                            return true;
                        }
                        return false;
                    }
                }));

        TSqlNode startBracket = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_BRACKET_START);
        tCreateASTNode.setProcStartBracket(startBracket);

    }

    /**
     * Prepare AST after list item.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTAfterListItem(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {
        TCreateASTNode tCreateASTNode = (TCreateASTNode) fromAstNode;
        TSqlNode endBracket = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_BRACKET_END);

        tCreateASTNode.setProcEndBracket(endBracket);

        tCreateASTNode
                .setCommonExpression(NodeExpressionConverter.parseAndGetExpression(listIterator, getKeywordList()));

    }

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TCreateASTNode();
    }

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_CREATE;
    }

    /**
     * Gets the node list parser.
     *
     * @return the node list parser
     */
    @Override
    public AbstractNodeListParser getNodeListParser() {
        Set<String> asList = new HashSet<>(Arrays.asList(SQLFoldingConstants.SQL_BRACKET_END));
        asList.addAll(getKeywordList());
        return new SelectResultListParser(asList);
    }

}