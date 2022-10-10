/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.presentation.edittabledata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.GaussUtils;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.bl.serverdatacache.IQueryResult;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.presentation.IExecutionContext;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSResultRowVisitor;
import org.opengauss.mppdbide.presentation.grid.resultset.CursorQueryResult;
import org.opengauss.mppdbide.presentation.resultsetif.IResultConfig;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.ResultSetDatatypeMapping;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class CursorQueryExecutor.
 * 
 * @since 3.0.0
 */
public class CursorQueryExecutorV2 {
	private String query;
	private int fetchSize;
	private String cursorName = null;
	private IDSResultRowVisitor visitor = null;
	private Statement stmt;
	private boolean needAutoCommitReset;
	private boolean isEditTable;
	private boolean isQueryResultEdit;
	private IResultConfig resultConfig;
	private IQueryExecutionSummary execSummary;
	private IExecutionContext execContext;
	private IDSGridDataProvider dataProvider = null;
	private DBConnection connection;

	private static final int CURSOR_FETCH_BATCH_SIZE = 1000;
	private static final String CURSOR_QUERY_PREPEND = "CURSOR %s NO SCROLL FOR %s";
	private static final String FETCH_QUERY = "FETCH FORWARD %d FROM %s";
	private static final String CLOSE_QUERY = "CLOSE %s";
	private static ArrayList<DefaultParameter> inputDailogValueList;
	private static final String LIMIT_QUERY = "SELECT * FROM (%s) LIMIT 1";
	private static final String LIMIT_QUERY_SIZE = "SELECT * FROM (%s) LIMIT %s";

	private Boolean withoutCursor = false;

	/**
	 * Instantiates a new cursor query executor.
	 *
	 * @param query              the query
	 * @param execContext        the exec context
	 * @param execSummary        the exec summary
	 * @param editTableData      the edit table data
	 * @param queryResultEditing the query result editing
	 * @param connection         the connection
	 */
	public CursorQueryExecutorV2(String query, IExecutionContext execContext, IQueryExecutionSummary execSummary,
			boolean editTableData, boolean queryResultEditing, DBConnection connection) {
		this.query = query;
		this.execContext = execContext;
		this.execSummary = execSummary;
		this.isEditTable = editTableData;
		this.isQueryResultEdit = queryResultEditing;
		this.resultConfig = this.execContext.getResultConfig();
		this.fetchSize = this.execContext.getResultConfig().getFetchCount();
		this.connection = connection;
	}

	/**
	 * Execute.
	 *
	 * @param execSummary2 the exec summary 2
	 * @return the IDS grid data provider
	 * @throws DatabaseCriticalException  the database critical exception
	 * @throws DatabaseOperationException the database operation exception
	 */
	public IDSGridDataProvider execute(IQueryExecutionSummary execSummary2)
			throws DatabaseCriticalException, DatabaseOperationException {
		if (getDBConnection().isClosed()) {
			return null;
		}

		return executeSelect();
	}

	/**
	 * Gets the DB connection.
	 *
	 * @return the DB connection
	 */
	private DBConnection getDBConnection() {
		return connection;
	}

	/**
	 * Gets the sql connection.
	 *
	 * @return the sql connection
	 */
	private Connection getSqlConnection() {
		return getDBConnection().getConnection();
	}

	/**
	 * sets the input dailog value list
	 * 
	 * @param getInputDailogValueList the getInputDailogValueList
	 */
	public static void setInputDailogValueList(ArrayList<DefaultParameter> getInputDailogValueList) {
		inputDailogValueList = getInputDailogValueList;
	}

	/**
	 * gets the input dailog value list
	 * 
	 * @return inputDailogValueList the inputDailogValueList
	 */
	public static ArrayList<DefaultParameter> getInputDailogValueList() {
		return inputDailogValueList;
	}

	/**
	 * Execute select.
	 *
	 * @return the IDS grid data provider
	 * @throws DatabaseCriticalException  the database critical exception
	 * @throws DatabaseOperationException the database operation exception
	 */
	private IDSGridDataProvider executeSelect() throws DatabaseCriticalException, DatabaseOperationException {
		getDistributionColumns();
		startTxn();
		try {
			executeCursor();
			try {
				fetchRecords();
			} finally {
				closeCursor();
			}
		} finally {
			stopTxn();
		}

		return this.dataProvider;
	}

	/**
	 * Gets the distribution columns.
	 *
	 * @return the distribution columns
	 * @throws DatabaseCriticalException  the database critical exception
	 * @throws DatabaseOperationException the database operation exception
	 */
	private void getDistributionColumns() throws DatabaseCriticalException, DatabaseOperationException {

		if (isEditTable) {
			ServerObject currentServerObject = execContext.getCurrentServerObject();
			if (currentServerObject != null && currentServerObject instanceof TableMetaData) {
				((TableMetaData) currentServerObject).fetchDistributionColumnList(getDBConnection());
			}
		}
	}

	/**
	 * Gets the visitor.
	 *
	 * @param sqlStmt the sql stmt
	 * @return the visitor
	 * @throws DatabaseOperationException the database operation exception
	 * @throws DatabaseCriticalException  the database critical exception
	 */
	private IDSResultRowVisitor getVisitor(Statement sqlStmt, boolean isfuncProcResultFlow)
			throws DatabaseOperationException, DatabaseCriticalException {
		if (null == this.visitor) {
			IQueryResult queryResult = new CursorQueryResult(sqlStmt, getDBConnection(), true);
			if (this.isEditTable || this.isQueryResultEdit) {
				DSEditTableDataGridDataProvider editTableDataProvider = null;
				editTableDataProvider = new DSEditTableDataGridDataProvider(queryResult, this.resultConfig,
						this.execSummary, this.execContext, this.isQueryResultEdit);
				editTableDataProvider.setDatabase(execContext.getTermConnection().getDatabase());
				editTableDataProvider
						.setIncludeEncoding(BLPreferenceManager.getInstance().getBLPreference().isIncludeEncoding());
				this.dataProvider = editTableDataProvider;
				this.visitor = editTableDataProvider.initByVisitor(isfuncProcResultFlow);
			} else {
				DSResultSetGridDataProvider rsdp = new DSResultSetGridDataProvider(queryResult, this.resultConfig,
						this.execSummary);
				rsdp.setIncludeEncoding(BLPreferenceManager.getInstance().getBLPreference().isIncludeEncoding());
				this.visitor = rsdp.initByVisitor(isfuncProcResultFlow);
				this.dataProvider = rsdp;
			}
		}

		return this.visitor;
	}

	/**
	 * Stop txn.
	 */
	private void stopTxn() {
		if (!needAutoCommitReset) {
			return;
		}
		IExecTimer timer = new ExecTimer("Stop transaction");
		timer.start();

		try {
			Connection sqlConnection = getSqlConnection();
			// Set auto commit would issue a commit before changing the flag.
			// Its a cautious call that "select fns()", might do an DML
			// operation and need commit.
			sqlConnection.setAutoCommit(true);
		} catch (SQLException ex) {
			// Ignore. Not mechanism to recover from this failure.
			MPPDBIDELoggerUtility.error(
					"Stop transaction after query execution failed. " + "No way to recover, and skiping this error",
					ex);
		}
		timer.stopAndLogNoException();
	}

	/**
	 * Start txn.
	 *
	 * @throws DatabaseCriticalException  the database critical exception
	 * @throws DatabaseOperationException the database operation exception
	 */
	private void startTxn() throws DatabaseCriticalException, DatabaseOperationException {
		try {
			if (getSqlConnection().getAutoCommit()) {
				// if already inside a transaction, no need to reset.
				getSqlConnection().setAutoCommit(false);
				this.needAutoCommitReset = true;
			}
		} catch (SQLException ex) {
			GaussUtils.handleCriticalException(ex);
			throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, ex);
		}
	}

	/**
	 * Close cursor.
	 */
	private void closeCursor() {
		IExecTimer timer = new ExecTimer("close cursor");
		timer.start();
		try {
			if (!withoutCursor) {
				this.stmt.execute(getCloseCursorQuery());
			}
		} catch (SQLException ex) {
			// Ignore. No way to recover from close failure.
			MPPDBIDELoggerUtility.error("Error closing a cursor.", ex);
		} finally {
			getDBConnection().closeStatement(stmt);
			timer.stopAndLogNoException();
		}
	}

	/**
	 * Fetch records.
	 *
	 * @throws DatabaseCriticalException  the database critical exception
	 * @throws DatabaseOperationException the database operation exception
	 */
	private void fetchRecords() throws DatabaseCriticalException, DatabaseOperationException {
		IExecTimer timer = new ExecTimer("fetch records");
		timer.start();
		int fetchedBatchSize = 0;
		int currentFetchSize = 0;
		int toFetchRowCount = 0;

		for (int index = 0; index < this.fetchSize || this.fetchSize < 1;) {
			if (this.fetchSize == -1) {
				// Fetch All case.
				currentFetchSize = CURSOR_FETCH_BATCH_SIZE;
			} else {
				toFetchRowCount = this.fetchSize - index;
				currentFetchSize = toFetchRowCount > CURSOR_FETCH_BATCH_SIZE ? CURSOR_FETCH_BATCH_SIZE
						: toFetchRowCount;
			}
			fetchedBatchSize = fetchRecordBatch(currentFetchSize);
			index += fetchedBatchSize;

			if (fetchedBatchSize < currentFetchSize) {
				this.visitor.setEndOfRecords();
				timer.stopAndLogNoException();
				return;
			}
		}
		timer.stopAndLogNoException();
	}

	/**
	 * Fetch record batch.
	 *
	 * @param currentFetchSize the current fetch size
	 * @return the int
	 * @throws DatabaseCriticalException  the database critical exception
	 * @throws DatabaseOperationException the database operation exception
	 */
	private int fetchRecordBatch(int currentFetchSize) throws DatabaseCriticalException, DatabaseOperationException {
		ResultSet rs = null;
		int recordFetchCounter = 0;
		boolean isfuncProcResultFlow = false;
		ArrayList<DefaultParameter> inputDailogValueListLocal;
		try {
			if (!withoutCursor) {
				rs = stmt.executeQuery(getFetchQuery(currentFetchSize));
			} else {
				rs = stmt.executeQuery(getLimitQueryWithSize(currentFetchSize));
			}
			inputDailogValueListLocal = this.execContext.getInputValues();
			setInputDailogValueList(inputDailogValueListLocal);
			IQueryResult queryResult = new CursorQueryResult(stmt, getDBConnection(), true);

			if (inputDailogValueListLocal != null && queryResult.getColumnCount() == 1) {
				isfuncProcResultFlow = true;
			}
			IDSResultRowVisitor rowVisitor = getVisitor(stmt, isfuncProcResultFlow);

			if (isfuncProcResultFlow) {
				int listSize = 0;
				if (inputDailogValueListLocal != null) {
					listSize = inputDailogValueListLocal.size();
				}
				for (int i = 0; i < listSize; i++) {
					rowVisitor.visitInputValues(inputDailogValueListLocal.get(i), i + 1);
				}

				this.dataProvider.setFuncProcExport(true);
			}
			while (rs.next()) {
				if (isfuncProcResultFlow) {
					rowVisitor.visit(rs, isfuncProcResultFlow, false, null);
				} else if (ResultSetDatatypeMapping.isReturnTypeCursor(rs)) {
					rowVisitor.visitUnNameCursor(rs);
				} else {
					rowVisitor.visit(rs);
				}
				recordFetchCounter++;
				if (recordFetchCounter == currentFetchSize) {
					break;
				}
			}
			return recordFetchCounter;
		} catch (SQLException ex) {
			GaussUtils.handleCriticalException(ex);
			throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, ex);
		} finally {
			closeResultSet(rs);
		}
	}

	/**
	 * Close result set.
	 *
	 * @param rs the rs
	 */
	private void closeResultSet(ResultSet rs) {
		try {
			if (null != rs) {
				rs.close();
			}
		} catch (SQLException ex) {
			// Ignore. Nothing can be done to recover.
			MPPDBIDELoggerUtility.debug("Resultset close failed while materializing the records");
		}
	}

	/**
	 * Execute cursor.
	 *
	 * @throws DatabaseCriticalException  the database critical exception
	 * @throws DatabaseOperationException the database operation exception
	 */
	private void executeCursor() throws DatabaseCriticalException, DatabaseOperationException {
		IExecTimer timer = new ExecTimer("Start Cursor");
		DBConnection dbConnection = getDBConnection();
		timer.start();
		try {
			this.stmt = getSqlConnection().createStatement();
			dbConnection.registerNoticeListner(this.stmt, this.execContext.getNoticeMessageQueue());
			stmt.execute(getCursorQuery());
		} catch (SQLException ex) {
			if (ex.getMessage().indexOf("Permission denied") > 0 || ex.getMessage().indexOf("subquery is bogus") > 0) {
				//
				withoutCursor = true;
				//
				stopTxn();
				//
				this.retryWithoutCursor(dbConnection);

				return;
			}
			GaussUtils.handleCriticalException(ex);
			throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE,
					dbConnection.extractErrorCodeAndErrorMsgFromServerError(ex), ex);
		} finally {
			if (!withoutCursor) {
				timer.stopAndLogNoException();
			}
			timer.stopAndLogNoException();
		}
	}

	private void retryWithoutCursor(DBConnection dbConnection)
			throws DatabaseCriticalException, DatabaseOperationException {
		IExecTimer timer = new ExecTimer("Start Cursor");
		timer.start();
		try {
			this.stmt = getSqlConnection().createStatement();
			dbConnection.registerNoticeListner(this.stmt, this.execContext.getNoticeMessageQueue());
			stmt.execute(getLimitQuery());
		} catch (SQLException ex) {
			GaussUtils.handleCriticalException(ex);
			throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE,
					dbConnection.extractErrorCodeAndErrorMsgFromServerError(ex), ex);
		} finally {
			timer.stopAndLogNoException();
		}
	}

	/**
	 * Gets the cursor query.
	 *
	 * @return the cursor query
	 */
	private String getCursorQuery() {
		return String.format(Locale.ENGLISH, CURSOR_QUERY_PREPEND, getUniqCursorName(), query);
	}

	/**
	 * gets the statement
	 * 
	 * @return the statement
	 */
	public Statement getStatement() {
		return this.stmt;
	}

	/**
	 * To be changed to a uniq name. __DS_QRY_CRSR_<TIMESTAMP>__ Unique name
	 * generator
	 *
	 * @return the uniq cursor name
	 */
	public String getUniqCursorName() {
		if (null == cursorName) {
			String timeStamp = new SimpleDateFormat("HHmmssSSS").format(new Date());
			cursorName = "__DS_QRY_CRSR_" + timeStamp + "__";
		}

		return cursorName;
	}

	/**
	 * Gets the fetch query.
	 *
	 * @param currentFetchSize the current fetch size
	 * @return the fetch query
	 */
	private String getFetchQuery(int currentFetchSize) {
		return String.format(Locale.ENGLISH, FETCH_QUERY, currentFetchSize, getUniqCursorName());
	}

	private String getLimitQuery() {
		return String.format(Locale.ENGLISH, LIMIT_QUERY, query);
	}

	private String getLimitQueryWithSize(int currentFetchSize) {
		return String.format(Locale.ENGLISH, LIMIT_QUERY_SIZE, query, currentFetchSize);
	}

	/**
	 * Gets the close cursor query.
	 *
	 * @return the close cursor query
	 */
	private String getCloseCursorQuery() {
		return String.format(Locale.ENGLISH, CLOSE_QUERY, getUniqCursorName());
	}
}
