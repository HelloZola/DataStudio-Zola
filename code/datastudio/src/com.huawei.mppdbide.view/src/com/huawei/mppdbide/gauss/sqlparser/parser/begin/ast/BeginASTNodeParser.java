/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.begin.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserUtils;
import com.huawei.mppdbide.gauss.sqlparser.parser.ast.BasicASTNodeParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.begin.nodelist.WhenItemListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.AbstractNodeListParser;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.FullStatementConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeExpressionConverter;
import com.huawei.mppdbide.gauss.sqlparser.parser.nodelist.NodeListParserConverter;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.begin.TBeginASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;

/**
 * Title: BeginASTNodeParser
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 30-Dec-2019]
 * @since 30-Dec-2019
 */

public class BeginASTNodeParser extends BasicASTNodeParser {

    /**
     * Prepare AST stmt object.
     *
     * @param listIterator the list iterator
     * @return the t begin AST node
     */
    @Override
    public TBeginASTNode prepareASTStmtObject(ListIterator<ISQLTokenData> listIterator) {

        TBeginASTNode beginAstNode = new TBeginASTNode();

        TSqlNode lBegin = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_KEYWORK_BEGIN);
        beginAstNode.setKeywordNode(lBegin);

        Set<String> newKeyList = new HashSet<String>();
        newKeyList.add(SQLFoldingConstants.SQL_KEYWORK_END);
        newKeyList.add(SQLFoldingConstants.SQL_KEYWORK_EXCEPTION);

        TFullStmt fullStmt = FullStatementConverter.parseAndGetFullStmt(listIterator, newKeyList);
        beginAstNode.setFullStmt(fullStmt);

        TSqlNode exception = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_KEYWORK_EXCEPTION);
        beginAstNode.setException(exception);

        if (null != exception) {
            // handle when list

            Set<String> lineBreakSet = new HashSet<String>(Arrays.asList(SQLFoldingConstants.SQL_KEYWORK_END));

            WhenItemListParser lWhenItemListParser = new WhenItemListParser(lineBreakSet);

            NodeListParserConverter.handleSelectList(listIterator, lWhenItemListParser);
            beginAstNode.setExceptionWhenList(lWhenItemListParser.getItemList());
        }

        TSqlNode lend = ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_KEYWORK_END);
        beginAstNode.setEnd(lend);

        TExpression expression = NodeExpressionConverter.parseAndGetExpression(listIterator, getKeywordList());
        beginAstNode.setEndExpression(expression);

        return beginAstNode;
    }

    /**
     * Prepare AST other stmt object.
     *
     * @param listIterator the list iterator
     * @param fromAstNode the from ast node
     */
    protected void prepareASTOtherStmtObject(ListIterator<ISQLTokenData> listIterator, TBasicASTNode fromAstNode) {

        TBeginASTNode beginAstNode = (TBeginASTNode) fromAstNode;

        Set<String> lineBreakSet = new HashSet<String>(Arrays.asList(SQLFoldingConstants.SQL_KEYWORK_END));

        beginAstNode.setFullStmt(FullStatementConverter.parseAndGetFullStmt(listIterator, lineBreakSet));

        // code to parse all the statements

        beginAstNode.setEnd(ParserUtils.handleToken(listIterator, SQLFoldingConstants.SQL_KEYWORK_END));

        beginAstNode.setEndExpression(NodeExpressionConverter.parseAndGetExpression(listIterator, null));
    }

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TBeginASTNode();
    }

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORK_BEGIN;
    }

    /**
     * Gets the node list parser.
     *
     * @return the node list parser
     */
    @Override
    public AbstractNodeListParser getNodeListParser() {
        return null;
    }

}
