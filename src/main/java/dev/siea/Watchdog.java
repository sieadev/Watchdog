package dev.siea;


import dev.siea.commands.CheckCommand;
import dev.siea.commands.CommandManager;
import dev.siea.commands.HelpCommand;
import dev.siea.commands.ReportCommand;
import dev.siea.config.ConfigUtil;
import dev.siea.database.MySQLWrapper;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.simpleyaml.configuration.ConfigurationSection;

/**
 * The Watchdog class is responsible for initializing and managing the Discord bot and database connection.
 */
public class Watchdog {
    private static Watchdog instance;
    private final ShardManager shardManager;
    private final MySQLWrapper databaseWrapper;

    /**
     * Constructs a Watchdog instance, initializing the Discord bot and the MySQL database connection using configurations
     * from the config.yml file.
     */
    public Watchdog(){
        ConfigurationSection config = new ConfigUtil("/config.yml").getConfig();

        String token = config.getString("token");
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token).enableIntents(GatewayIntent.MESSAGE_CONTENT).enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.watching("You!"));
        shardManager = builder.build();

        String url = "jdbc:mysql://" + config.getString("sql.ip") + "/" + config.getString("sql.name");
        String user = config.getString("sql.user");
        String pass = config.getString("sql.pass");
        databaseWrapper = new MySQLWrapper(url, user, pass);

        CommandManager commandManager = new CommandManager();

        shardManager.addEventListener(new CommandManager());
        commandManager.registerCommand(new CheckCommand(databaseWrapper));
        commandManager.registerCommand(new ReportCommand(databaseWrapper));
        commandManager.registerCommand(new HelpCommand());
    }

    /**
     * The main method serves as the entry point for the Watchdog application.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        instance = new Watchdog();
    }
}
