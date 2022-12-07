package org.opengauss.mppdbide.explainplan.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.opengauss.mppdbide.explainplan.mock.CommonLLTUtils;
import org.opengauss.mppdbide.explainplan.nodetypes.BuffersInDetail;
import org.opengauss.mppdbide.explainplan.nodetypes.HashDetail;
import org.opengauss.mppdbide.explainplan.nodetypes.HashJoinDNDetails;
import org.opengauss.mppdbide.explainplan.nodetypes.LLVMDNDetails;
import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNode;
import org.opengauss.mppdbide.explainplan.plannode.CStoreScanNode;
import org.opengauss.mppdbide.explainplan.plannode.CTEScanNode;
import org.opengauss.mppdbide.explainplan.plannode.DataNodeScan;
import org.opengauss.mppdbide.explainplan.plannode.FunctionScanNode;
import org.opengauss.mppdbide.explainplan.plannode.GroupBy;
import org.opengauss.mppdbide.explainplan.plannode.HashAggregate;
import org.opengauss.mppdbide.explainplan.plannode.HashJoin;
import org.opengauss.mppdbide.explainplan.plannode.HashNode;
import org.opengauss.mppdbide.explainplan.plannode.ModifyTableNode;
import org.opengauss.mppdbide.explainplan.plannode.NestLoopJoin;
import org.opengauss.mppdbide.explainplan.plannode.NestedLoopNode;
import org.opengauss.mppdbide.explainplan.plannode.PartitionItetrator;
import org.opengauss.mppdbide.explainplan.plannode.RecursiveUnionNode;
import org.opengauss.mppdbide.explainplan.plannode.RowAdapter;
import org.opengauss.mppdbide.explainplan.plannode.ScanNode;
import org.opengauss.mppdbide.explainplan.plannode.SortDetails;
import org.opengauss.mppdbide.explainplan.plannode.SortNode;
import org.opengauss.mppdbide.explainplan.plannode.StreamGather;
import org.opengauss.mppdbide.explainplan.plannode.StreamRedistribute;
import org.opengauss.mppdbide.explainplan.plannode.UnknownOperator;
import org.opengauss.mppdbide.explainplan.plannode.ValuesScanNode;
import org.opengauss.mppdbide.explainplan.plannode.VectorSetOpNode;
import org.opengauss.mppdbide.explainplan.plannode.WorkTableScanNode;
import org.opengauss.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelContentNode;
import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelOperationalPlanNode;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

public class NodeTypesTest
{
    
    
    @Test
    public void test_workTableScanNode_methods()
    {
        WorkTableScanNode workTbleScnNde = new WorkTableScanNode();
        assertEquals(0, workTbleScnNde.getAdditionalInfo(true).size());
        assertEquals(10, workTbleScnNde.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, workTbleScnNde.getPerDNSpecificDetails(inputMap).size());
    }
    
    @Test
    public void test_ValuesScanNode_methods()
    {
        ValuesScanNode valuesScanNode = new ValuesScanNode();
        assertEquals(0, valuesScanNode.getNodeSpecific().size());
        assertEquals(10, valuesScanNode.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, valuesScanNode.getPerDNSpecificDetails(inputMap).size());
        assertNull(valuesScanNode.getItemName());
    }
    
    @Test
    public void test_RecursiveUnionNode_methods()
    {
        RecursiveUnionNode node = new RecursiveUnionNode();
        assertEquals(0, node.getAdditionalInfo(true).size());
        assertEquals(10, node.getNodeSpecificProperties().size());
        assertNull(node.getItemName());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
    }
    
    @Test
    public void test_HashAggregate_methods()
    {
        HashAggregate hashAggregate = new HashAggregate();
        assertEquals(10, hashAggregate.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, hashAggregate.getPerDNSpecificDetails(inputMap).size());
        List<DNIntraNodeDetailsColumn> colGroup = new ArrayList<DNIntraNodeDetailsColumn>(5);
        assertEquals(2, hashAggregate.getPerDNSpecificColumnGroupingInfo(colGroup).size());
    }
    
    @Test
    public void test_CStoreScanNode_methods()
    {
        CStoreScanNode node = new CStoreScanNode();
        assertEquals(1, node.getAdditionalInfo(true).size());
        assertEquals(10, node.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
    }
    
    @Test
    public void test_GroupBy_methods()
    {
        GroupBy node = new GroupBy();
        assertEquals(null, node.getNodeSpecific());
        assertEquals(11, node.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
    }


    @Test
    public void test_DataNodeScan_methods()
    {
        DataNodeScan node = new DataNodeScan();
        assertEquals(2, node.getNodeSpecific().size());
        assertEquals(12, node.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
    }

    @Test
    public void test_ModifyTableNode_methods()
    {
        ModifyTableNode node = new ModifyTableNode();
        assertEquals(5, node.getNodeSpecific().size());
        assertEquals(15, node.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
        assertEquals("Table: ", node.getItemName());
    }

    @Test
    public void test_NestLoopJoin_methods()
    {
        NestLoopJoin node = new NestLoopJoin();
        assertEquals(0, node.getAdditionalInfo(true).size());
        assertEquals(13, node.getNodeSpecificProperties().size());
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
        List<DNIntraNodeDetailsColumn> colGroup = new ArrayList<DNIntraNodeDetailsColumn>(5);
        assertEquals(2, node.getPerDNSpecificColumnGroupingInfo(colGroup).size());
    }

    @Test
    public void test_NestedLoopNode_methods()
    {
        NestedLoopNode node = new NestedLoopNode();
        Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
        assertEquals(0, node.getPerDNSpecificDetails(inputMap).size());
        List<DNIntraNodeDetailsColumn> colGroup = new ArrayList<DNIntraNodeDetailsColumn>(5);
        assertEquals(2, node.getPerDNSpecificColumnGroupingInfo(colGroup).size());
    }

    @Test
    public void test_BuffersInDetail_methods()
    {
        BuffersInDetail buffersInDetail = new BuffersInDetail();
        assertEquals(12, buffersInDetail.propertyDetails().size());
        assertNotNull(BuffersInDetail.fillColumnPropertyHeader());
        assertNull(buffersInDetail.getDnName());
    }

    @Test
    public void test_HashDetail_methods()
    {
        HashDetail hashDetail = new HashDetail();
        assertEquals(4, hashDetail.propertyDetails().size());
        assertNotNull(HashDetail.fillColumnPropertyHeader());
        assertNull(hashDetail.getDnName());
    }

    @Test
    public void test_HashJoinDNDetails_methods()
    {
        HashJoinDNDetails hashJoinDNDetail = new HashJoinDNDetails();
        assertNotNull(HashJoinDNDetails.fillColumnPropertyHeader());
        hashJoinDNDetail.setDnName("DB1");
        assertEquals("DB1", hashJoinDNDetail.getDnName());
        hashJoinDNDetail.setMemoryUsed(20);
        assertEquals(20, hashJoinDNDetail.getMemoryUsed(), 0);
    }
    
    @Test
    public void test_HashJoinDNDetails_methods1()
    {
        HashJoinDNDetails hashJoinDNDetail = new HashJoinDNDetails();
        ArrayList<OperationalNode> childNodes = new ArrayList<OperationalNode>(1);
        hashJoinDNDetail.setDnName("DB1");
        hashJoinDNDetail.setMemoryUsed(20);
        hashJoinDNDetail.propertyDetails(childNodes);
        assertEquals(20, hashJoinDNDetail.getMemoryUsed(), 0);
    }

    @Test
    public void test_LLVMDNDetails_methods()
    {
        LLVMDNDetails details = new LLVMDNDetails();
        assertEquals(1, details.propertyDetails().size());
        assertNotNull(LLVMDNDetails.fillColumnPropertyHeader());
        assertNull(details.getDnName());
    }
    

     
	@Test
	public void test_sortdeatils() {
		SortDetails sortDetails = new SortDetails();
		String json = "{\"Sort Key\": [\"key1\",\"key2\"]}";
		Gson gson = new Gson();
		try {
			sortDetails = gson.fromJson(json, SortDetails.class);
		} catch (JsonSyntaxException excep) {
			excep.printStackTrace();
		}
		assertNotNull(sortDetails.getSortDetails());
	}
	
	private boolean isSupport() {
		return false;
	}

	private void pass() {
		assertNotNull(1 == 1);
	}
	
	@Test
    public void test_unknownOperator_01()
    {
	    UnknownOperator unknwnOp = new UnknownOperator();
	    assertEquals("", unknwnOp.getNodeCategoryName());
    }
}
