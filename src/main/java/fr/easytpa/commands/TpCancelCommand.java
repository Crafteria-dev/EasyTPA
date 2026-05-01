// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.commands;

import fr.easytpa.EasyTPA;
import fr.easytpa.utils.PermissionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class TpCancelCommand implements CommandExecutor {

    private final EasyTPA plugin;

    public TpCancelCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        if (!PermissionUtils.hasPermission(player, "teleport.tpcancel", plugin)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        if (!plugin.getTpaManager().hasRequestAsInitiator(player.getUniqueId())) {
            plugin.getMessageManager().send(player, "tpcancel-no-request");
            return true;
        }

        UUID targetId = plugin.getTpaManager().getTargetOfInitiator(player.getUniqueId());
        String targetName = resolvePlayerName(targetId);

        plugin.getTpaManager().cancelByInitiator(player.getUniqueId(), true);
        plugin.getMessageManager().send(player, "tpcancel-success", Map.of("player", targetName));

        return true;
    }

    private String resolvePlayerName(UUID id) {
        if (id == null) return "Joueur inconnu";
        Player online = plugin.getServer().getPlayer(id);
        if (online != null) return online.getName();
        // Récupérer le nom depuis les données hors-ligne (joueur déjà connecté au serveur)
        String offlineName = plugin.getServer().getOfflinePlayer(id).getName();
        return offlineName != null ? offlineName : "Joueur inconnu";
    }
}
