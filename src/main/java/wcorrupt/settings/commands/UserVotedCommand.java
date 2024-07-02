package wcorrupt.settings.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wcorrupt.settings.Settings;
import wcorrupt.settings.util.PlayerSettingsManager;

public class UserVotedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

        String finalMessage = ChatColor.GREEN + "Thanks " + ChatColor.RED + playerName + ChatColor.GREEN + " for voting on " + ChatColor.RED + message.toString().trim();
        PlayerSettingsManager manager = Settings.getInstance().getPlayerSettingsManager();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (manager.isSettingEnabled(p, "vote")) {
                p.sendMessage(finalMessage);
            }
        }

        return true;
    }
}
