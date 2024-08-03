package dev.siea.database.models;

/**
 * The Report record represents a report made against a user.
 *
 * @param reportID    the unique identifier of the report.
 * @param userID      the unique identifier of the user being reported.
 * @param reporterID  the unique identifier of the user submitting the report.
 * @param type        the type of report being submitted, as defined by {@link ReportType}.
 * @param description a detailed description of the report.
 */
public record Report(String reportID, String userID, String reporterID, ReportType type, String description) {
}
