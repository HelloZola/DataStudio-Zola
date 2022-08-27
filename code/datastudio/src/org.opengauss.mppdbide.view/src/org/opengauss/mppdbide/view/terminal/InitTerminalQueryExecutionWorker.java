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

package org.opengauss.mppdbide.view.terminal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Display;
import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.GaussUtils;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.presentation.CanContextContinueExecuteRule;
import org.opengauss.mppdbide.presentation.ExecutionFailureActionOptions;
import org.opengauss.mppdbide.presentation.IExecutionContext;
import org.opengauss.mppdbide.presentation.TerminalExecutionSQLConnectionInfra;
import org.opengauss.mppdbide.presentation.resultset.ActionAfterResultFetch;
import org.opengauss.mppdbide.presentation.resultset.ConsoleDataWrapper;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.IQuerrySplitter;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.SQLTerminalQuerySplit;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.ILogger;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.edittabledata.AbstractEditTableDataResultDisplayUIManager;
import org.opengauss.mppdbide.view.terminal.executioncontext.SQLTerminalExecutionContext;
import org.opengauss.mppdbide.view.terminal.queryexecution.SqlQueryExecutionWorkingContext;
import org.opengauss.mppdbide.view.ui.DBAssistantWindow;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.ui.terminal.resulttab.ResultTabQueryExecuteContext;
import org.opengauss.mppdbide.view.utils.DateFormatUtils;
import org.opengauss.mppdbide.view.utils.IDEMemoryAnalyzer;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;
import org.postgresql.PGConnection;

/**
 * Title: TerminalQueryExecutionWorker
 * 
 * Description:The class TerminalQueryExecutionWorker
 * 
 * @since 3.0.0
 */
public class InitTerminalQueryExecutionWorker extends UIWorkerJob {

	/**
	 * The context.
	 */
	protected volatile IExecutionContext context;
	private IQueryExecutionSummary latestQuerysummary;
	private static final String QUERY_EXEC_RESULT = "query_execution_result";
	private QueryExecutionOrchestrator orchestrator;

	/**
	 * The conn.
	 */
	DBConnection conn;
	private boolean canExecute = false;
	private SQLTerminal terminal;
	private final Object INSTANCE_LOCK = new Object();
	private HashSet<Object> listOfObjects = new HashSet<>();

	/**
	 * Instantiates a new terminal query execution worker.
	 *
	 * @param context the context
	 */
	public InitTerminalQueryExecutionWorker(IExecutionContext context) {
		super(context.getContextName(), context.jobType());
		this.context = context;
		conn = null;
		context.getResultDisplayUIManager().initDisplayManager(context.getCurrentExecution());
	}

	/**
	 * Do job.
	 *
	 * @return the object
	 * @throws DatabaseOperationException the database operation exception
	 * @throws DatabaseCriticalException  the database critical exception
	 * @throws MPPDBIDEException          the MPPDBIDE exception
	 * @throws Exception                  the exception
	 */
	@Override
	public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {

		MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_EXECUTE_SQLTERMINAL_QUERY, true);

		executeQueries();

		return null;
	}

	private void executeQueries() throws MPPDBIDEException, DatabaseOperationException {

		ConsoleDataWrapper consoleData = new ConsoleDataWrapper();
		// 渲染控制台登录信息
		this.getLoginInfo(consoleData);

		Object materializedResult = new Object();
		handlePostExecutionQuery(consoleData, materializedResult);

		return;
	}

	private void getLoginInfo(ConsoleDataWrapper consoleData) {

		String socketAddress = null;
		SQLTerminal sqlTerminal = ((SQLTerminalExecutionContext) context).getTerminal();
		Database database = sqlTerminal.getDatabase();
		DBConnection dbConnetion = null;
		List<UserLoginInfoRecord> records = new ArrayList<>();
		try {
			dbConnetion = database.getConnectionManager().getFreeConnection();
			socketAddress = dbConnetion.getSocketAddress();
			Statement statement = dbConnetion.getConnection().createStatement();

			String qry = "select * from " + "(" + "select * from pg_vastbase_login_info where username = '"
					+ dbConnetion.getDbUser() + "'" + "and login_result = true order by login_date desc limit 1"
					+ ") tem1" + " UNION " + "select * from " + "("
					+ "select * from pg_vastbase_login_info where username = '" + dbConnetion.getDbUser() + "'"
					+ "and login_result = false order by login_date desc limit 1" + ") tem2;";

			ResultSet rs = statement.executeQuery(qry);
			while (rs.next()) {
				String login_date = rs.getString("login_date");
				String ip_address = rs.getString("ip_address");
				String login_method = rs.getString("login_method");
				boolean login_result = rs.getBoolean("login_result");
				String app_name = StringUtils.isBlank(rs.getString("app_name")) ? "vds" : rs.getString("app_name");
				UserLoginInfoRecord record = new UserLoginInfoRecord();
				record.setLoginTime(login_date.split("\\.")[0]);
				record.setIpAddress(ip_address);
				record.setMethod(login_method);
				record.setApplicationName(app_name);
				record.setLoginResult(login_result);
				records.add(record);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnetion.disconnect();
		}

		this.loginRender(records, consoleData, socketAddress);
	}

	private void loginRender(List<UserLoginInfoRecord> records, ConsoleDataWrapper consoleData, String socketAddress) {

		for (UserLoginInfoRecord record : records) {
			consoleData.add("+++++++++++++++++++");
			if (record.isLoginResult()) {
				consoleData.add("Last successful login info");
			} else {
				consoleData.add("Last failed login info");
			}
			consoleData.add("login time: " + record.getLoginTime());
			consoleData.add("application name: " + record.getMethod());
			consoleData.add("Ip address: " + record.getIpAddress());
			consoleData.add("method: " + record.getMethod());
		}

		if (CollectionUtils.isEmpty(records)) {
			consoleData.add("+++++++++++++++++++");
			consoleData.add("current login info");
			consoleData.add("login time:" + DateFormatUtils.dataFormat(DateFormatUtils.yyyyMMddHHmmss, new Date()));
			consoleData.add("application name: vds");
			consoleData.add("Ip address: " + socketAddress.split("/")[0].split(":")[0]);
			consoleData.add("method: MD5");
		}
	}

	private void handlePostExecutionQuery(ConsoleDataWrapper consoleData, Object materializedResult)
			throws MPPDBIDEException {
		displayMaterializedResultToUI(consoleData, materializedResult);
	}

	/**
	 * Handle progres bar.
	 */
	private void handleProgresBar() {
		Display.getDefault().asyncExec(new Runnable() {

			/**
			 * run
			 */
			public void run() {
				synchronized (INSTANCE_LOCK) {
					context.hideExecProgresBar();
				}
			}
		});
	}

	/**
	 * Display materialized result to UI.
	 *
	 * @param consoleData        the console data
	 * @param materializedResult the materialized result
	 */
	private void displayMaterializedResultToUI(ConsoleDataWrapper consoleData, Object materializedResult) {
		if (materializedResult != null) {
			displayUIResult(materializedResult, consoleData);
		}
	}

	/**
	 * Can proceed with execution silent.
	 *
	 * @return true, if successful
	 */
	private boolean canProceedWithExecutionSilent() {
		for (;;) {
			switch (this.context.getResultDisplayUIManager().canContextExecutionContinue()) {
			case CONTEXT_EXECUTION_UNKNOWN: {
				/*
				 * If the job needs to be cancelled, the context might have some work to do. We
				 * move the handing to onMPPDBIDEExceptionUIAction method by raising a simple
				 * exception.
				 */
				if (isCancel()) {
					return false;
				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException exception) {
					break;
				}
				break;
			}

			case CONTEXT_EXECUTION_STOP: {
				return false;
			}

			case CONTEXT_EXECUTION_PROCEED: {
				if (isCancel()) {
					return false;
				}

				return haveMoreQueriesInJobContext();
			}
			}
		}
	}

	/**
	 * Have more queries in job context.
	 *
	 * @return true, if successful
	 */
	private boolean haveMoreQueriesInJobContext() {
		SqlQueryExecutionWorkingContext jobContext = (SqlQueryExecutionWorkingContext) this.context
				.getWorkingJobContext();

		if (null != jobContext && jobContext.hasNext()) {
			return true;
		}

		return false;
	}

	/**
	 * Perform post execution action.
	 *
	 * @throws MPPDBIDEException the MPPDBIDE exception
	 */
	protected void performPostExecutionAction() throws MPPDBIDEException {
		ActionAfterResultFetch action = this.context.getResultConfig().getActionAfterFetch();
		switch (action) {
		case ISSUE_COMMIT_CONNECTION_AFTER_FETCH: {
			performCommitConnection();
			break;
		}
		case ISSUE_ROLLBACK_CONNECTION_AFTER_FETCH: {
			performRollbackConnection();
			break;
		}
		case ISSUE_NO_OP: {
			// Do nothing. This happens when Auto Commit is set
			// to OFF by User. User has to manually COMMIT or
			// ROLLBACK the transaction.
			break;
		}
		case CLOSE_CONNECTION_AFTER_FETCH:
		default: {
			closeConnection();
			break;
		}
		}

		/*
		 * now the UI manager might want to do something. Like for example update
		 * progress bar etc.
		 */
		context.getResultDisplayUIManager().handleStepCompletion();
		getMemoryUsage();

	}

	/**
	 * Gets the memory usage.
	 *
	 * @return the memory usage
	 */
	private void getMemoryUsage() {
		if (!IDEMemoryAnalyzer.is90PercentReached() && IDEMemoryAnalyzer.getTotalUsedMemoryPercentage() >= 90) {
			IDEMemoryAnalyzer.setIs90PercentReached(true);
			MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
					MessageConfigLoader.getProperty(IMessagesConstants.MEMORY_USAGE),
					MessageConfigLoader.getProperty(IMessagesConstants.MEMORY_USAGE_WARNING));
		} else if (IDEMemoryAnalyzer.is90PercentReached() && IDEMemoryAnalyzer.getTotalUsedMemoryPercentage() < 90) {
			IDEMemoryAnalyzer.setIs90PercentReached(false);
		}
	}

	/**
	 * Perform rollback connection.
	 *
	 * @throws MPPDBIDEException the MPPDBIDE exception
	 */
	private void performRollbackConnection() throws MPPDBIDEException {
		this.context.getTermConnection().getConnection().rollback();

	}

	/**
	 * Perform commit connection.
	 *
	 * @throws MPPDBIDEException the MPPDBIDE exception
	 */
	private void performCommitConnection() throws MPPDBIDEException {
		this.context.getTermConnection().getConnection().commitConnection("Error while completing transaction");

	}

	/**
	 * Close connection.
	 *
	 * @throws MPPDBIDEException the MPPDBIDE exception
	 */
	private void closeConnection() throws MPPDBIDEException {
		this.context.getTermConnection().releaseConnection();
	}

	/**
	 * Display UI result.
	 *
	 * @param queryMatResult the query mat result
	 * @param consoleData    the console data
	 */
	private void displayUIResult(Object queryMatResult, ConsoleDataWrapper consoleData) {

		context.getResultDisplayUIManager().handleResultDisplay(queryMatResult, consoleData, this.latestQuerysummary);
	}

	/**
	 * On success UI action.
	 *
	 * @param obj the obj
	 */
	@Override
	public void onSuccessUIAction(Object obj) {
		handleProgresBar();

		/*
		 * the control might have jumped out from the doJob method, but it is not for
		 * sure that it was success case. So we check if the UI manager had set the flag
		 * for this context to complete and it actually completed. Only then we claim
		 * successful and do its related handling...
		 */
		if (CanContextContinueExecuteRule.CONTEXT_EXECUTION_PROCEED == this.context.getResultDisplayUIManager()
				.canContextExecutionContinue()) {
			this.context.handleSuccessfullCompletion();
		}
	}

	/**
	 * On critical exception UI action.
	 *
	 * @param exception the exception
	 */
	@Override
	public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
		handleCritcalExceptionPreOperation(exception);

		if (CanContextContinueExecuteRule.CONTEXT_EXECUTION_PROCEED == this.context.getResultDisplayUIManager()
				.canContextExecutionContinue()) {
			reconnectTerminalConnectionOnBtnClik();
			this.conn = this.context.getTermConnection().getConnection();

			if (context.getTermConnection().getAutoCommitFlag() == true) {
				handleExceptionOnAutoCommitTrue();

			} else if (context.getTermConnection().getAutoCommitFlag() == false) {
				SqlQueryExecutionWorkingContext workingJobContext;
				IQuerrySplitter querySplitter = null;

				// In reconnect pop up if reconnect clicked, the job content
				// should reset to new
				workingJobContext = new SqlQueryExecutionWorkingContext();
				this.context.setWorkingJobContext(workingJobContext);

				if (this.context.needQueryParseAndSplit()) {
					boolean isOLAP = isOLAPCon();
					// Split functionalities moved into SQLTerminalQuerySplit
					querySplitter = new SQLTerminalQuerySplit();
					try {
						querySplitter.splitQuerries(workingJobContext.getQueryArray(), context.getQuery(), isOLAP);
					} catch (DatabaseOperationException e1) {
						handleDbOperationExceptionOnSplitQuery(e1);
						return;
					}
				} else {
					workingJobContext.getQueryArray().add(context.getQuery());
				}

				handleExceptionOnAutoCommitFalse(workingJobContext);

			}
		} else if (context.getTermConnection().isReconnectOnTerminal()) {
			reconnectTerminalConnectionOnBtnClik();
			this.conn = this.context.getTermConnection().getConnection();
			return;
		}

	}

	private void handleCritcalExceptionPreOperation(DatabaseCriticalException exception) {
		handleProgresBar();
		handleQueryExecutionFailure();
		this.context.setCriticalErrorThrown(true);
		this.context.handleExecutionException(exception);
		context.getTermConnection().releaseConnection();
		// In reconnect pop up if reconnect & continue is clicked, the job
		// content should start from where it failed
	}

	private void handleDbOperationExceptionOnSplitQuery(DatabaseOperationException e1) {
		MPPDBIDELoggerUtility.error("TerminalqueryExecutionWorker: splitting queries failed.", e1);
		if (this.context instanceof ResultTabQueryExecuteContext) {
			((TerminalExecutionSQLConnectionInfra) context.getTermConnection()).releaseSecureConnection(this.conn);
		}
	}

	private void handleExceptionOnAutoCommitFalse(SqlQueryExecutionWorkingContext workingJobContext) {
		this.context.getResultDisplayUIManager().getSingleQueryArray(workingJobContext.getQueryArray(),
				context.getQuery());
		if (this.context instanceof ResultTabQueryExecuteContext) {
			((TerminalExecutionSQLConnectionInfra) context.getTermConnection()).releaseSecureConnection(this.conn);
		}
	}

	private void handleExceptionOnAutoCommitTrue() {
		SqlQueryExecutionWorkingContext workingJobContext = (SqlQueryExecutionWorkingContext) this.context
				.getWorkingJobContext();
		if (this.context instanceof ResultTabQueryExecuteContext) {
			((TerminalExecutionSQLConnectionInfra) context.getTermConnection()).releaseSecureConnection(this.conn);
		}
		workingJobContext.previous();
	}

	private boolean isOLAPCon() {
		boolean isOLAP = true;
		if (null != this.context.getTermConnection().getConnection()) {
			isOLAP = this.context.getTermConnection().getConnection().isOLAPConnection();
		}
		return isOLAP;
	}

	/**
	 * Reconnect terminal connection on btn clik.
	 */
	private void reconnectTerminalConnectionOnBtnClik() {
		this.context.getResultDisplayUIManager()
				.handlePreExecutionUIDisplaySetupCritical(this.context.getTermConnection(), true);
	}

	/**
	 * On operational exception UI action.
	 *
	 * @param exception the exception
	 */
	@Override
	public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
		handleProgresBar();

		/**
		 * if the context wishes to abort the execution for an error,then stop the work
		 * and return to the context owner.
		 */
		handleQueryExecutionFailure();
		if (this.context.getActionOnQueryFailure() == ExecutionFailureActionOptions.EXECUTION_FAILURE_ACTION_ABORT) {
			DBAssistantWindow.execErr(exception.getServerMessage() != null ? exception.getServerMessage() : "");
			this.context.handleExecutionException(exception);
		} else {
			MPPDBIDELoggerUtility.debug("Continuing Execution inspite of error during execution as per configuration");
		}

	}

	/**
	 * Handle query execution failure.
	 */
	private void handleQueryExecutionFailure() {
		if (null != this.latestQuerysummary) {
			this.latestQuerysummary.stopQueryTimer();
			this.latestQuerysummary.setQueryExecutionStatus(false);
			if (null != this.context.getResultDisplayUIManager().getEventBroker()) {
				this.context.getResultDisplayUIManager().getEventBroker().post(QUERY_EXEC_RESULT,
						this.latestQuerysummary);
			}
		}
	}

	/**
	 * On MPPDBIDE exception UI action.
	 *
	 * @param exception the exception
	 */
	@Override
	public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {
		handleProgresBar();
		handleQueryExecutionFailure();
		this.context.handleExecutionException(exception);
	}

	/**
	 * On exception UI action.
	 *
	 * @param exception the exception
	 */
	@Override
	public void onExceptionUIAction(Exception exception) {
		handleProgresBar();
	}

	/**
	 * Final cleanup.
	 *
	 * @throws MPPDBIDEException the MPPDBIDE exception
	 */
	@Override
	public void finalCleanup() throws MPPDBIDEException {
		if (canExecute) {
			this.schedule();
		} else {
			this.context.setJobDone();
			if (this.context.canFreeConnectionAfterUse()) {
				this.context.getTermConnection().releaseConnection();
				MPPDBIDELoggerUtility.debug("Connection released after use.");
			}
			if (this.context instanceof ResultTabQueryExecuteContext) {
				((TerminalExecutionSQLConnectionInfra) context.getTermConnection()).releaseSecureConnection(this.conn);
			}
		}
		canExecute = false;
	}

	/**
	 * Pre final cleanup.
	 *
	 * @throws MPPDBIDEException the MPPDBIDE exception
	 */
	@Override
	public void preFinalCleanup() throws MPPDBIDEException {
		canExecute = canProceedWithExecutionSilent();
	}

	/**
	 * Final cleanup UI.
	 */
	@Override
	public void finalCleanupUI() {
		UIElement.getInstance().autoRefresh(listOfObjects);
		if (terminal != null) {
			terminal.resetCommitAndRollbackButton();
		}
		if (!canExecute) {
			if (this.context.getResultDisplayUIManager() instanceof AbstractEditTableDataResultDisplayUIManager) {
				((AbstractEditTableDataResultDisplayUIManager) this.context.getResultDisplayUIManager())
						.handlePostGridDataLoadEvent();
			}
			this.context.getResultDisplayUIManager().handleFinalCleanup();
		}
	}

	/**
	 * Canceling.
	 */
	@Override
	protected void canceling() {
		if (isCancel()) {
			return;
		} else {
			handleProgresBar();
			ConsoleDataWrapper consoleMsg = new ConsoleDataWrapper();
			consoleMsg.add(MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUERY_CANCELMSG_PROGRESS));
			this.context.getResultDisplayUIManager().handleConsoleDisplay(consoleMsg);

			super.canceling();
			this.context.getResultDisplayUIManager().handleCancelRequest();

			try {
				Statement stmt = this.orchestrator.getStatement();
				if (null != stmt) {
					cancelSQLExecutionQuery(stmt);
				} else {
					DBConnection conne = this.context.getTermConnection().getConnection();
					if (null != conne) {
						conne.cancelQuery();
					}
				}
			} catch (MPPDBIDEException e1) {
				ConsoleDataWrapper consoleData = new ConsoleDataWrapper();

				consoleData.add(MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUERY_CANCEL_CANCELMSG));
				this.context.getResultDisplayUIManager().handleConsoleDisplay(consoleData);
			}
		}
	}

	/**
	 * Cancel SQL Execution query.
	 *
	 * @throws DatabaseCriticalException  the database critical exception
	 * @throws DatabaseOperationException the database operation exception
	 */
	public void cancelSQLExecutionQuery(Statement stmt) throws DatabaseCriticalException, DatabaseOperationException {
		try {
			MPPDBIDELoggerUtility.info("ADAPTER: Sending cancel request");
			if (stmt != null && !stmt.isClosed()) {
				stmt.cancel();
			}

			MPPDBIDELoggerUtility.info("ADAPTER: Cancel successfully executed");
		} catch (SQLException exp) {
			GaussUtils.handleCriticalException(exp);
			MPPDBIDELoggerUtility.error("ADAPTER: cancel query returned exception.", exp);
		} finally {
			try {
				if (null != stmt) {
					stmt.close();
				}
			} catch (SQLException exception) {
				MPPDBIDELoggerUtility.error("ADAPTER: statement close returned exception.", exception);
			}
		}
	}

	/**
	 * Sets the terminal.
	 *
	 * @param terminal the new terminal
	 */
	public void setTerminal(SQLTerminal terminal) {
		this.terminal = terminal;
	}

	class UserLoginInfoRecord {

		private String loginTime;

		private String applicationName;

		private String ipAddress;

		private String method;

		private boolean loginResult;
		
		private String errorMsg;

		public String getLoginTime() {
			return loginTime;
		}

		public void setLoginTime(String loginTime) {
			this.loginTime = loginTime;
		}

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public String getIpAddress() {
			return ipAddress;
		}

		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public boolean isLoginResult() {
			return loginResult;
		}

		public void setLoginResult(boolean loginResult) {
			this.loginResult = loginResult;
		}

		public String getErrorMsg() {
			return errorMsg;
		}

		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}

	}
}
