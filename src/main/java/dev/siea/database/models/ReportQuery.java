package dev.siea.database.models;

/**
 * The ReportQuery record represents a query for submitting a report against a user.
 *
 * @param userID      the unique identifier of the user being reported.
 * @param reporterID  the unique identifier of the user submitting the report.
 * @param type        the type of report being submitted, as defined by {@link ReportType}.
 * @param description a detailed description of the report.
 */
public record ReportQuery(String userID, String reporterID, ReportType type, String description) {
}
