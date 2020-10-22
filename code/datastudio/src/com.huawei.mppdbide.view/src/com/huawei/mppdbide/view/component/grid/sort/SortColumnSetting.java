/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.sort;

import java.util.Arrays;

import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortColumnSetting.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SortColumnSetting {

    /**
     * The priority.
     */
    String priority; // convert the number to string and store

    /**
     * The column name.
     */
    String columnName;

    /**
     * The data type.
     */
    String dataType;

    /**
     * The order.
     */
    String order;

    /**
     * Instantiates a new sort column setting.
     */
    public SortColumnSetting() {
        this.priority = "0";
        setDefaultSettings();
    }

    private void setDefaultSettings() {
        this.columnName = MessageConfigLoader.getProperty(IMessagesConstants.COMBO_TEXT_SORT_COLUMN);
        this.dataType = "";
        this.order = MessageConfigLoader.getProperty(IMessagesConstants.COMBO_TEXT_SORT_OREDER);
    }

    /**
     * Instantiates a new sort column setting.
     *
     * @param priority the priority
     */
    public SortColumnSetting(String priority) {
        this.priority = priority;
        setDefaultSettings();
    }

    /**
     * Instantiates a new sort column setting.
     *
     * @param priority the priority
     * @param columnName the column name
     * @param dataType the data type
     * @param sortOrder the sort order
     */
    public SortColumnSetting(String priority, String columnName, String dataType, String sortOrder) {
        this.priority = priority;
        this.columnName = columnName;
        this.dataType = dataType;
        this.order = sortOrder;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Gets the column name.
     *
     * @return the column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets the column name.
     *
     * @param colName the col name
     * @param cols the cols
     * @param datatypes the datatypes
     */
    public void setColumnName(String colName, String[] cols, String[] datatypes) {
        this.columnName = colName;
        int index = Arrays.asList(cols).indexOf((String) columnName);
        if (index != -1) {
            this.setDataType(datatypes[index]);
            if (MessageConfigLoader.getProperty(IMessagesConstants.COMBO_TEXT_SORT_OREDER).equals(getSortOrder())) {
                // if no dir chosen, set ascending
                this.setSortOrder(MessageConfigLoader.getProperty(IMessagesConstants.COMBO_OPTION_ASCENDING));
            }
        }
    }

    /**
     * Gets the data type.
     *
     * @return the data type
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets the data type.
     *
     * @param dataType the new data type
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * Gets the sort order.
     *
     * @return the sort order
     */
    public String getSortOrder() {
        return order;
    }

    /**
     * Sets the sort order.
     *
     * @param sortOrder the new sort order
     */
    public void setSortOrder(String sortOrder) {
        this.order = sortOrder;
    }

    /**
     * Gets the sort direction enum from combo text.
     *
     * @param text the text
     * @return the sort direction enum from combo text
     */
    public static SortDirectionEnum getSortDirectionEnumFromComboText(String text) {
        SortDirectionEnum sortEnum = null;
        if (MessageConfigLoader.getProperty(IMessagesConstants.COMBO_OPTION_DESCENDING).equals(text)) {
            sortEnum = SortDirectionEnum.DESC;
        } else {
            sortEnum = SortDirectionEnum.ASC; // keep it default
        }
        return sortEnum;
    }

    /**
     * Gets the combo text from sort direction enum.
     *
     * @param dir the dir
     * @return the combo text from sort direction enum
     */
    public static String getComboTextFromSortDirectionEnum(SortDirectionEnum dir) {
        // return ascending index by default
        String text = MessageConfigLoader.getProperty(IMessagesConstants.COMBO_OPTION_ASCENDING);

        if (dir == SortDirectionEnum.DESC) {
            text = MessageConfigLoader.getProperty(IMessagesConstants.COMBO_OPTION_DESCENDING);
        }
        return text;
    }

    /**
     * Gets the priority text.
     *
     * @return the priority text
     */
    public String getPriorityText() {
        return String.valueOf(Integer.parseInt(this.priority) + 1);
    }

    /**
     * Reduce priority.
     */
    public void reducePriority() {
        this.priority = String.valueOf(Integer.parseInt(this.priority) - 1);
    }

}