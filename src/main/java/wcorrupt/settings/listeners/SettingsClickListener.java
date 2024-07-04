package wcorrupt.settings.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wcorrupt.settings.Settings;
import wcorrupt.settings.util.PlayerSettingsManager;

public class SettingsClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Settings")) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        PlayerSettingsManager manager = Settings.getInstance().getPlayerSettingsManager();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            return;
        }

        int slot = event.getSlot();
        String option = null;

        switch (slot) {
            case 10:
            case 19:
                option = "death";
                break;
            case 12:
            case 21:
                option = "join";
                break;
            case 14:
            case 23:
                option = "vote";
                break;
            case 16:
            case 25:
                option = "public_chat";
                break;
        }

        if (option != null) {
            manager.toggleSetting(player, option);
            updateGUI(player.getOpenInventory().getTopInventory(), player);
        }
    }

    private void updateGUI(Inventory inv, Player player) {
        PlayerSettingsManager manager = Settings.getInstance().getPlayerSettingsManager();
        setOptionItem(inv, 10, "Death Messages", manager.isSettingEnabled(player, "death"));
        setOptionItem(inv, 12, "Join/Leave Messages", manager.isSettingEnabled(player, "join"));
        setOptionItem(inv, 14, "Vote Messages", manager.isSettingEnabled(player, "vote"));
        setOptionItem(inv, 16, "Public Chat", manager.isSettingEnabled(player, "public_chat"));
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
