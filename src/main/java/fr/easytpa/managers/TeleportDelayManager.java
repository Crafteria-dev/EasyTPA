// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.managers;

import fr.easytpa.EasyTPA;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class TeleportDelayManager {

    private final EasyTPA plugin;

    /** Position bloc du joueur au démarrage du délai (pour détecter le déplacement). */
    private final Map<UUID, int[]> startBlockPos = new HashMap<>();

    /** Tâches de téléportation planifiées. */
    private final Map<UUID, BukkitTask> pendingTasks = new HashMap<>();

    public TeleportDelayManager(EasyTPA plugin) {
        this.plugin = plugin;
    }

    /**
     * Planifie la téléportation avec vérification du cooldown et du délai.
     * Le callback reçoit true si la téléportation a réussi.
     */
    public void scheduleTeleport(Player player, Location destination, Consumer<Boolean> callback) {
        if (destination == null || destination.getWorld() == null) {
            if (callback != null) callback.accept(false);
            return;
        }
        UUID id = player.getUniqueId();

        if (!player.hasPermission("teleport.cooldown.bypass")) {
            int cooldown = plugin.getConfigManager().getTeleportCooldown();
            int remaining = plugin.getCooldownManager().getRemaining(id, cooldown);
            if (remaining > 0) {
                plugin.getMessageManager().send(player, "teleport-cooldown",
                        Map.of("remaining", String.valueOf(remaining)));
                if (callback != null) callback.accept(false);
                return;
            }
        }

        cancel(id, false);

        int delay = plugin.getConfigManager().getTeleportDelay();

        if (delay <= 0) {
            executeTeleport(player, destination, callback);
            return;
        }

        plugin.getMessageManager().send(player, "teleport-pending",
                Map.of("delay", String.valueOf(delay)));

        Location loc = player.getLocation();
        startBlockPos.put(id, new int[]{loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()});

        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            startBlockPos.remove(id);
            pendingTasks.remove(id);
            if (player.isOnline()) executeTeleport(player, destination, callback);
        }, delay * 20L);

        pendingTasks.put(id, task);
    }

    private void executeTeleport(Player player, Location destination, Consumer<Boolean> callback) {
        // Sauvegarde la position avant téléportation (seulement quand le tp se produit réellement)
        plugin.getBackManager().setLocation(player.getUniqueId(), player.getLocation());
        player.teleportAsync(destination).thenAccept(success -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (success) plugin.getCooldownManager().set(player.getUniqueId());
                if (callback != null) callback.accept(success);
            });
        });
    }

    /** Appelé sur chaque PlayerMoveEvent. Annule si le joueur a changé de bloc. */
    public void onPlayerMove(Player player) {
        if (pendingTasks.isEmpty()) return;

        UUID id = player.getUniqueId();
        int[] start = startBlockPos.get(id);
        if (start == null) return;

        Location current = player.getLocation();
        if (start[0] != current.getBlockX()
                || start[1] != current.getBlockY()
                || start[2] != current.getBlockZ()) {
            cancel(id, true);
        }
    }

    public void cancel(UUID playerId, boolean notify) {
        BukkitTask task = pendingTasks.remove(playerId);
        startBlockPos.remove(playerId);
        if (task != null) {
            task.cancel();
            if (notify) {
                Player player = plugin.getServer().getPlayer(playerId);
                if (player != null) plugin.getMessageManager().send(player, "teleport-cancelled-moved");
            }
        }
    }

    public boolean hasPending(UUID playerId) {
        return pendingTasks.containsKey(playerId);
    }

    public void cancelAll() {
        pendingTasks.values().forEach(BukkitTask::cancel);
        pendingTasks.clear();
        startBlockPos.clear();
    }
}
