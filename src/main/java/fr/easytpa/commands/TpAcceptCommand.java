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

public class TpAcceptCommand implements CommandExecutor {

    private final EasyTPA plugin;

    public TpAcceptCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        if (!PermissionUtils.hasPermission(player, "teleport.tpaccept", plugin)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        TpaManager.TpaRequest request = plugin.getTpaManager().getRequestForTarget(player.getUniqueId());
        if (request == null) {
            plugin.getMessageManager().send(player, "tpaccept-no-request");
            return true;
        }

        Player initiator = plugin.getServer().getPlayer(request.getInitiator());
        if (initiator == null || !initiator.isOnline()) {
            plugin.getTpaManager().cancelByTarget(player.getUniqueId());
            plugin.getMessageManager().send(player, "tpaccept-initiator-gone");
            return true;
        }

        // Vérifie le cooldown avant de consommer la demande
        Player teleportee = (request.getType() == TpaManager.TpaType.TPA) ? initiator : player;
        if (!teleportee.hasPermission("teleport.cooldown.bypass")) {
            int cooldown = plugin.getConfigManager().getTeleportCooldown();
            int remaining = plugin.getCooldownManager().getRemaining(teleportee.getUniqueId(), cooldown);
            if (remaining > 0) {
                if (teleportee == player) {
                    // L'accepteur lui-même est en cooldown
                    plugin.getMessageManager().send(player, "teleport-cooldown",
                            Map.of("remaining", String.valueOf(remaining)));
                } else {
                    // L'initiateur est en cooldown : on notifie l'accepteur uniquement (pas de spam sur l'initiateur)
                    plugin.getMessageManager().send(player, "tpaccept-partner-cooldown",
                            Map.of("player", teleportee.getName(), "remaining", String.valueOf(remaining)));
                }
                return true;
            }
        }

        // Retire la demande avant de téléporter
        plugin.getTpaManager().cancelByTarget(player.getUniqueId());

        plugin.getMessageManager().send(player, "tpaccept-success-target");
        plugin.getMessageManager().send(initiator, "tpaccept-success-initiator", Map.of("player", player.getName()));

        if (request.getType() == TpaManager.TpaType.TPA) {
            // L'initiateur vient chez la cible
            plugin.getDelayManager().scheduleTeleport(initiator, player.getLocation(), success -> {
                if (success) plugin.getMessageManager().send(initiator, "teleport-success");
            });
        } else {
            // TPA HERE: la cible va chez l'initiateur
            plugin.getDelayManager().scheduleTeleport(player, initiator.getLocation(), success -> {
                if (success) plugin.getMessageManager().send(player, "teleport-success");
            });
        }

        return true;
    }
}
