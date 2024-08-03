package dev.siea.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.siea.database.models.Report;
import dev.siea.database.models.ReportQuery;
import dev.siea.database.models.ReportType;
import dev.siea.database.models.WatchDogUser;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.HashMap;

/**
 * This class is responsible for managing the interaction with a MySQL database, including creating tables,
 * retrieving and submitting reports, and fetching user data.
 */
public class MySQLWrapper {
    private final HikariDataSource dataSource;

    /**
     * Constructs a MySQLWrapper instance and initializes the HikariDataSource with the given database credentials.
     *
     * @param url      the JDBC URL of the database.
     * @param username the database username.
     * @param password the database password.
     */
    public MySQLWrapper(@NotNull String url, @NotNull String username, @NotNull String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        try {
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            System.out.println("[Watchdog] Unable to connect to MySQL database.");
            throw new RuntimeException(e);
        }
        System.out.println("[Watchdog] Connected to MySQL database.");
        createReportsTable();
    }

    /**
     * Creates the "reports" table if it does not already exist.
     */
    private void createReportsTable(){
        try (Connection connection = dataSource.getConnection()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS reports (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "reported_user_id VARCHAR(24) NOT NULL," +
                    "reporter_user_id VARCHAR(24) NOT NULL," +
                    "report_type_id VARCHAR(128) NOT NULL," +
                    "description TEXT," +
                    "reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createTableSQL);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Retrieves a WatchDogUser object with their report IDs and types by their user ID.
     *
     * @param id the user ID.
     * @return a WatchDogUser object containing the user ID and a map of report IDs and their types.
     */
    public WatchDogUser getWatchdogUser(@NotNull String id) {
        String query = "SELECT id, report_type_id FROM reports WHERE reported_user_id = ?";
        HashMap<String, ReportType> reportIDs = new HashMap<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String reportId = resultSet.getString("id");
                    String reportTypeStr = resultSet.getString("report_type_id");
                    ReportType reportType = ReportType.valueOf(reportTypeStr);
                    reportIDs.put(reportId, reportType);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[Watchdog] Invalid report type found in database for user " + id);
        }

        return new WatchDogUser(id, reportIDs);
    }

    /**
     * Submits a report to the database and returns a result code.
     *
     * @param reportQuery a ReportQuery object containing the report details.
     * @return an integer code indicating the result of the operation.
     */
    public int submitReport(@NotNull ReportQuery reportQuery) {
        String insertSQL = "INSERT INTO reports (reported_user_id, reporter_user_id, report_type_id, description) VALUES (?, ?, ?, ?)";
        String checkReportSQL = "SELECT COUNT(*) FROM reports WHERE reported_user_id = ? AND reporter_user_id = ? AND report_type_id = ?";
        String checkReportsCountSQL = "SELECT COUNT(*) FROM reports WHERE reporter_user_id = ? AND reported_at >= NOW() - INTERVAL 24 HOUR";

        try (Connection connection = dataSource.getConnection()) {

            // Check if the user has already reported the same target with the same reason
            try (PreparedStatement checkReportStmt = connection.prepareStatement(checkReportSQL)) {
                checkReportStmt.setString(1, reportQuery.userID());
                checkReportStmt.setString(2, reportQuery.reporterID());
                checkReportStmt.setString(3, reportQuery.type().name());

                try (ResultSet resultSet = checkReportStmt.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        return 409;
                    }
                }
            }

            // Check if the user has exceeded the maximum reports in the last 24 hours
            try (PreparedStatement checkReportsCountStmt = connection.prepareStatement(checkReportsCountSQL)) {
                checkReportsCountStmt.setString(1, reportQuery.reporterID());

                try (ResultSet resultSet = checkReportsCountStmt.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) >= 5) { // Assume 5 is the max reports in 24 hours
                        return 403;
                    }
                }
            }

            // If checks pass, insert the report
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, reportQuery.userID());
                preparedStatement.setString(2, reportQuery.reporterID());
                preparedStatement.setString(3, reportQuery.type().name());
                preparedStatement.setString(4, reportQuery.description());

                preparedStatement.executeUpdate();
                return 200;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1; // Indicate a generic SQL error
        }
    }

    /**
     * Retrieves a Report object by its report ID.
     *
     * @param reportID the report ID.
     * @return a Report object containing the report details, or null if the report is not found.
     */
    public Report retrieveReportById(@NotNull String reportID) {
        String query = "SELECT reported_user_id, reporter_user_id, report_type_id, description FROM reports WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, reportID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String userID = resultSet.getString("reported_user_id");
                    String reporterID = resultSet.getString("reporter_user_id");
                    String reportTypeStr = resultSet.getString("report_type_id");
                    ReportType reportType = ReportType.valueOf(reportTypeStr);
                    String description = resultSet.getString("description");

                    return new Report(reportID, userID, reporterID, reportType, description);
                } else {
                    System.out.println("No report found with ID: " + reportID);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("[Watchdog] Invalid report type found in database for report ID " + reportID);
        }

        return null;
    }

    /**
     * Closes the HikariDataSource and releases any database connections.
     */
    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
