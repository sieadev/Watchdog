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
        ReportType type = ReportType.valueOf(Objects.requireNonNull(event.getOption("report_type")).getAsString());

        if (target.isBot()){
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Report Failed")
                    .setColor(Color.RED)
                    .setDescription("You may not report Bots");
            event.replyEmbeds(embed.build()).queue();
            return;
        }

        if (target == reporter){
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Report Failed")
                    .setColor(Color.RED)
                    .setDescription("You may not report yourself");
            event.replyEmbeds(embed.build()).queue();
            return;
        }

        ReportQuery query = new ReportQuery(target.getId(), reporter.getId(), type, "No description.");
        int result = databaseWrapper.submitReport(query);

        EmbedBuilder embed = new EmbedBuilder()
                .setTimestamp(event.getInteraction().getTimeCreated())
                .setFooter("Thank you for helping us keep the community safe.");

        switch (result) {
            case 200:
                embed.setTitle("Report Submitted")
                        .setDescription("Your report has been successfully submitted.")
                        .setColor(Color.GREEN)
                        .addField("Reported User", target.getAsMention(), true)
                        .addField("Report Type", type.name().replaceAll("_", " "), true)
                        .addField("Reporter", reporter.getAsMention(), true);
                event.replyEmbeds(embed.build()).queue();
                break;

            case 409:
                embed.setTitle("Report Failed")
                        .setDescription("You have already reported this user for this reason.")
                        .setColor(Color.RED);
                event.replyEmbeds(embed.build()).queue();
                break;

            case 429:
                embed.setTitle("Report Failed")
                        .setDescription("You can only report once per minute.")
                        .setColor(Color.RED);
                event.replyEmbeds(embed.build()).queue();
                break;

            case 403:
                embed.setTitle("Report Failed")
                        .setDescription("You can't report more than 5 users in the last 24 hours.")
                        .setColor(Color.RED);
                event.replyEmbeds(embed.build()).queue();
                break;

            default:
                embed.setTitle("Report Failed")
                        .setDescription("An unexpected error occurred while submitting your report.")
                        .setColor(Color.RED);
                event.replyEmbeds(embed.build()).queue();
                break;
        }
    }
}
