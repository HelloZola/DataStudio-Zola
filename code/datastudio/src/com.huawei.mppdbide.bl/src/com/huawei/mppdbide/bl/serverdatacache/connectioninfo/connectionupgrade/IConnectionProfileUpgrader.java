/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.connectioninfo.connectionupgrade;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IConnectionProfileUpgrader.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IConnectionProfileUpgrader {

    /**
     * Upgrade.
     *
     * @param jsonString the json string
     * @return the string
     */
    public String upgrade(String jsonString);
}