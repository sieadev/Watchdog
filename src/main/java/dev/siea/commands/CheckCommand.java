package dev.siea.commands;

import dev.siea.database.MySQLWrapper;
import dev.siea.database.models.ReportType;
import dev.siea.database.models.WatchDogUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.HashMap;
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
                .setThumbnail(target.getEffectiveAvatarUrl())
                .setAuthor(target.getAsTag(), null, target.getEffectiveAvatarUrl())
                .setTimestamp(event.getInteraction().getTimeCreated());

        int reportCount = watchdogUser.reports().size();
        if (reportCount == 0) {
            embed.setDescription(target.getAsMention() + " has never been reported using Watchdog.")
                    .setColor(Color.GREEN);
        } else if (reportCount < 10) {
            embed.setDescription(target.getAsMention() + " has been previously reported using Watchdog.")
                    .setColor(Color.YELLOW);
        } else {
            embed.setDescription(target.getAsMention() + " has been reported more than 10 times using Watchdog. Please exercise caution.")
                    .setColor(Color.RED);
        }

        if (!watchdogUser.reports().isEmpty()) {
            HashMap<String, Integer> reportsCount = new HashMap<>();
            for (Map.Entry<String, ReportType> entry : watchdogUser.reports().entrySet()) {
                reportsCount.put(entry.getValue().toString(), reportsCount.getOrDefault(entry.getValue().toString(), 0) + 1);
            }

            StringBuilder reportDetails = new StringBuilder();
            for (Map.Entry<String, Integer> entry : reportsCount.entrySet()) {
                reportDetails.append(entry.getValue()).append("x ").append(entry.getKey().replaceAll("_", " ")).append("\n");
            }

            embed.addField("Report Breakdown", reportDetails.toString(), false);
        }
        embed.setFooter("Stay safe <3.");
        event.replyEmbeds(embed.build()).queue();
    }
}