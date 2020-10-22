/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.exportdata;

import java.text.ParseException;
import java.util.ArrayList;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.utils.CustomStringUtility;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportExportDataCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ImportExportDataCore extends AbstractImportExportDataCore {

    private ImportExportData importExportData;
    private ExportCursorQueryExecuter exportCursorExecuter;

    /**
     * Instantiates a new import export data core.
     *
     * @param obj the obj
     * @param clmList the clm list
     * @param executedQuery the executed query
     * @param terminalID the terminal ID
     * @param querySubmitTime the query submit time
     */
    public ImportExportDataCore(ServerObject obj, ArrayList<String> clmList, String executedQuery, String terminalID,
            String querySubmitTime) {
        super(obj, clmList, executedQuery, terminalID, querySubmitTime);
        importExportData = new ImportExportData(obj);
    }

    /**
     * Compose excel query.
     *
     * @return the string
     */
    public String composeExcelQuery() {
        StringBuffer queryBuff = new StringBuffer();
        if (importExportServerObj instanceof TableMetaData) {
            importExportData.composeExcelQuery(queryBuff, importExportoptions);
        } else {
            composeExcelQuery(queryBuff);
        }
        return queryBuff.toString();
    }

    /**
     * Append delimiter option.
     *
     * @param queryBuff the query buff
     */
    protected void appendDelimiterOption(StringBuffer queryBuff) {
        super.appendDelimiterOption(queryBuff);
        if (importExportServerObj instanceof TableMetaData) {
            importExportData.appendQueryFormatForCsvFormat(queryBuff);
        }
    }

    /**
     * Append tbl name or executed query.
     *
     * @param queryBuff the query buff
     */
    protected void appendTblNameOrExecutedQuery(StringBuffer queryBuff) {
        super.appendTblNameOrExecutedQuery(queryBuff);
        if (importExportServerObj instanceof TableMetaData) {
            importExportData.appaendTblNameOrExecutedQuery(queryBuff, importExportoptions);
        }
    }

    /**
     * Cancel import export operation.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void cancelImportExportOperation() throws DatabaseCriticalException, DatabaseOperationException {
        super.cancelImportExportOperation();
        if (isOLAPDB() && exportCursorExecuter != null) {
            exportCursorExecuter.setCancelFlag(true);
        }
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        String fileName = super.getFileName();
        if (importExportServerObj instanceof TableMetaData) {
            fileName = importExportData.getFileName();
        }
        return  CustomStringUtility.sanitizeExportFileName(fileName);
    }

    /**
     * Gets the safe sheet name.
     *
     * @return the safe sheet name
     */
    public String getSafeSheetName() {
        String fileName = super.getSafeSheetName();
        if (importExportServerObj instanceof TableMetaData) {
            fileName = importExportData.getFileName();
        }
        return CustomStringUtility.sanitizeExcelSheetName(fileName);
    }

    /**
     * Gets the display table name.
     *
     * @return the display table name
     */
    public String getDisplayTableName() {
        super.getDisplayTableName();
        if (importExportServerObj instanceof TableMetaData) {
            displayTableName = importExportData.getDisplayTableName();
        }
        return displayTableName;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        displayTableName = super.getDisplayName();
        if (importExportServerObj instanceof TableMetaData) {
            displayTableName = importExportData.getDisplayName();
        }
        return displayTableName;
    }

    /**
     * Gets the progress label name.
     *
     * @return the progress label name
     */
    public String getProgressLabelName() {
        displayTableName = super.getProgressLabelName();
        if (importExportServerObj instanceof TableMetaData) {
            displayTableName = importExportData.getProgressLabelName();
        }
        return displayTableName;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        Database database = super.getDatabase();
        if (importExportServerObj instanceof TableMetaData) {
            database = importExportData.getDatabase();
        }
        return database;
    }

    @Override
    protected long getExportExcelTotalRows(DBConnection currentConnection, boolean isFuncProcExport)
            throws ParseException, MPPDBIDEException {
        String queryForExport = composeExcelQuery();
        exportCursorExecuter = new ExportCursorQueryExecuter(queryForExport, currentConnection);
        long totalRows = exportCursorExecuter.exportExcelData(visitor, isFuncProcExport);
        return totalRows;
    }
}