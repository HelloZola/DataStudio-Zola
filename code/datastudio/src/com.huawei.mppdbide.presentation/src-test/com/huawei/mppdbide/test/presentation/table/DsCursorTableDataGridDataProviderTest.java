package com.huawei.mppdbide.test.presentation.table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DatabaseUtils;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.sqlhistory.QueryExecutionSummary;
import com.huawei.mppdbide.mock.presentation.CommonLLTUtils;
import com.huawei.mppdbide.presentation.edittabledata.DSCursorDataGridRow;
import com.huawei.mppdbide.presentation.edittabledata.DSCursorTableDataGridDataProvider;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridColumnDataProvider;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataRow;
import com.huawei.mppdbide.presentation.edittabledata.EditTableCellState;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordStates;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class DsCursorTableDataGridDataProviderTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    int                               actionFetchResult;
    DSResultSetGridColumnDataProvider colData;

    public int getActionFetchResult()
    {
        return actionFetchResult;
    }

    public void setActionFetchResult(int actionFetchResult)
    {
        this.actionFetchResult = actionFetchResult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        connection = new MockConnection();
        // test for logging
        MPPDBIDELoggerUtility
                .setArgs(new String[] {"-logfolder=.", "-detailLogging=true"});

        // MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");

        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDriverName("FusionInsight LibrA");
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    @After
    public void tearDown() throws Exception
    {
        super.tearDown();

        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().close();

        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();
        connProfCache.closeAllNodes();

        Iterator<Server> itr = connProfCache.getServers().iterator();

        while (itr.hasNext())
        {
            connProfCache.removeServer(itr.next().getId());
        }

        connProfCache.closeAllNodes();

    }

    @Test
    public void testINIT_FUNC_001_001_1()
    {
        try
        {
            Object[] valueList = new Object[]{"empid", "ename", "salary"}; 
            DSCursorTableDataGridDataProvider cdsr = new DSCursorTableDataGridDataProvider(valueList);
            cdsr.initByVisitor(valueList);
            DSResultSetGridDataRow gridDataRow = new DSResultSetGridDataRow(cdsr);
            cdsr.visit(gridDataRow);
            cdsr.setEndOfRecords();
            cdsr.rollBackProvider();
            cdsr.decrementUpdatedRowCount();
            cdsr.incrementUpdatedRowCount();
            cdsr.cancelCommit();
            assertNotNull(cdsr.getConsolidatedRows());
            assertNotNull(cdsr.getColumnDataProvider());
            assertTrue(cdsr.isEndOfRecords());
            assertEquals(cdsr.isEditSupported(), false);
            assertEquals(cdsr.isGridDataEdited(), false);
            assertEquals(cdsr.getUpdatedRowCount(), 0);
            assertEquals(cdsr.getInsertedRowCount(), 0);
            assertEquals(cdsr.getDeletedRowCount(), 0);
            assertEquals(cdsr.getEmptyRowForInsert(0), null);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not Expected to come here");
        }
    }
    
    @Test
    public void testINIT_FUNC_001_001_2()
    {
        try
        {
            Object[] valueList = new Object[]{"empid", "ename", "salary"}; 
            DSCursorTableDataGridDataProvider cdsr = new DSCursorTableDataGridDataProvider(valueList);
            
            DSCursorDataGridRow gridRow = new DSCursorDataGridRow(false, 1, new DSEventTable(), cdsr);
            gridRow.setCellSatus(EditTableCellState.MODIFIED, 0);
            gridRow.setUpdatedRecords(0);
            gridRow.setCommitStatusMessage("commited");
            gridRow.setRowIndex(0);
            gridRow.setValue(0, "101");
            assertEquals(EditTableCellState.MODIFIED, gridRow.getCellStatus(0));
            assertEquals(gridRow.getUpdatedRecords(), 0);
            assertEquals(gridRow.getCommitStatusMessage(), "commited");
            assertEquals(gridRow.getRowIndex(), 0);
            assertEquals(gridRow.getValue(0), "101");
            assertEquals(gridRow.getUpdatedState(0), EditTableRecordStates.UPDATE);
            assertNotNull(gridRow.getOriginalValue(0));
            assertNotNull(gridRow.getExecutionStatus());
            assertNotNull(gridRow.getModifiedColumns());
            Object[] value = new Object[] {"101"};
            gridRow.createNewRow(value);
            assertNotNull(gridRow.getValues());
            assertNotNull(gridRow.getClonedValues());
            gridRow.undo(0);
            gridRow.clearAllRowUpdates();
            gridRow.setStateDelete();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Not Expected to come here");
        }
    }
}
