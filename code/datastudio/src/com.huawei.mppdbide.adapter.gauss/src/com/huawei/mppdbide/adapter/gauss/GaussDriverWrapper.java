/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.gauss;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.huawei.mppdbide.adapter.AbstractConnectionDriver;
import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.adapter.keywordssyntax.Keywords;
import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;

/**
 * 
 * Title: class
 * 
 * Description: The Class GaussDriverWrapper.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GaussDriverWrapper implements IConnectionDriver {
    private AbstractConnectionDriver driverInstance;
    private String driverWrapperName;
    private static volatile int driverWrapperCount = 0;

    private boolean isShowDDLSupport = false;
    private boolean hasShowDDLSupportCheck = false;

    private static final Object LOCK = new Object();

    /**
     * Gets the unique count.
     *
     * @return the unique count
     */
    private static int getUniqueCount() {
        synchronized (LOCK) {
            driverWrapperCount++;
        }
        return driverWrapperCount;
    }

    /**
     * Instantiates a new gauss driver wrapper.
     *
     * @param driver the driver
     */
    public GaussDriverWrapper(AbstractConnectionDriver driver) {
        this.driverInstance = driver;
        this.driverWrapperName = this.driverInstance.getDriverName() + String.valueOf(getUniqueCount());
    }

    /**
     * Gets the driver instance.
     *
     * @return the driver instance
     */
    public AbstractConnectionDriver getDriverInstance() {
        return this.driverInstance;
    }

    /**
     * Gets the show DDL support.
     *
     * @return the show DDL support
     */
    public boolean getShowDDLSupport() {
        return this.isShowDDLSupport;
    }

    /**
     * Gets the show DDL support.
     *
     * @param connection the connection
     * @return the show DDL support
     */
    public boolean getShowDDLSupport(DBConnection connection) {
        this.isShowDDLSupport = connection.getShowDDLSupportFromServer();
        this.hasShowDDLSupportCheck = true;
        return this.isShowDDLSupport;
    }

    /**
     * Gets the show DDL support check.
     *
     * @return the show DDL support check
     */
    public boolean getShowDDLSupportCheck() {
        return this.hasShowDDLSupportCheck;
    }

    @Override
    public String getDriverName() {
        return this.driverWrapperName;
    }

    @Override
    public String getToolPath(String toolName) {
        return this.driverInstance.getToolPath(toolName);
    }

    @Override
    public Driver getJDBCDriver() {
        return this.driverInstance.getJDBCDriver();
    }

    @Override
    public Properties getDriverSpecificProperties() {
        return this.driverInstance.getDriverSpecificProperties();
    }

    @Override
    public String extractErrCodeAdErrMsgFrmServErr(SQLException eex) {
        return this.driverInstance.extractErrCodeAdErrMsgFrmServErr(eex);
    }

    @Override
    public Keywords getKeywordList() {
        return this.driverInstance.getKeywordList();
    }

    @Override
    public SQLSyntax loadSQLSyntax() {

        return this.driverInstance.loadSQLSyntax();
    }

}