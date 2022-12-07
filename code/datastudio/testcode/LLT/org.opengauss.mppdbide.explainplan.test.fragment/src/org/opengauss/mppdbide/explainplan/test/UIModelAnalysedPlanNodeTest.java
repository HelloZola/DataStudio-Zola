package org.opengauss.mppdbide.explainplan.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.opengauss.mppdbide.explainplan.mock.CommonLLTUtils;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNode;
import org.opengauss.mppdbide.explainplan.service.ExplainPlanAnlysisService;
import org.opengauss.mppdbide.presentation.IWindowDetail;
import org.opengauss.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import org.opengauss.mppdbide.presentation.visualexplainplan.ExplainPlanNodePropertiesCore;
import org.opengauss.mppdbide.presentation.visualexplainplan.IUIModelAnalysedPlanNodeToGraphModelConvertor;
import org.opengauss.mppdbide.presentation.visualexplainplan.Relationship;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelConverter;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

public class UIModelAnalysedPlanNodeTest {

	@Test
	public void test_AnalysedPlanedNode_methods_test() {
		try {
			String jsonString = "[{\"Plan\": {\"Node Type\": \"Streaming (type: GATHER)\",\"Startup Cost\": 74.28,"
					+ "\"Total Cost\": 123.37,\"Plan Rows\": 30,\"Plan Width\": 268,\"Actual Startup Time\": 33.401,"
					+ "\"Actual Total Time\": 34.134,\"Actual Rows\": 150,\"Actual Loops\": 1,\"Output\": [\"comp.name\", "
					+ "\"cindex.idx_col\", \"ng.dob\", \"part.part_range\", \"cpart.part_range\"],\"Nodes\": "
					+ "\"All datanodes\",\"Shared Hit Blocks\": 1,\"Shared Read Blocks\": 2,\"Shared Dirtied Blocks\": 3,"
					+ "\"Shared Written Blocks\": 4,\"Local Hit Blocks\": 5,\"Local Read Blocks\": 6,\"Local Dirtied Blocks\": 7,"
					+ "\"Local Written Blocks\": 8,\"Temp Read Blocks\": 9,\"Temp Written Blocks\": 10,\"IO Read Time\": 0.000,"
					+ "\"IO Write Time\": 0.000}}]";
			ExplainPlanAnlysisService planAnalysis = new ExplainPlanAnlysisService(jsonString);
			UIModelAnalysedPlanNode node = null;

			node = UIModelConverter.covertToUIModel(planAnalysis.doAnalysis());
			Relationship relationShip = new Relationship(node, node);
			relationShip.getParentNode();
			relationShip.setParentNode(node);
			relationShip.setChildNode(node);
			relationShip.getChildNode();
			relationShip.getRecordCount();
			List<UIModelAnalysedPlanNode> flattenedExplainPlan = new ArrayList<UIModelAnalysedPlanNode>(5);
			flattenedExplainPlan.add(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
