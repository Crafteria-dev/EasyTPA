// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.commands;

import fr.easytpa.EasyTPA;
import fr.easytpa.utils.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SetWarpCommand implements CommandExecutor, TabCompleter {

    private final EasyTPA plugin;

    public SetWarpCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        if (!PermissionUtils.hasPermission(player, "teleport.setwarp", plugin)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessageManager().send(player, "invalid-usage", Map.of("usage", "/" + label + " <nom>"));
            return true;
        }

        String warpName = args[0].toLowerCase();

        if (!warpName.matches("[a-z0-9_-]{1,32}")) {
            plugin.getMessageManager().send(player, "invalid-name");
            return true;
        }

        if (plugin.getWarpManager().hasWarp(warpName)) {
            // Overwrite : seul le créateur ou un admin peut écraser
            boolean isAdmin = PermissionUtils.hasAdminPermission(player, "teleport.setwarp");
            if (!isAdmin) {
                UUID creator = plugin.getWarpManager().getCreator(warpName);
                if (!player.getUniqueId().equals(creator)) {
                    plugin.getMessageManager().send(player, "warp-not-yours", Map.of("warp", warpName));
                    return true;
                }
            }
        } else {
            // Nouveau warp : vérifier la limite du joueur
            int current = plugin.getWarpManager().getWarpCountByPlayer(player.getUniqueId());
            int max = plugin.getConfigManager().getMaxWarps();
            if (current >= max) {
                plugin.getMessageManager().send(player, "warp-limit-reached", Map.of("max", String.valueOf(max)));
                return true;
            }
        }

        plugin.getWarpManager().setWarp(warpName, player.getLocation(), player.getUniqueId());
        plugin.getMessageManager().send(player, "warp-set", Map.of("warp", warpName));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            boolean isAdmin = PermissionUtils.hasAdminPermission(player, "teleport.setwarp");
            return plugin.getWarpManager().getWarpNames().stream()
                    .filter(name -> isAdmin || player.getUniqueId().equals(plugin.getWarpManager().getCreator(name)))
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
