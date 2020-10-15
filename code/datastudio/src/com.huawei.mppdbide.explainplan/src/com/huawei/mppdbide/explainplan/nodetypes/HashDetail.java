/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.nodetypes;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class HashDetail.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class HashDetail {
    @SerializedName("DN Name")
    private String dnName;

    @SerializedName("Hash Buckets")
    private long hashBuckets;

    @SerializedName("Hash Batches")
    private long hashBatches;

    @SerializedName("Original Hash Batches")
    private long originalHashBatches;

    @SerializedName("Peak Memory Usage")
    private long peakMemoryUsage;

    /**
     * Property details.
     *
     * @return the list
     */
    public List<Object> propertyDetails() {
        ArrayList<Object> colInfo = new ArrayList<Object>(5);

        colInfo.add(getHashBuckets());
        colInfo.add(getHashBatches());
        colInfo.add(getOriginalHashBatches());
        colInfo.add(getPeakMemoryUsage());

        return colInfo;
    }

    /**
     * Fill column property header.
     *
     * @return the DN intra node details column
     */
    public static DNIntraNodeDetailsColumn fillColumnPropertyHeader() {
        DNIntraNodeDetailsColumn dnBufferInDetail = new DNIntraNodeDetailsColumn();

        dnBufferInDetail.setGroupColumnName(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_HASHDETAIL));

        ArrayList<String> colnames = new ArrayList<String>(12);
        colnames.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_HASH_HASHBUCKETS));
        colnames.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_HASH_HASHBATCHES));
        colnames.add(MessageConfigLoader
                .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_HASH_ORIGINALHASHBATCHES));
        colnames.add(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_HASH_PEAKMEMORYUSAGE));

        dnBufferInDetail.setColCount(4);
        dnBufferInDetail.setColnames(colnames);

        return dnBufferInDetail;
    }

    private long getHashBuckets() {
        return hashBuckets;
    }

    private long getHashBatches() {
        return hashBatches;
    }

    private long getOriginalHashBatches() {
        return originalHashBatches;
    }

    private long getPeakMemoryUsage() {
        return peakMemoryUsage;
    }

    /**
     * Gets the dn name.
     *
     * @return the dn name
     */
    public String getDnName() {
        return dnName;
    }
}
