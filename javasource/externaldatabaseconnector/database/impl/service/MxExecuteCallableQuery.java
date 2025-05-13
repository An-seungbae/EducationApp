package externaldatabaseconnector.database.impl.service;

import externaldatabaseconnector.database.impl.callablestatement.StatementWrapper;
import externaldatabaseconnector.database.impl.statement.CallableStatementCreatorImpl;
import externaldatabaseconnector.database.interfaces.CallableStatementCreator;
import externaldatabaseconnector.database.utils.MxCallableParameterUtils;
import externaldatabaseconnector.database.utils.MxResultSetUtils;
import externaldatabaseconnector.exceptions.MxException;
import externaldatabaseconnector.livepreview.responses.pojo.CallableParameterResult;
import externaldatabaseconnector.livepreview.responses.pojo.CallableResult;
import externaldatabaseconnector.livepreview.responses.pojo.TableResult;
import externaldatabaseconnector.pojo.QueryDetails;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class MxExecuteCallableQuery {
  private Connection connection;
  private QueryDetails queryDetails;
  private CallableStatementCreator callableStatementCreator;


  public MxExecuteCallableQuery(Connection connection, QueryDetails queryDetails, CallableStatementCreator callableStatementCreator) {
    this.connection = connection;
    this.queryDetails = queryDetails;
    this.callableStatementCreator = callableStatementCreator;
  }

  public MxExecuteCallableQuery(Connection connection, QueryDetails queryDetails) {
    this(connection, queryDetails, new CallableStatementCreatorImpl());
  }

  public CallableResult execute() throws SQLException, MxException {
    try (StatementWrapper callableStatement = callableStatementCreator.create(queryDetails.getQuery(), queryDetails.getQueryParameters(),
        connection)) {

      // Set auto-commit to false to start a transaction manually
      connection.setAutoCommit(false);

      try (CallableStatement callableStatementResult = callableStatement.executeCallable()) {
        int rowsAffected = callableStatementResult.getUpdateCount();
        TableResult tableResult = MxResultSetUtils.buildTableResult(callableStatementResult.getResultSet());
        CallableParameterResult callableParameterResult =
            MxCallableParameterUtils.readCallableParameterValues(queryDetails.getQueryParameters(), callableStatementResult);

        // Roll back the transaction after successful execution to verify the query without committing changes
        connection.rollback();
        return new CallableResult(rowsAffected, tableResult, callableParameterResult);
      }
    }
  }
}
