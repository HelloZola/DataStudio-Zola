/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.jsonparser;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNodeDeserializer;
import com.huawei.mppdbide.explainplan.nodetypes.RootPlanNode;
import com.huawei.mppdbide.explainplan.nodetypes.RootPlanNodeDeserializer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainPlanJsonContentParser.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExplainPlanJsonContentParser {
    private String jsonContent;

    /**
     * Instantiates a new explain plan json content parser.
     *
     * @param jsonContent the json content
     */
    public ExplainPlanJsonContentParser(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    /**
     * Parses the file contents.
     *
     * @return the root plan node
     * @throws DatabaseOperationException the database operation exception
     */
    public RootPlanNode parseFileContents() throws DatabaseOperationException {
        RootPlanNode cRootNode = jsonParser();
        if (cRootNode != null && !cRootNode.getChildren().isEmpty()) {
            cRootNode.getChildren().get(0).setParent(null);
            fixParentChildRelationship(cRootNode.getChildren(), null);
            return cRootNode;
        }

        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_JSON_PARSING_FAILED));
        throw new DatabaseOperationException(IMessagesConstants.VIS_EXPLAIN_JSON_PARSING_FAILED);

    }

    private void fixParentChildRelationship(ArrayList<OperationalNode> arrayList, OperationalNode parent) {
        for (OperationalNode c : arrayList) {
            c.setParent(parent);
            fixParentChildRelationship(c.getChildren(), c);
        }

    }

    private RootPlanNode jsonParser() {
        Gson gson = null;
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(RootPlanNode.class, new RootPlanNodeDeserializer());
        gsonBuilder.registerTypeAdapter(OperationalNode.class, new OperationalNodeDeserializer());
        gson = gsonBuilder.create();

        try {
            return gson.fromJson(jsonContent, RootPlanNode.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
