package wcorrupt.settings.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wcorrupt.settings.Settings;
import wcorrupt.settings.util.PlayerSettingsManager;

import java.lang.reflect.Method;

public class PlayerEventListener implements Listener {

    private final PlayerSettingsManager manager = Settings.getInstance().getPlayerSettingsManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null); // Cancel the default join message

        String joinMessage = ChatColor.YELLOW + player.getName() + " joined the game.";
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (manager.isSettingEnabled(p, "join")) {
                p.sendMessage(joinMessage);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null); // Cancel the default quit message

        String quitMessage = ChatColor.YELLOW + player.getName() + " left the game.";
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (manager.isSettingEnabled(p, "join")) {
                p.sendMessage(quitMessage);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null); // Cancel the default death message

        String deathMessage = generateDeathMessage(player, event);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (manager.isSettingEnabled(p, "death")) {
                p.sendMessage(deathMessage);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        boolean isPublicChatEnabled = manager.isSettingEnabled(player, "public_chat");

        try {
            Class<?> chatClass = Class.forName("phonon.nodes.chat.Chat");
            Method enableGlobalChat = chatClass.getMethod("enableGlobalChat", Player.class);
            Method disableGlobalChat = chatClass.getMethod("disableGlobalChat", Player.class);

            if (isPublicChatEnabled) {
                enableGlobalChat.invoke(null, player);
            } else {
                disableGlobalChat.invoke(null, player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Class<?> chatClass = Class.forName("phonon.nodes.chat.Chat");
            Method process = chatClass.getMethod("process", AsyncPlayerChatEvent.class);
            process.invoke(null, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateDeathMessage(Player player, PlayerDeathEvent event) {
        EntityDamageEvent lastDamage = player.getLastDamageCause();
        EntityDamageEvent.DamageCause cause = lastDamage != null ? lastDamage.getCause() : null;
        String playerName = player.getName();

        if (cause == null) {
            return ChatColor.WHITE + playerName + " died";
        }

        switch (cause) {
            case ENTITY_ATTACK:
            case ENTITY_SWEEP_ATTACK:
                Entity attacker = player.getKiller();
                if (attacker != null && attacker.getType() == EntityType.PLAYER) {
                    Player killer = (Player) attacker;
                    ItemStack weapon = killer.getInventory().getItemInMainHand();
                    if (weapon.getType() != Material.AIR) {
                        return getWeaponDeathMessage(playerName, killer.getName(), weapon);
                    } else {
                        return ChatColor.WHITE + playerName + " was slain by " + killer.getName();
                    }
                }
                return ChatColor.WHITE + playerName + " was slain by an entity";
            case PROJECTILE:
                Entity shooter = player.getKiller();
                if (shooter != null && shooter.getType() == EntityType.PLAYER) {
                    Player killer = (Player) shooter;
                    ItemStack weapon = killer.getInventory().getItemInMainHand();
                    return getWeaponDeathMessage(playerName, killer.getName(), weapon);
                }
                return ChatColor.WHITE + playerName + " was shot by an entity";
            case FALL:
                return ChatColor.WHITE + playerName + " fell from a high place";
            case SUFFOCATION:
                return ChatColor.WHITE + playerName + " suffocated in a wall";
            case DROWNING:
                return ChatColor.WHITE + playerName + " drowned";
            case FIRE:
            case FIRE_TICK:
                return ChatColor.WHITE + playerName + " burned to death";
            case LAVA:
                return ChatColor.WHITE + playerName + " tried to swim in lava";
            case STARVATION:
                return ChatColor.WHITE + playerName + " starved to death";
            case ENTITY_EXPLOSION:
                return ChatColor.WHITE + playerName + " was blown up";
            case CONTACT:
                return ChatColor.WHITE + playerName + " was pricked to death";
            case MAGIC:
                return ChatColor.WHITE + playerName + " was killed by magic";
            case WITHER:
                return ChatColor.WHITE + playerName + " withered away";
            default:
                return ChatColor.WHITE + playerName + " died";
        }
    }

    private String getWeaponDeathMessage(String playerName, String killerName, ItemStack weapon) {
        ItemMeta meta = weapon.getItemMeta();
        String weaponName;
        if (meta != null && meta.hasDisplayName()) {
            weaponName = meta.getDisplayName();
        } else {
            weaponName = weapon.getType().name().replace('_', ' ').toLowerCase();
        }

        return ChatColor.WHITE + playerName + " was slain by " + killerName + " using [" + ChatColor.AQUA + weaponName + ChatColor.WHITE + "]";
    }
}
