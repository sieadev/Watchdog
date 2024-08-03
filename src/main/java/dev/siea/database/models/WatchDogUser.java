package dev.siea.database.models;
import java.util.HashMap;

/**
 * The WatchDogUser record represents a user with an ID and a map of report IDs to their corresponding report types.
 *
 * @param id      the unique identifier of the user.
 * @param reports a map where the key is the report ID, and the value is the ReportType, representing the user's reports.
 */
public record WatchDogUser (String id, HashMap<String, ReportType> reports) {

}
