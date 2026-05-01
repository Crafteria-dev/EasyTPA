// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.utils;

import fr.easytpa.EasyTPA;
import org.bukkit.entity.Player;

public final class PermissionUtils {

    private PermissionUtils() {}

    /**
     * Vérifie une permission joueur standard.
     * Si permissions: false dans config.yml, retourne toujours true.
     */
    public static boolean hasPermission(Player player, String node, EasyTPA plugin) {
        if (!plugin.getConfigManager().isPermissionsEnabled()) return true;
        return player.hasPermission(node);
    }

    /**
     * Vérifie une permission admin (setwarp, delwarp, setspawn, tpreload).
     * Nécessite toujours teleport.admin, le node spécifique, ou le statut OP,
     * indépendamment du paramètre permissions: dans config.yml.
     */
    public static boolean hasAdminPermission(Player player, String specificNode) {
        return player.isOp()
                || player.hasPermission("teleport.admin")
                || player.hasPermission(specificNode);
    }
}
