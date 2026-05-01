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

public class DelWarpCommand implements CommandExecutor, TabCompleter {

    private final EasyTPA plugin;

    public DelWarpCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessageManager().send(player, "invalid-usage", Map.of("usage", "/" + label + " <nom>"));
            return true;
        }

        String warpName = args[0].toLowerCase();

        if (!plugin.getWarpManager().hasWarp(warpName)) {
            plugin.getMessageManager().send(player, "warp-not-found", Map.of("warp", args[0]));
            return true;
        }

        boolean isAdmin = PermissionUtils.hasAdminPermission(player, "teleport.delwarp");

        if (!isAdmin) {
            UUID creator = plugin.getWarpManager().getCreator(warpName);
            if (!player.getUniqueId().equals(creator)) {
                plugin.getMessageManager().send(player, "warp-not-yours", Map.of("warp", warpName));
                return true;
            }
        }

        plugin.getWarpManager().deleteWarp(warpName);
        plugin.getMessageManager().send(player, "warp-deleted", Map.of("warp", warpName));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            boolean isAdmin = PermissionUtils.hasAdminPermission(player, "teleport.delwarp");
            return plugin.getWarpManager().getWarpNames().stream()
                    .filter(name -> isAdmin || player.getUniqueId().equals(plugin.getWarpManager().getCreator(name)))
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
