package com.huawei.mppdbide.test.presentation.table;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.adapter.gauss.StmtExecutor.GetFuncProcResultValueParam;
import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.bl.sqlhistory.QueryExecutionSummary;
import com.huawei.mppdbide.mock.presentation.CommonLLTUtils;
import com.huawei.mppdbide.mock.presentation.ResultSetMetaDataImplementation;
import com.huawei.mppdbide.presentation.ContextExecutionOperationType;
import com.huawei.mppdbide.presentation.ExecutionFailureActionOptions;
import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.presentation.IResultDisplayUIManager;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.edittabledata.CursorQueryExecutor;
import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.ResultSetDatatypeMapping;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import static org.junit.Assert.*;
/**
 * Query executor and materialize Test
 *
 */
public class CursorQueryExecutorTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    ServerConnectionInfo              serverInfo                = new ServerConnectionInfo();
    JobCancelStatus                   status                    = null;
    private Database                  database;
    ArrayList<DefaultParameter> debugInputValueList = new ArrayList<DefaultParameter>();

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        connection = new MockConnection();
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.addViewTableData(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
        database = connProfCache.getDbForProfileId(profileId);
        database.getServer().setServerCompatibleToNodeGroup(true);
    }

    @After
    public void tearDown() throws Exception
    {
        super.tearDown();

        Database database = connProfCache.getDbForProfileId(profileId);
        if (null != database)
        {
            database.getServer().close();
        }

        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();
        connProfCache.closeAllNodes();

        Iterator<Server> itr = connProfCache.getServers().iterator();

        while (itr.hasNext())
        {
            connProfCache.removeServer(itr.next().getId());
            itr = connProfCache.getServers().iterator();
        }

        connProfCache.closeAllNodes();

    }

    public IExecutionContext getExecutionContext()
    {
        return new IExecutionContext()
        {

            @Override
            public void setWorkingJobContext(Object jobContext)
            {

            }

            @Override
            public void setJobDone()
            {
                

            }

            @Override
            public void setCurrentExecution(ContextExecutionOperationType contextOperationTypeNewPlSqlCreation)
            {

            }

            @Override
            public void setCriticalErrorThrown(boolean b)
            {
                

            }

            @Override
            public boolean needQueryParseAndSplit()
            {
                
                return false;
            }

            @Override
            public String jobType()
            {
                
                return null;
            }

            @Override
            public void handleSuccessfullCompletion()
            {
                

            }

            @Override
            public void handleExecutionException(Exception e)
            {
                

            }

            @Override
            public Object getWorkingJobContext()
            {
                
                return null;
            }

            @Override
            public TerminalExecutionConnectionInfra getTermConnection()
            {
                
                return getTermConnn();
            }

            @Override
            public IResultDisplayUIManager getResultDisplayUIManager()
            {
                
                return handleResultDisplay();
            }

            @Override
            public IResultConfig getResultConfig()
            {
                
                return new IResultConfig()
                {

                    @Override
                    public int getFetchCount()
                    {
                        
                        return 1000;
                    }

                    @Override
                    public ActionAfterResultFetch getActionAfterFetch()
                    {
                        
                        return ActionAfterResultFetch.CLOSE_CONNECTION_AFTER_FETCH;
                    }
                };
            }

            @Override
            public String getQuery()
            {
                
                return null;
            }

            @Override
            public MessageQueue getNoticeMessageQueue()
            {
                
                return null;
            }

            @Override
            public ServerObject getCurrentServerObject()
            {
                
                return null;
            }

            @Override
            public ContextExecutionOperationType getCurrentExecution()
            {
                
                return null;
            }

            @Override
            public String getContextName()
            {
                
                return null;
            }

            @Override
            public String getConnectionProfileID()
            {
                
                return null;
            }

            @Override
            public ExecutionFailureActionOptions getActionOnQueryFailure()
            {
                
                return null;
            }

            @Override
            public boolean canFreeConnectionAfterUse()
            {
                
                return false;
            }

            @Override
            public ArrayList<DefaultParameter> getInputValues() {
                return debugInputValueList;
            }
        };
    }

    public IResultDisplayUIManager handleResultDisplay()
    {
        

        return null;
    }

    public TerminalExecutionConnectionInfra getTermConnn()
    {
        TerminalExecutionConnectionInfra termConn = new TerminalExecutionConnectionInfra();
        try
        {
            termConn.setConnection(database.getConnectionManager().getFreeConnection());
        }
        catch (MPPDBIDEException e)
        {
            
            e.printStackTrace();
        }
        return termConn;

    }

    public IQueryExecutionSummary getQueryExecutionSummary()
    {
        QueryExecutionSummary queryExcSummary = null;
        String input = "2016-12-16 18:29:09";

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
        java.util.Date date;
        try
        {
            date = sdf1.parse(input);
            String dt = sdf1.format(date);
            queryExcSummary = new QueryExecutionSummary("Gauss", "ds", CommonLLTUtils.EDIT_TABLE_DATA_SELECT_QUERY,
                    true, dt, 109, 0);
        }
        catch (ParseException e)
        {
            System.out.println("Parse exception occurred");
        }

        return queryExcSummary;
    }

    public IResultConfig getResultConfig()
    {
        return new IResultConfig()
        {

            @Override
            public int getFetchCount()
            {
                
                return 100;
            }

            @Override
            public ActionAfterResultFetch getActionAfterFetch()
            {
                
                return ActionAfterResultFetch.CLOSE_CONNECTION_AFTER_FETCH;
            }
        };
    }

    @Test
    public void test_cursor_query_executor_001_01() throws MPPDBIDEException
    {

        IQueryExecutionSummary summary = getQueryExecutionSummary();
        QueryExecutionSummary qrySummery = (QueryExecutionSummary) summary;
        IExecutionContext context = getExecutionContext();
        CommonLLTUtils.updateSelectQryCursorExecRS(statementHandler);
        

        CursorQueryExecutor curExecutor =
                new CursorQueryExecutor("select * from pg_amop;", context, qrySummery, true, true,database.getConnectionManager().getFreeConnection());
        String query = curExecutor.getUniqCursorName();
        
        MockResultSet getselectrs = preparedstatementHandler.createResultSet();
        getselectrs.addColumn("amopfamily");
        getselectrs.addColumn("amoplefttype");
        getselectrs.addColumn("amoprighttype");
        getselectrs.addColumn("amopstrategy");
        getselectrs.addColumn("amoppurpose");
        getselectrs.addColumn("amopopr");
        getselectrs.addColumn("amopmethod");
        getselectrs.addColumn("amopsortfamily");
        
        getselectrs.addRow(new Object[] {2, 22, 23, 7, 's', 88, 403, 0 });
        
        statementHandler.prepareResultSet(query, getselectrs);
        curExecutor.execute(qrySummery);
    }

    @Test
    public void test_cursor_query_executor_001_02() throws MPPDBIDEException
    {
        IQueryExecutionSummary summary = getQueryExecutionSummary();
        QueryExecutionSummary qrySummery = (QueryExecutionSummary) summary;
        IExecutionContext context = getExecutionContext();
        CommonLLTUtils.updateSelectQryCursorExecRS(statementHandler);

        CursorQueryExecutor curExecutor =
                new CursorQueryExecutor("select * from pg_amop;", context, qrySummery, false, false,database.getConnectionManager().getFreeConnection());

        
        String query = curExecutor.getUniqCursorName();
        MockResultSet getselectrs = preparedstatementHandler.createResultSet();
        getselectrs.addColumn("amopfamily");
        getselectrs.addColumn("amoplefttype");
        getselectrs.addColumn("amoprighttype");
        getselectrs.addColumn("amopstrategy");
        getselectrs.addColumn("amoppurpose");
        getselectrs.addColumn("amopopr");
        getselectrs.addColumn("amopmethod");
        getselectrs.addColumn("amopsortfamily");
        
        getselectrs.addRow(new Object[] {2, 22, 23, 7, 's', 88, 403, 0 });
        
        statementHandler.prepareResultSet(query, getselectrs);
        curExecutor.execute(qrySummery);
    }
    
    @Test
    public void test_cursor_query_executor_001_03() throws MPPDBIDEException
    {
        IQueryExecutionSummary summary = getQueryExecutionSummary();
        QueryExecutionSummary qrySummery = (QueryExecutionSummary) summary;
        IExecutionContext context = getExecutionContext();
        CommonLLTUtils.updateSelectQryCursorExecRS(statementHandler);

        CursorQueryExecutor curExecutor =
                new CursorQueryExecutor("select * from pg_amop;", context, qrySummery, false, false,database.getConnectionManager().getFreeConnection());

        
        String query = curExecutor.getUniqCursorName();
        MockResultSet getselectrs = preparedstatementHandler.createResultSet();
        getselectrs.setResultSetMetaData(new ResultSetMetaDataImplementation());
        getselectrs.addColumn("amopfamily");
        getselectrs.addColumn("amoplefttype");
        getselectrs.addColumn("amoprighttype");
        getselectrs.addColumn("amopstrategy");
        getselectrs.addColumn("amoppurpose");
        getselectrs.addColumn("amopopr");
        getselectrs.addColumn("amopmethod");
        getselectrs.addColumn("amopsortfamily");
        getselectrs.addRow(new Object[] {12345678, false, 10000.56, new Date(100001456), new Timestamp(100001456), "fdhfeufhuf", 403, 0 });
        statementHandler.prepareResultSet(query, getselectrs);
        ResultSetDatatypeMapping.setIncludeEncoding(true);
        curExecutor.execute(qrySummery);
        assertNotNull(curExecutor);
    }
    
    @Test
    public void test_cursor_query_executor_001_04() throws MPPDBIDEException
    {
        IQueryExecutionSummary summary = getQueryExecutionSummary();
        QueryExecutionSummary qrySummery = (QueryExecutionSummary) summary;
        DefaultParameter dp1 = new DefaultParameter("N1", "BINARY_INTEGER", "5", PARAMETERTYPE.IN);
        DefaultParameter dp2 = new DefaultParameter("N2", "BINARY_INTEGER", "5", PARAMETERTYPE.IN);
        DefaultParameter dp3 = new DefaultParameter("TEMP_RESULT", "BINARY_INTEGER", "500", PARAMETERTYPE.OUT);
        debugInputValueList.add(dp1);
        debugInputValueList.add(dp2);
        debugInputValueList.add(dp3);
        IExecutionContext context = getExecutionContext();
        CommonLLTUtils.updateSelectQryCursorExecRS(statementHandler);
        
        CursorQueryExecutor curExecutor =
                new CursorQueryExecutor("select * from pg_amop;", context, qrySummery, false, false,database.getConnectionManager().getFreeConnection());

        
        String query = curExecutor.getUniqCursorName();
        MockResultSet getselectrs = preparedstatementHandler.createResultSet();
        getselectrs.setResultSetMetaData(new ResultSetMetaDataImplementation() {
            @Override
            public int getColumnCount() throws SQLException
            {
                return 1;
            }
            @Override
            public String getColumnTypeName(int column) throws SQLException
            {
                return "refcursor";
            }
        });
        getselectrs.addColumn("amopfamily");
        List<ArrayList<Object>> listObj = new ArrayList<ArrayList<Object>>();
        ArrayList<Object> listObj1 = new ArrayList<Object>();
        listObj1.add(500);
        listObj.add(listObj1);
        getselectrs.addRow(new Object[] {listObj});
        statementHandler.prepareResultSet(query, getselectrs);
        ResultSetDatatypeMapping.setIncludeEncoding(true);
        curExecutor.execute(qrySummery);
        assertNotNull(curExecutor);
    }
    
    @Test
    public void test_cursor_query_executor_001_05() throws MPPDBIDEException
    {
        IQueryExecutionSummary summary = getQueryExecutionSummary();
        QueryExecutionSummary qrySummery = (QueryExecutionSummary) summary;
        DefaultParameter dp1 = new DefaultParameter("N1", "BINARY_INTEGER", "5", PARAMETERTYPE.IN);
        DefaultParameter dp2 = new DefaultParameter("N2", "BINARY_INTEGER", "5", PARAMETERTYPE.IN);
        DefaultParameter dp3 = new DefaultParameter("TEMP_RESULT", "BINARY_INTEGER", "500", PARAMETERTYPE.OUT);
        debugInputValueList.add(dp1);
        debugInputValueList.add(dp2);
        debugInputValueList.add(dp3);
        IExecutionContext context = getExecutionContext();
        CommonLLTUtils.updateSelectQryCursorExecRS(statementHandler);
        
        CursorQueryExecutor curExecutor =
                new CursorQueryExecutor("select * from pg_amop;", context, qrySummery, false, false,database.getConnectionManager().getFreeConnection());

        
        String query = curExecutor.getUniqCursorName();
        MockResultSet getselectrs = preparedstatementHandler.createResultSet();
        getselectrs.setResultSetMetaData(new ResultSetMetaDataImplementation() {
            @Override
            public int getColumnCount() throws SQLException
            {
                return 2;
            }
            @Override
            public String getColumnTypeName(int column) throws SQLException
            {
                return "refcursor";
            }
        });
        getselectrs.addColumn("amopfamily");
        getselectrs.addColumn("amoplefttype");
        List<ArrayList<Object>> listObj = new ArrayList<ArrayList<Object>>();
        ArrayList<Object> listObj1 = new ArrayList<Object>();
        listObj1.add(500);
        listObj.add(listObj1);
        getselectrs.addRow(new Object[] {listObj, false});
        statementHandler.prepareResultSet(query, getselectrs);
        ResultSetDatatypeMapping.setIncludeEncoding(true);
        curExecutor.execute(qrySummery);
        assertNotNull(curExecutor);
    }
    
    @Test
    public void test_StmtExecutor_GetFuncProcResultValueParam()
    {
        try
        {
            GetFuncProcResultValueParam param = new GetFuncProcResultValueParam(0, false, false);
            param.setColumnCount(0);
            param.setCallableStmt(true);
            param.setInputParaVisited(true);
            assertEquals(param.getColumnCount(), 0);
            assertTrue(param.isCallableStmt());
            assertTrue(param.isInputParaVisited());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
