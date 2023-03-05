package nl.itz_kiwisap_.devroom.banplugin;

import lombok.Getter;
import nl.itz_kiwisap_.devroom.banplugin.commands.BanCommand;
import nl.itz_kiwisap_.devroom.banplugin.commands.HistoryCommand;
import nl.itz_kiwisap_.devroom.banplugin.commands.UnbanCommand;
import nl.itz_kiwisap_.devroom.banplugin.database.SQLDatabase;
import nl.itz_kiwisap_.devroom.banplugin.database.query.queries.TableCreationQuery;
import nl.itz_kiwisap_.devroom.banplugin.handler.BanHandler;
import nl.itz_kiwisap_.devroom.banplugin.listeners.PlayerBanListener;
import nl.itz_kiwisap_.devroom.banplugin.menus.HistoryMenuReader;
import nl.itz_kiwisap_.devroom.banplugin.menus.provider.BanPluginDefaultItemProvider;
import nl.odalitadevelopments.menus.OdalitaMenus;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class BanPlugin extends JavaPlugin {

    @Getter
    private static BanPlugin instance;

    private SQLDatabase database;
    private BanHandler banHandler;

    private OdalitaMenus odalitaMenus;

    private HistoryMenuReader historyMenuReader;

    @Override
    public void onEnable() {
        instance = this;

        try {
            super.saveDefaultConfig();
            FileConfiguration config = super.getConfig();

            this.database = new SQLDatabase(
                    config.getString("database.host"),
                    config.getInt("database.port"),
                    config.getString("database.database"),
                    config.getString("database.username"),
                    config.getString("database.password")
            );

            new TableCreationQuery().execute(this.database);
        } catch (Exception exception) {
            exception.printStackTrace();
            super.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.banHandler = new BanHandler(this);

        this.odalitaMenus = OdalitaMenus.createInstance(this);
        this.odalitaMenus.getProvidersContainer().setDefaultItemProvider(new BanPluginDefaultItemProvider(this));

        this.historyMenuReader = new HistoryMenuReader(this);

        this.registerCommand("ban", new BanCommand(this));
        this.registerCommand("history", new HistoryCommand(this));
        this.registerCommand("unban", new UnbanCommand(this));

        super.getServer().getPluginManager().registerEvents(new PlayerBanListener(this), this);
    }

    @Override
    public void onDisable() {
        if (this.database != null) {
            this.database.disconnect();
        }

        instance = null;
    }

    private void registerCommand(String command, CommandExecutor executor) {
        PluginCommand cmd = super.getCommand(command);
        if (cmd == null) {
            throw new IllegalArgumentException("Command " + command + " not found!");
        }

        cmd.setExecutor(executor);
        if (executor instanceof TabCompleter tabCompleter) {
            cmd.setTabCompleter(tabCompleter);
        }
    }
}