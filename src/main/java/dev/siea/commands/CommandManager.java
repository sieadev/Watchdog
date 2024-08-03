package dev.siea.commands;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

/**
 * CommandManager is responsible for managing and registering slash commands within a Discord guild.
 * It handles the registration of commands when the bot joins a new guild or when the guild is ready.
 * It also processes interactions with slash commands.
 */
public class CommandManager extends ListenerAdapter {
    private final ArrayList<CommandData> commandDataList = new ArrayList<>();
    private final ArrayList<WatchdogCommand> commands = new ArrayList<>();

    /**
     * Constructs a CommandManager instance and initializes the command data list with the available commands.
     * Sets up commands for reporting a user, checking a user's report, and a help command.
     */
    public CommandManager() {
        OptionData userOption = new OptionData(OptionType.USER, "user", "Select a member!", true);
        OptionData reportTypeOption = new OptionData(OptionType.STRING, "report_type", "Select a report type!", true)
                .addChoice("Cheating in Video Game", "CHEATING_IN_VIDEO_GAME")
                .addChoice("Doxxing (Publicizing Private Information)", "DOXXING")
                .addChoice("Scamming or Fraud", "SCAMMING")
                .addChoice("Malicious Media (Links, Texts, Visual Material)", "MALICIOUS_MEDIA")
                .addChoice("Hate Speech", "HATE_SPEECH")
                .addChoice("Bullying or Harassment", "BULLYING")
                .addChoice("Threats of Violence", "THREATS_OF_VIOLENCE")
                .addChoice("Illegal Activity", "ILLEGAL_ACTIVITY");

        CommandData reportCommand = Commands.slash("report", "Report a User!")
                .addOptions(userOption, reportTypeOption);
        CommandData checkCommand = Commands.slash("check", "Check a Users report")
                .addOptions(userOption);
        CommandData helpCommand = Commands.slash("help", "Help Command");

        commandDataList.add(reportCommand);
        commandDataList.add(checkCommand);
        commandDataList.add(helpCommand);
    }

    /**
     * This method is called when the guild is fully loaded and ready.
     * It registers the slash commands with the guild.
     *
     * @param event the GuildReadyEvent containing information about the guild that is ready
     */
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        event.getGuild().updateCommands()
                .addCommands(commandDataList)
                .queue();
    }

    /**
     * This method is called when the bot joins a new guild.
     * It registers the slash commands with the new guild.
     *
     * @param event the GuildJoinEvent containing information about the guild the bot has joined
     */
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        event.getGuild().updateCommands()
                .addCommands(commandDataList)
                .queue();
    }

    /**
     * This method is called when a slash command interaction is received.
     * It delegates the command execution to the appropriate {@link WatchdogCommand} based on the command name.
     *
     * @param event the SlashCommandInteractionEvent containing information about the received command interaction
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (WatchdogCommand command : commands) {
            if (command.getName().equalsIgnoreCase(event.getName())) {
                command.execute(event);
                break;
            }
        }
    }

    /**
     * Registers a custom {@link WatchdogCommand} with the CommandManager.
     * The registered command will be handled in the {@link #onSlashCommandInteraction(SlashCommandInteractionEvent)} method.
     *
     * @param watchdogCommand the {@link WatchdogCommand} to register
     */
    public void registerCommand(WatchdogCommand watchdogCommand) {
        commands.add(watchdogCommand);
    }
}
