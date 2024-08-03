package dev.siea.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a command that can be executed in response to a slash command interaction in Discord.
 * Implementations of this interface define specific commands and how they are executed when invoked.
 */
public interface WatchdogCommand {

    /**
     * Returns the name of the command.
     * This name is used to identify the command in slash command interactions.
     *
     * @return the name of the command
     */
    String getName();

    /**
     * Executes the command when a slash command interaction is received.
     * This method is called when the command with the name returned by {@link #getName()} is invoked.
     *
     * @param event the SlashCommandInteractionEvent containing information about the command interaction
     */
    void execute(@NotNull SlashCommandInteractionEvent event);
}