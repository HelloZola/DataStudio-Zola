/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.importexportdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.export.ExportManager;
import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportExportDataExecuter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ImportExportDataExecuter {

    /**
     * Export data.
     *
     * @param path the path
     * @param sql the sql
     * @param conn the conn
     * @param encoding the encoding
     * @param fileFormat the file format
     * @return the long
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public long exportData(Path path, String sql, DBConnection conn, String encoding, String fileFormat)
            throws MPPDBIDEException {
        long totalRows = 0;

        FileOutputStream fileOutStream = null;
        try {
            fileOutStream = new FileOutputStream(path.toString(), true);
            if (!MPPDBIDEConstants.BINARY_FILE_FORMAT.equalsIgnoreCase(fileFormat)) {
                ExportManager.prependBomForUtf8(encoding, fileOutStream);
            }

            totalRows = conn.execExportData(sql, fileOutStream);
        } catch (FileNotFoundException exp) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE), exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_EXPORT_TABLE, exp);
        } finally {
            if (fileOutStream != null) {
                try {
                    fileOutStream.close();
                } catch (IOException e) {
                    fileOutStream = null;

                }
            }
        }

        return totalRows;
    }

    /**
     * Import tabledata.
     *
     * @param sql the sql
     * @param path the path
     * @param conn the conn
     * @return the long
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public long importTabledata(String sql, String path, DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        FileInputStream fileStream = null;
        long totalRows = 0;

        double fileLimit = BLPreferenceManager.getInstance().getBLPreference().getImportFileSizeInMb();
        double fileSizeInMB = FileUtils.sizeOf(new File(path)) / (double) (1024 * 1024);

        if (fileLimit != 0 && fileSizeInMB > fileLimit) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.FILE_LIMIT_WARNING_MESSAGE));
            throw new DatabaseOperationException(IMessagesConstants.FILE_LIMIT_WARNING_MESSAGE);
        }

        try {
            fileStream = new FileInputStream(path);
            totalRows = conn.execImportTableData(sql, fileStream);
        } catch (FileNotFoundException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_EXPORT_TABLE, exception);
        } finally {
            try {
                if (fileStream != null) {
                    fileStream.close();
                }

            } catch (IOException ex) {
                fileStream = null;
            }

        }

        return totalRows;
    }

}