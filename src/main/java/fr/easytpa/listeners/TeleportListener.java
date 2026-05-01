// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.listeners;

import fr.easytpa.EasyTPA;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TeleportListener implements Listener {

    private final EasyTPA plugin;

    public TeleportListener(EasyTPA plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        plugin.getDelayManager().onPlayerMove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.getConfigManager().isBackOnDeath()) return;
        plugin.getBackManager().setLocation(
                event.getEntity().getUniqueId(),
                event.getEntity().getLocation()
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        java.util.UUID id = event.getPlayer().getUniqueId();
        plugin.getTpaManager().cleanupPlayer(id);
        plugin.getDelayManager().cancel(id, false);
        plugin.getBackManager().removeLocation(id);
        plugin.getCooldownManager().clear(id);
    }
}
