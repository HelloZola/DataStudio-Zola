/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser.select.ast;

import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.TBasicASTNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.astnode.select.TForASTNode;

/**
 * 
 * Title: ForASTNodeParser
 * 
 * Description: The Class ForASTNodeParser.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author aWX619007
 * @version [DataStudio 6.5.1, Dec 2, 2019]
 * @since Dec 2, 2019
 */
public class ForASTNodeParser extends LimitASTNodeParser {

    /**
     * Gets the keyword token str.
     *
     * @return the keyword token str
     */
    @Override
    public String getKeywordTokenStr() {
        return SQLFoldingConstants.SQL_KEYWORD_FOR;
    }

    /**
     * Gets the AST node bean.
     *
     * @return the AST node bean
     */
    @Override
    public TBasicASTNode getASTNodeBean() {
        return new TForASTNode();
    }
}
