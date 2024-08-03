package dev.siea.commands;

import dev.siea.database.MySQLWrapper;
import dev.siea.database.models.ReportType;
import dev.siea.database.models.WatchDogUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.Map;
import java.util.Objects;

/**
 * The CheckCommand class handles the execution of the "check" slash command.
 * It retrieves and displays the report history of a specified user from the database.
 */
public class CheckCommand implements WatchdogCommand {
    private final MySQLWrapper databaseWrapper;

    /**
     * Constructs a CheckCommand with the specified database wrapper.
     *
     * @param databaseWrapper the MySQLWrapper instance used for database operations
     */
    public CheckCommand(MySQLWrapper databaseWrapper) {
        this.databaseWrapper = databaseWrapper;
    }

    /**
     * Returns the name of the command, which is "check".
     *
     * @return the name of the command
     */
    @Override
    public String getName() {
        return "check";
    }

    /**
     * Executes the "check" command when a slash command interaction is received.
     * It retrieves the report history for the specified user and sends an embedded response.
     *
     * @param event the SlashCommandInteractionEvent containing information about the command interaction
     */
    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        User target = Objects.requireNonNull(event.getOption("user")).getAsUser();
        WatchDogUser watchdogUser = databaseWrapper.getWatchdogUser(target.getId());

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("User Report History")
                .setColor(Color.BLUE)
                .setDescription("Here is the report history for " + target.getAsMention())
                .setTimestamp(event.getInteraction().getTimeCreated());

        if (watchdogUser.reports().isEmpty()) {
            embed.addField("Report Status", "No reports found for this user.", false);
        } else {
            StringBuilder reportDetails = new StringBuilder();
            for (Map.Entry<String, ReportType> entry : watchdogUser.reports().entrySet()) {
                String reportId = entry.getKey();
                ReportType reportType = entry.getValue();
                reportDetails.append("Report ID: ").append(reportId)
                        .append("\nType: ").append(reportType.name().replaceAll("_", " "))
                        .append("\n\n");
            }
            embed.addField("Report Details", reportDetails.toString(), false);
        }

        event.replyEmbeds(embed.build()).queue();
    }
}
