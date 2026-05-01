// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.commands;

import fr.easytpa.EasyTPA;
import fr.easytpa.utils.PermissionUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class WarpCommand implements CommandExecutor, TabCompleter {

    private final EasyTPA plugin;

    public WarpCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        if (!PermissionUtils.hasPermission(player, "teleport.warp", plugin)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessageManager().send(player, "invalid-usage", Map.of("usage", "/" + label + " <nom>"));
            return true;
        }

        String warpName = args[0].toLowerCase();
        Location dest = plugin.getWarpManager().getWarp(warpName);

        if (dest == null) {
            plugin.getMessageManager().send(player, "warp-not-found", Map.of("warp", args[0]));
            return true;
        }

        plugin.getDelayManager().scheduleTeleport(player, dest, success -> {
            if (success) plugin.getMessageManager().send(player, "warp-success", Map.of("warp", warpName));
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return plugin.getWarpManager().getWarpNames().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
