package wcorrupt.settings;

import org.bukkit.plugin.java.JavaPlugin;
import wcorrupt.settings.commands.SettingsCommand;
import wcorrupt.settings.commands.UserVotedCommand;
import wcorrupt.settings.listeners.PlayerEventListener;
import wcorrupt.settings.listeners.SettingsClickListener;
import wcorrupt.settings.util.PlayerSettingsManager;

public class Settings extends JavaPlugin {

    private static Settings instance;
    private PlayerSettingsManager playerSettingsManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        playerSettingsManager = new PlayerSettingsManager();
        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);
        getServer().getPluginManager().registerEvents(new SettingsClickListener(), this);
        getCommand("settings").setExecutor(new SettingsCommand());
        getCommand("uservoted").setExecutor(new UserVotedCommand());
    }

    public static Settings getInstance() {
        return instance;
    }

    public PlayerSettingsManager getPlayerSettingsManager() {
        return playerSettingsManager;
    }
}
