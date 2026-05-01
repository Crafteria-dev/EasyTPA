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

public class HomeCommand implements CommandExecutor, TabCompleter {

    private final EasyTPA plugin;

    public HomeCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        if (!PermissionUtils.hasPermission(player, "teleport.home", plugin)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        String homeName = args.length > 0 ? args[0] : "default";

        Map<String, Location> homes = plugin.getHomeManager().getHomes(player.getUniqueId());

        if (homes.isEmpty()) {
            plugin.getMessageManager().send(player, "home-list-empty");
            return true;
        }

        Location dest = plugin.getHomeManager().getHome(player.getUniqueId(), homeName);
        if (dest == null && homeName.equals("default") && homes.size() == 1) {
            dest = homes.values().iterator().next();
            homeName = homes.keySet().iterator().next();
        }

        if (dest == null) {
            plugin.getMessageManager().send(player, "home-not-found", Map.of("home", homeName));
            return true;
        }

        final String finalName = homeName;
        final Location finalDest = dest;
        plugin.getDelayManager().scheduleTeleport(player, finalDest, success -> {
            if (success) plugin.getMessageManager().send(player, "home-success", Map.of("home", finalName));
        });

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
