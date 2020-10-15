/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.exportdata;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.FileCompressException;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: ExportZipData
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author xWX634836
 * @version [DataStudio 6.5.1, 11-Oct-2019]
 * @since Jun 14, 2019
 */

public class ExportZipData {
    private ZipOutputStream out;
    private BufferedInputStream bis;
    private FileInputStream fis;
    private FileOutputStream fos;

    /**
     * Do compress.
     *
     * @param srcFilePath the src file path
     * @param zipFilePath the zip file path
     * @throws FileCompressException the file compress exception
     * @Author: lijialiang(l00448174)
     * @Date: Aug 12, 2019
     * @Title: doCompress
     * @Description: the entrance for performing compression
     */
    public void doCompress(String srcFilePath, String zipFilePath) throws FileCompressException {
        try {
            compressCore(srcFilePath, zipFilePath);
        } catch (IOException | DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.COMPRESS_FAILED), exception);
            throw new FileCompressException(IMessagesConstants.COMPRESS_FAILED, exception.getMessage(), exception);
        }
    }

    private void compressCore(String srcFilePath, String zipFilePath)
            throws IOException, DatabaseOperationException {
        File srcFile = new File(srcFilePath);
        File zipFile = null;
        try {
            fis = new FileInputStream(srcFile);
            zipFile = new File(zipFilePath);
            if (!zipFile.exists()) {
                FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(zipFilePath, false, null,
                        false);
            }
            fos = new FileOutputStream(zipFile);
            out = new ZipOutputStream(fos);
            writeData(fis, out, srcFile);
        } finally {
            closeIO();
            deleteFile(srcFile);
        }
    }

    private void writeData(FileInputStream fis, ZipOutputStream out, File srcFile) throws IOException {
        String entryName = null;
        bis = new BufferedInputStream(fis);
        entryName = srcFile.getName();
        ZipEntry entry = new ZipEntry(entryName);
        out.putNextEntry(entry);
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = bis.read(buffer)) > 0) {
            out.write(buffer, 0, len);
            out.flush();
        }
    }

    private void closeIO() {
        if (out != null) {
            try {
                out.closeEntry();
            } catch (IOException ioException) {
                MPPDBIDELoggerUtility.error("ExportZipData: Error while closing Entry", ioException);
            }

            try {
                out.close();
            } catch (IOException ioException) {
                MPPDBIDELoggerUtility.error("ExportZipData: Error while closing ZipOutputStream", ioException);
            }
        }

        if (fos != null) {
            try {
                fos.close();
            } catch (IOException ioException) {
                MPPDBIDELoggerUtility.error("ExportZipData: Error while closing FileOutputStream", ioException);
            }
        }

        if (bis != null) {
            try {
                bis.close();
            } catch (IOException ioException) {
                MPPDBIDELoggerUtility.error("ExportZipData: Error while closing BufferedInputStream", ioException);
            }
        }

        if (fis != null) {
            try {
                fis.close();
            } catch (IOException ioException) {
                MPPDBIDELoggerUtility.error("ExportZipData: Error while closing FileInputStream", ioException);
            }
        }
    }

    private void deleteFile(File file) {
        if (file.exists() && file.isFile()) {
            if (file.delete() == false) {
                MPPDBIDELoggerUtility.error("ExportZipData: failed to delete the file in temp path");
            }

        }
    }

    /**
     * Gets the temp path str.
     *
     * @param zipFilePath the zip file path
     * @param fileSuffix the file suffix
     * @return the temp path str
     * @Author: lijialiang(l00448174)
     * @Date: Aug 14, 2019
     * @Title: getTempPathStr
     */
    public static String getTempPathStr(String zipFilePath, String fileSuffix, String tempPath) {
        return tempPath + EnvirnmentVariableValidator.validateAndGetFileSeperator()
                + zipFilePath.substring(
                        zipFilePath.lastIndexOf(EnvirnmentVariableValidator.validateAndGetFileSeperator()) + 1,
                        zipFilePath.lastIndexOf(MPPDBIDEConstants.DOT))
                + fileSuffix;
    }

}
