package wcorrupt.settings.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wcorrupt.settings.Settings;
import wcorrupt.settings.util.PlayerSettingsManager;

public class UserVotedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /uservoted <player> <text>");
            return true;
        }

        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            sender.sendMessage("Player not found.");
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        Component finalMessage = Component.text("Thanks ", NamedTextColor.GREEN)
                .append(Component.text(playerName, NamedTextColor.RED))
                .append(Component.text(" for voting on ", NamedTextColor.GREEN))
                .append(Component.text(message.toString().trim(), NamedTextColor.RED));

        PlayerSettingsManager manager = Settings.getInstance().getPlayerSettingsManager();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (manager.isSettingEnabled(p, "vote")) {
                p.sendMessage(finalMessage);
            }
        }

        return true;
    }
}
