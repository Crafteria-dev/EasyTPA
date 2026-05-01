// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.commands;

import fr.easytpa.EasyTPA;
import fr.easytpa.managers.TpaManager;
import fr.easytpa.utils.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class TpDenyCommand implements CommandExecutor {

    private final EasyTPA plugin;

    public TpDenyCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        if (!PermissionUtils.hasPermission(player, "teleport.tpdeny", plugin)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        TpaManager.TpaRequest request = plugin.getTpaManager().getRequestForTarget(player.getUniqueId());
        if (request == null) {
            plugin.getMessageManager().send(player, "tpdeny-no-request");
            return true;
        }

        Player initiator = plugin.getServer().getPlayer(request.getInitiator());
        plugin.getTpaManager().cancelByTarget(player.getUniqueId());

        plugin.getMessageManager().send(player, "tpdeny-success-target");
        if (initiator != null && initiator.isOnline()) {
            plugin.getMessageManager().send(initiator, "tpdeny-success-initiator",
                    Map.of("player", player.getName()));
        }

        return true;
    }
}
