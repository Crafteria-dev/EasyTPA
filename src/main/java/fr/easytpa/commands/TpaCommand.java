// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.commands;

import fr.easytpa.EasyTPA;
import fr.easytpa.managers.TpaManager;
import fr.easytpa.utils.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class TpaCommand implements CommandExecutor, TabCompleter {

    private final EasyTPA plugin;

    public TpaCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        if (!PermissionUtils.hasPermission(player, "teleport.tpa", plugin)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessageManager().send(player, "invalid-usage", Map.of("usage", "/" + label + " <joueur>"));
            return true;
        }

        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessageManager().send(player, "player-not-found", Map.of("player", args[0]));
            return true;
        }

        if (target.equals(player)) {
            plugin.getMessageManager().send(player, "tpa-self");
            return true;
        }

        if (plugin.getTpaManager().isToggled(target.getUniqueId())) {
            plugin.getMessageManager().send(player, "tpa-disabled", Map.of("player", target.getName()));
            return true;
        }

        if (plugin.getTpaManager().hasRequestAsInitiator(player.getUniqueId())) {
            plugin.getMessageManager().send(player, "tpa-already-pending", Map.of("player", target.getName()));
            return true;
        }

        plugin.getTpaManager().sendRequest(player, target, TpaManager.TpaType.TPA);

        plugin.getMessageManager().send(player, "tpa-sent", Map.of("player", target.getName()));

        plugin.getMessageManager().send(target, "tpa-received", Map.of("player", player.getName()));
        target.sendMessage(plugin.getMessageManager().parse("tpa-received-buttons"));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
