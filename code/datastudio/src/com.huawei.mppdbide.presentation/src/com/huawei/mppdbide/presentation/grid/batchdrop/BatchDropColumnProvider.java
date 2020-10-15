/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.grid.batchdrop;

import java.sql.Types;
import java.util.Comparator;

import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.resultset.ColumnValueSqlTypeComparator;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class BatchDropColumnProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class BatchDropColumnProvider implements IDSGridColumnProvider {
    private int columnCount = 5;
    private String[] columnNames;

    /**
     * Instantiates a new batch drop column provider.
     */
    public BatchDropColumnProvider() {
        this.columnNames = new String[] {
            MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_NATTABLE_COLUMN_TYPE),
            MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_NATTABLE_COLUMN_NAME),
            MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_NATTABLE_COLUMN_QUERY),
            MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_NATTABLE_COLUMN_STATUS),
            MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_NATTABLE_COLUMN_ERR_MSG)};
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public String[] getColumnNames() {
        return this.columnNames.clone();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return this.columnNames[columnIndex];
    }

    @Override
    public String getColumnDesc(int columnIndex) {
        return getColumnName(columnIndex);
    }

    @Override
    public int getColumnIndex(String columnLabel) {
        return 0;
    }

    @Override
    public Comparator<Object> getComparator(int columnIndex) {
        return new ColumnValueSqlTypeComparator<Object>(Types.VARCHAR);
    }

    @Override
    public int getColumnDatatype(int columnIndex) {
        return Types.VARCHAR;
    }

    @Override
    public String getColumnDataTypeName(int columnIndex) {
        return "varchar";
    }

    @Override
    public int getPrecision(int columnIndex) {
        return 0;
    }

    @Override
    public int getScale(int columnIndex) {
        return 0;
    }

    @Override
    public int getMaxLength(int columnIndex) {
        return 0;
    }

    @Override
    public String getDefaultValue(int i) {
        return null;
    }
}
