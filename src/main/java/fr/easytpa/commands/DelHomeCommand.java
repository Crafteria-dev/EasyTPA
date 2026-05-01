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

public class DelHomeCommand implements CommandExecutor, TabCompleter {

    private final EasyTPA plugin;

    public DelHomeCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        if (!PermissionUtils.hasPermission(player, "teleport.delhome", plugin)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        String homeName = args.length > 0 ? args[0].toLowerCase() : "default";

        boolean deleted = plugin.getHomeManager().deleteHome(player.getUniqueId(), homeName);
        if (!deleted) {
            plugin.getMessageManager().send(player, "home-not-found", Map.of("home", homeName));
            return true;
        }

        plugin.getMessageManager().send(player, "home-deleted", Map.of("home", homeName));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            return plugin.getHomeManager().getHomes(player.getUniqueId()).keySet().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
