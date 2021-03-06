package no.fractal.database;

import no.fractal.debugLogger.DebugLogger;
import no.fractal.util.ThrowingConsumer;

import java.sql.*;

/**
 * PostgreSQL connection and executor.
 * It is responsible for connecting to the database and execute queries.
 */
abstract class PsqlDb {

    protected static final DebugLogger allQueries = new DebugLogger(false);
    protected static final DebugLogger errorQueries = new DebugLogger(true);
    private static final String url = System.getenv("SQLURL"); //"jdbc:postgresql://10.10.50.50/fractal";
    private static final String dbUser = System.getenv("USER_USERNAME");
    private static final String dbPassword = System.getenv("USER_PASSWORD");

    protected static Connection tryConnectToDB() throws SQLException {
        allQueries.log("try connect to db", "url", url, "user", dbUser, "passwd", dbPassword);
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver"); // i think this is to chek if the class exists
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Executes a sql query against the database.
     * It establishes a connection to the database, and executes the query.
     * @param query the query to execute.
     * @param rowHandler query response handler
     * @throws SQLException thrown if problems with the database
     */
    protected static void sqlQuery(String query, ThrowingConsumer<ResultSet, SQLException> rowHandler)
            throws SQLException {

        Connection connection = tryConnectToDB();
        Statement statement = connection.createStatement();

        allQueries.log("making SQL query:\n", query);
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            rowHandler.accept(resultSet);
        }

        resultSet.close();
        statement.close();
        connection.close();

    }

    /**
     * Performs an sql query against the database, without result handling.
     *
     * @param query the query to execute.
     * @throws SQLException thrown if problems with database
     */
    protected static void sqlUpdate(String query) throws SQLException {
        Connection connection = tryConnectToDB();
        Statement statement = connection.createStatement();

        allQueries.log("making SQL update:\n", query);
        statement.executeUpdate(query);

        statement.close();
        connection.close();

    }
}
