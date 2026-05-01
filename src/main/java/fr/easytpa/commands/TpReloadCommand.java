// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.commands;

import fr.easytpa.EasyTPA;
import fr.easytpa.utils.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TpReloadCommand implements CommandExecutor {

    private final EasyTPA plugin;

    public TpReloadCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!PermissionUtils.hasAdminPermission(player, "teleport.reload")) {
                plugin.getMessageManager().send(player, "no-permission");
                return true;
            }
        } else if (!sender.isOp()) {
            sender.sendMessage("Vous n'avez pas la permission.");
            return true;
        }

        plugin.reload();
        plugin.getMessageManager().send(sender, "reload-success");

        return true;
    }
}
