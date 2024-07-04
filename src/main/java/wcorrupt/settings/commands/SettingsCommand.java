package wcorrupt.settings.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import wcorrupt.settings.Settings;
import wcorrupt.settings.util.PlayerSettingsManager;

public class SettingsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        openSettingsGUI(player);
        return true;
    }

    private void openSettingsGUI(Player player) {
        PlayerSettingsManager manager = Settings.getInstance().getPlayerSettingsManager();
        Inventory inv = Bukkit.createInventory(null, 36, "Settings");

        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glassPane.setItemMeta(glassMeta);
        }

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, glassPane);
        }

        setOptionItem(inv, 10, "Death Messages", manager.isSettingEnabled(player, "death"));
        setOptionItem(inv, 12, "Join/Leave Messages", manager.isSettingEnabled(player, "join"));
        setOptionItem(inv, 14, "Vote Messages", manager.isSettingEnabled(player, "vote"));
        setOptionItem(inv, 16, "Public Chat", manager.isSettingEnabled(player, "public_chat"));

        player.openInventory(inv);
    }

    private void setOptionItem(Inventory inv, int slot, String name, boolean enabled) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + name);
            item.setItemMeta(meta);
        }

        inv.setItem(slot, item);

        ItemStack dye = new ItemStack(enabled ? Material.LIME_DYE : Material.RED_DYE);
        ItemMeta dyeMeta = dye.getItemMeta();
        if (dyeMeta != null) {
            dyeMeta.setDisplayName(enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled");
            dye.setItemMeta(dyeMeta);
        }
        inv.setItem(slot + 9, dye);
    }
}
