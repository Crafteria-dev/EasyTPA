// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.commands;

import fr.easytpa.EasyTPA;
import fr.easytpa.utils.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {

    private final EasyTPA plugin;

    public SpawnCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        if (!PermissionUtils.hasPermission(player, "teleport.spawn", plugin)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        if (!plugin.getSpawnManager().hasSpawn()) {
            plugin.getMessageManager().send(player, "spawn-not-set");
            return true;
        }

        plugin.getDelayManager().scheduleTeleport(player, plugin.getSpawnManager().getSpawn(), success -> {
            if (success) plugin.getMessageManager().send(player, "spawn-success");
        });

        return true;
    }
}
