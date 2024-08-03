package dev.siea.commands;

import dev.siea.database.MySQLWrapper;
import dev.siea.database.models.ReportQuery;
import dev.siea.database.models.ReportType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

/**
 * The ReportCommand class handles the execution of the "report" slash command.
 * It interacts with the MySQL database to record user reports and sends an embedded response to the user.
 */
public class ReportCommand implements WatchdogCommand {
    private final MySQLWrapper databaseWrapper;

    /**
     * Constructs a ReportCommand with the specified database wrapper.
     *
     * @param databaseWrapper the MySQLWrapper instance used for database operations
     */
    public ReportCommand(MySQLWrapper databaseWrapper) {
        this.databaseWrapper = databaseWrapper;
    }

    /**
     * Returns the name of the command, which is "report".
     *
     * @return the name of the command
     */
    @Override
    public String getName() {
        return "report";
    }

    /**
     * Executes the "report" command when a slash command interaction is received.
     * It records a report in the database and sends a response to the user.
     *
     * @param event the SlashCommandInteractionEvent containing information about the command interaction
     */
    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        User target = Objects.requireNonNull(event.getOption("user")).getAsUser();
        User reporter = event.getUser();
        ReportType type = ReportType.valueOf(Objects.requireNonNull(event.getOption("reportType")).getAsString());

        ReportQuery query = new ReportQuery(target.getId(), reporter.getId(), type, "No description.");

        databaseWrapper.submitReport(query);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Report Submitted")
                .setDescription("Your report has been successfully submitted.")
                .setColor(Color.GREEN)
                .addField("Reported User", target.getAsMention(), true)
                .addField("Report Type", type.name().replaceAll("_", " "), true)
                .addField("Reporter", reporter.getAsMention(), true)
                .setFooter("Thank you for helping us keep the community safe.")
                .setTimestamp(event.getInteraction().getTimeCreated());

        event.replyEmbeds(embed.build()).queue();
    }
}
