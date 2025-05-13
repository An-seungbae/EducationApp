package externaldatabaseconnector.database.impl.service;

import com.mendix.logging.ILogNode;

import externaldatabaseconnector.database.exceptions.MxDataTypeNotSupported;
import externaldatabaseconnector.database.impl.connection.ConnectionManagerSingleton;
import externaldatabaseconnector.database.impl.statement.PreparedStatementCreatorImpl;
import externaldatabaseconnector.database.interfaces.ConnectionManager;
import externaldatabaseconnector.database.interfaces.PreparedStatementCreator;
import externaldatabaseconnector.pojo.ConnectionDetails;
import externaldatabaseconnector.pojo.QueryDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MxExecuteDmlQuery {
  private final Connection connection;
  private final QueryDetails queryDetails;
  private final PreparedStatementCreator preparedStatementCreator;

  public MxExecuteDmlQuery(Connection connection, QueryDetails queryDetails, PreparedStatementCreator preparedStatementCreator) {
    this.connection = connection;
    this.queryDetails = queryDetails;
    this.preparedStatementCreator = preparedStatementCreator;
  }

  public MxExecuteDmlQuery(Connection connection, QueryDetails queryDetails) {
    this(connection, queryDetails, new PreparedStatementCreatorImpl());
  }

  public int execute() throws SQLException, MxDataTypeNotSupported {
    try (PreparedStatement preparedStatement = preparedStatementCreator.create(queryDetails.getQuery(), queryDetails.getQueryParameters(),
        connection)) {
      // Set auto-commit to false to start a transaction manually
      connection.setAutoCommit(false);

      int numberOfAffectedRows = preparedStatement.executeUpdate();

      // Roll back the transaction after successful execution to verify the query without committing changes
      connection.rollback();
      return numberOfAffectedRows;
    }
  }
}
