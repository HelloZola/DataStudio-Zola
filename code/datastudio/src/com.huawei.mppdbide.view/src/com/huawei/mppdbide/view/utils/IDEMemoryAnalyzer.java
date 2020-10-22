/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class IDEMemoryAnalyzer.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class IDEMemoryAnalyzer {
    private static Runtime runtime = Runtime.getRuntime();
    private static boolean is90PercentReached;
    private static final double THOUSAND_TWENTYFOUR = 1024.0;

    /**
     * Instantiates a new IDE memory analyzer.
     */
    private IDEMemoryAnalyzer() {

    }

    /**
     * Checks if is 90 percent reached.
     *
     * @return true, if is 90 percent reached
     */
    public static boolean is90PercentReached() {
        return is90PercentReached;
    }

    /**
     * Sets the checks if is 90 percent reached.
     *
     * @param is90PercentReached the new checks if is 90 percent reached
     */
    public static void setIs90PercentReached(boolean is90PercentReached) {
        IDEMemoryAnalyzer.is90PercentReached = is90PercentReached;
    }

    /**
     * Gets the max memory.
     *
     * @return the max memory
     */
    private static long getMaxMemory() {
        return runtime.maxMemory();
    }

    /**
     * Gets the max memory MB.
     *
     * @return the max memory MB
     */
    public static long getMaxMemoryMB() {
        long maxMemory = Math
                .round(Math.ceil(((double) runtime.maxMemory() / THOUSAND_TWENTYFOUR) / THOUSAND_TWENTYFOUR));
        if (MPPDBIDELoggerUtility.isTraceEnabled()) {
            MPPDBIDELoggerUtility
                    .trace(MessageConfigLoader.getProperty(IMessagesConstants.MPPDB_IDE_MOM_MAX) + ' ' + maxMemory);
        }
        return maxMemory;
    }

    /**
     * Gets the free memory.
     *
     * @return the free memory
     */
    private static long getFreeMemory() {
        return runtime.freeMemory();
    }

    /**
     * Gets the alloted memory.
     *
     * @return the alloted memory
     */
    private static long getAllotedMemory() {
        return runtime.totalMemory();
    }

    /**
     * Gets the total free memory.
     *
     * @return the total free memory
     */
    public static long getTotalFreeMemory() {
        return getFreeMemory() + (getMaxMemory() - getAllotedMemory());
    }

    /**
     * Gets the total used memory MB.
     *
     * @return the total used memory MB
     */
    public static long getTotalUsedMemoryMB() {
        long usedMemory = ((getMaxMemory() - getTotalFreeMemory()) / 1024) / 1024;
        if (MPPDBIDELoggerUtility.isTraceEnabled()) {
            MPPDBIDELoggerUtility
                    .trace(MessageConfigLoader.getProperty(IMessagesConstants.MPPDB_IDE_MOM_MAX) + ' ' + usedMemory);
        }
        return usedMemory;
    }

    /**
     * Gets the total used memory percentage.
     *
     * @return the total used memory percentage
     */
    public static long getTotalUsedMemoryPercentage() {

        return Math.round(Math.ceil(((double) getTotalUsedMemoryMB() / (double) getMaxMemoryMB()) * 100));
    }

    /**
     * Validate memory usage.
     */
    public static void validateMemoryUsage() {
        if (!IDEMemoryAnalyzer.is90PercentReached() && IDEMemoryAnalyzer.getTotalUsedMemoryPercentage() >= 90) {
            IDEMemoryAnalyzer.setIs90PercentReached(true);
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_MOM_USAGE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_MOM_USAGE_MSG));
        } else if (IDEMemoryAnalyzer.is90PercentReached() && IDEMemoryAnalyzer.getTotalUsedMemoryPercentage() < 90) {
            IDEMemoryAnalyzer.setIs90PercentReached(false);
        }
    }
}