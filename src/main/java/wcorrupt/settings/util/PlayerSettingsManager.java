package wcorrupt.settings.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import wcorrupt.settings.Settings;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSettingsManager {

    private final Map<UUID, Map<String, Boolean>> playerSettings = new HashMap<>();

    public PlayerSettingsManager() {
        loadSettings();
    }

    public void toggleSetting(Player player, String setting) {
        UUID playerId = player.getUniqueId();
        Map<String, Boolean> settings = playerSettings.getOrDefault(playerId, new HashMap<>());
        settings.put(setting, !settings.getOrDefault(setting, true));
        playerSettings.put(playerId, settings);
        saveSettings(player);
    }

    public boolean isSettingEnabled(Player player, String setting) {
        return playerSettings.getOrDefault(player.getUniqueId(), getDefaultSettings()).getOrDefault(setting, true);
    }

    private void loadSettings() {
        FileConfiguration config = Settings.getInstance().getConfig();
        for (String key : config.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(key);
                Map<String, Boolean> settings = new HashMap<>();
                if (config.getConfigurationSection(key) != null) {
                    for (String setting : config.getConfigurationSection(key).getKeys(false)) {
                        settings.put(setting, config.getBoolean(key + "." + setting));
                    }
                }
                playerSettings.put(playerId, settings);
            } catch (IllegalArgumentException e) {
                Settings.getInstance().getLogger().warning("Invalid UUID string in config: " + key);
            }
        }
    }

    private void saveSettings(Player player) {
        FileConfiguration config = Settings.getInstance().getConfig();
        UUID playerId = player.getUniqueId();
        Map<String, Boolean> settings = playerSettings.get(playerId);
        for (Map.Entry<String, Boolean> entry : settings.entrySet()) {
            config.set(playerId.toString() + "." + entry.getKey(), entry.getValue());
        }
        Settings.getInstance().saveConfig();
    }

    private Map<String, Boolean> getDefaultSettings() {
        Map<String, Boolean> defaultSettings = new HashMap<>();
        defaultSettings.put("death", true);
        defaultSettings.put("join", true);
        defaultSettings.put("vote", true);
        return defaultSettings;
    }
}
