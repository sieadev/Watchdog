package dev.siea.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

/**
 * The HelpCommand class handles the execution of the "help" slash command.
 * It provides users with a list of all available commands and their descriptions.
 */
public class HelpCommand implements WatchdogCommand {

    /**
     * Returns the name of the command, which is "help".
     *
     * @return the name of the command
     */
    @Override
    public String getName() {
        return "help";
    }

    /**
     * Executes the "help" command when a slash command interaction is received.
     * It sends an embedded response listing all available commands and their descriptions.
     *
     * @param event the SlashCommandInteractionEvent containing information about the command interaction
     */
    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Available Commands")
                .setColor(Color.GREEN)
                .setDescription("Here is a list of all available commands and their descriptions:");

        embed.addField("/report", "Report a user for inappropriate behavior.", false);
        embed.addField("/check", "Check the report history of a user.", false);
        embed.addField("/help", "Display a list of available commands.", false);

        event.replyEmbeds(embed.build()).queue();
    }
}