/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.Database;

/**
 * This class collects the Database objects
 * 
 * @author g00408002
 * @version 1.0
 */

import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class DatabaseObjectGroup.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class DatabaseObjectGroup extends OLAPObjectGroup<Database> {

    private boolean isLoadingDatabaseGroupInProgress;

    /**
     * Instantiates a new database object group.
     *
     * @param type the type
     * @param server the server
     */
    public DatabaseObjectGroup(OBJECTTYPE type, Server server) {
        super(type, server);
    }

    /**
     * Gets the server.
     *
     * @return the server
     */
    public Server getServer() {
        return (Server) getParent();
    }

    /**
     * Refresh.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refresh() throws DatabaseOperationException, DatabaseCriticalException {
        Database db = this.getServer().findOneActiveDb();
        this.getServer().refreshDBs(db);
    }

    /**
     * Checks if is loading database group in progress.
     *
     * @return true, if is loading database group in progress
     */
    public boolean isLoadingDatabaseGroupInProgress() {
        return isLoadingDatabaseGroupInProgress;
    }

    /**
     * Sets the loading database group in progress.
     *
     * @param status the new loading database group in progress
     */
    public void setLoadingDatabaseGroupInProgress(boolean status) {
        this.isLoadingDatabaseGroupInProgress = status;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}