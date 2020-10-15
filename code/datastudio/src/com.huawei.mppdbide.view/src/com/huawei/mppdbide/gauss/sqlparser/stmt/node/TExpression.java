/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.stmt.node;

import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.gauss.sqlparser.parser.utils.ExpressionTypeEnum;

/**
 * Title: TExpression Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
 */
public class TExpression extends TParseTreeNode {

    private ExpressionTypeEnum expressionType = ExpressionTypeEnum.NORMAL;

    private List<TExpressionNode> expList = new ArrayList<TExpressionNode>();

    private boolean containStmt = false;

    private boolean directExpression = false;

    private Boolean addSpaceForCustomStmt = Boolean.FALSE;

    /**
     * Instantiates a new t expression.
     */
    public TExpression() {

    }

    /**
     * Instantiates a new t expression.
     *
     * @param expType the exp type
     */
    public TExpression(ExpressionTypeEnum expType) {
        this.expressionType = expType;
    }

    /**
     * Gets the exp list.
     *
     * @return the exp list
     */
    public List<TExpressionNode> getExpList() {
        return expList;
    }

    /**
     * Sets the exp list.
     *
     * @param expList the new exp list
     */
    public void setExpList(List<TExpressionNode> expList) {
        this.expList = expList;
    }

    /**
     * Adds the expression node.
     *
     * @param exp the exp
     */
    public void addExpressionNode(TExpressionNode exp) {
        this.expList.add(exp);
    }

    /**
     * Checks if is contain stmt.
     *
     * @return true, if is contain stmt
     */
    public boolean isContainStmt() {
        return containStmt;
    }

    /**
     * Sets the contain stmt.
     *
     * @param containStmt the new contain stmt
     */
    public void setContainStmt(boolean containStmt) {
        this.containStmt = containStmt;
    }

    /**
     * Gets the expression type.
     *
     * @return the expression type
     */
    public ExpressionTypeEnum getExpressionType() {
        return expressionType;
    }

    /**
     * Sets the expression type.
     *
     * @param expressionType the new expression type
     */
    public void setExpressionType(ExpressionTypeEnum expressionType) {
        this.expressionType = expressionType;
    }

    /**
     * Checks if is direct expression.
     *
     * @return true, if is direct expression
     */
    public boolean isDirectExpression() {
        return directExpression;
    }

    /**
     * Sets the direct expression.
     *
     * @param directExpression the new direct expression
     */
    public void setDirectExpression(boolean directExpression) {
        this.directExpression = directExpression;
    }

    /**
     * Gets the adds the space for custom stmt.
     *
     * @return the adds the space for custom stmt
     */
    public Boolean getAddSpaceForCustomStmt() {
        return addSpaceForCustomStmt;
    }

    /**
     * Sets the adds the space for custom stmt.
     *
     * @param addSpaceForCustomStmt the new adds the space for custom stmt
     */
    public void setAddSpaceForCustomStmt(Boolean addSpaceForCustomStmt) {
        this.addSpaceForCustomStmt = addSpaceForCustomStmt;
    }

    /**
     * Gets the start node.
     *
     * @return the start node
     */
    @Override
    public TParseTreeNode getStartNode() {
        return this;
    }

}
