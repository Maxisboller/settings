package wcorrupt.settings.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wcorrupt.settings.Settings;
import wcorrupt.settings.util.PlayerSettingsManager;

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

        BaseComponent deathMessage = generateDeathMessage(player, event);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (manager.isSettingEnabled(p, "death")) {
                p.spigot().sendMessage(deathMessage);
            }
        }
    }

    private BaseComponent generateDeathMessage(Player player, PlayerDeathEvent event) {
        EntityDamageEvent.DamageCause cause = player.getLastDamageCause().getCause();
        String playerName = player.getName();

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
                        return new TextComponent(ChatColor.WHITE + playerName + " was slain by " + killer.getName());
                    }
                }
                return new TextComponent(ChatColor.WHITE + playerName + " was slain by an entity");
            case PROJECTILE:
                Entity shooter = player.getKiller();
                if (shooter != null && shooter.getType() == EntityType.PLAYER) {
                    Player killer = (Player) shooter;
                    ItemStack weapon = killer.getInventory().getItemInMainHand();
                    return getWeaponDeathMessage(playerName, killer.getName(), weapon);
                }
                return new TextComponent(ChatColor.WHITE + playerName + " was shot by an entity");
            case FALL:
                return new TextComponent(ChatColor.WHITE + playerName + " fell from a high place");
            case SUFFOCATION:
                return new TextComponent(ChatColor.WHITE + playerName + " suffocated in a wall");
            case DROWNING:
                return new TextComponent(ChatColor.WHITE + playerName + " drowned");
            case FIRE:
            case FIRE_TICK:
                return new TextComponent(ChatColor.WHITE + playerName + " burned to death");
            case LAVA:
                return new TextComponent(ChatColor.WHITE + playerName + " tried to swim in lava");
            case STARVATION:
                return new TextComponent(ChatColor.WHITE + playerName + " starved to death");
            case ENTITY_EXPLOSION:
                return new TextComponent(ChatColor.WHITE + playerName + " was blown up");
            case CONTACT:
                return new TextComponent(ChatColor.WHITE + playerName + " was pricked to death");
            case MAGIC:
                return new TextComponent(ChatColor.WHITE + playerName + " was killed by magic");
            case WITHER:
                return new TextComponent(ChatColor.WHITE + playerName + " withered away");
            default:
                return new TextComponent(ChatColor.WHITE + playerName + " died");
        }
    }

    private BaseComponent getWeaponDeathMessage(String playerName, String killerName, ItemStack weapon) {
        ItemMeta meta = weapon.getItemMeta();
        String weaponName;
        if (meta != null && meta.hasDisplayName()) {
            weaponName = meta.getDisplayName().toLowerCase();
        } else {
            weaponName = weapon.getType().name().replace('_', ' ').toLowerCase();
        }

        TextComponent weaponComponent = new TextComponent("[" + weaponName + "]");
        weaponComponent.setColor(ChatColor.AQUA);
        weaponComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(weapon.getType().getKey().toString(), weapon.getAmount(), null)));

        ComponentBuilder builder = new ComponentBuilder(ChatColor.WHITE + playerName + " was slain by " + killerName + " using ");
        builder.append(weaponComponent);

        return new TextComponent(builder.create());
    }
}
