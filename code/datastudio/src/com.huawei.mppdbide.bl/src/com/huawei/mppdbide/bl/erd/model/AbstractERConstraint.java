/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.erd.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Title: AbstractERConstraint
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

public abstract class AbstractERConstraint {
    /** 
     * The cons type. 
     */
    protected String consType;
    
    /**
     *  the column id List. 
     */
    protected List<Long> keyColIndex;

    /**
     * Gets the cons type.
     *
     * @return the cons type
     */
    public String getConsType() {
        return consType;
    }

    /**
     * set the cons type.
     *
     * @param consType the new cons type
     */
    public void setConsType(String consType) {
        this.consType = consType;
    }

    /**
     * Sets the constraint info.
     *
     * @param rs the rs
     * @param constraints the constraints
     * @throws SQLException the SQL exception
     */
    public abstract void setConstraintInfo(ResultSet rs, List<AbstractERConstraint> constraints) throws SQLException;

    /**
     * Gets the keys type full name.
     *
     * @param keyType the key type
     * @return the keys type full name
     */
    public static String getKeysTypeFullName(String keyType) {
        if ("P".equalsIgnoreCase(keyType)) {
            return IERNodeConstants.PRIMARY_KEY;
        } else if ("U".equalsIgnoreCase(keyType)) {
            return IERNodeConstants.UNIQUE_KEY;
        } else {
            return IERNodeConstants.FOREIGN_KEY;
        }
    }

    /**
     * Gets the Key columns index.
     *
     * @return the Key columns index.
     */
    public List<Long> getKeyColIndex() {
        return keyColIndex;
    }

    /**
     * Sets the Key columns index.
     *
     * @param keyColIndex the new Key columns index.
     */
    public void setKeyColIndex(List<Long> keyColIndex) {
        this.keyColIndex = keyColIndex;
    }
    
}
