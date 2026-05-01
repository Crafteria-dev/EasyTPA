// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.managers;

import fr.easytpa.EasyTPA;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TpaManager {

    public enum TpaType { TPA, TPAHERE }

    public static class TpaRequest {
        private final UUID initiator;
        private final UUID target;
        private final TpaType type;
        private BukkitTask timeoutTask;

        public TpaRequest(UUID initiator, UUID target, TpaType type) {
            this.initiator = initiator;
            this.target = target;
            this.type = type;
        }

        public UUID getInitiator() { return initiator; }
        public UUID getTarget() { return target; }
        public TpaType getType() { return type; }
        public void setTimeoutTask(BukkitTask task) { this.timeoutTask = task; }
        public void cancelTimeout() { if (timeoutTask != null) timeoutTask.cancel(); }
    }

    private final EasyTPA plugin;

    /** Demande en attente indexée par la cible (celui qui doit accepter). */
    private final Map<UUID, TpaRequest> pendingByTarget = new HashMap<>();

    /** Index inverse : initiateur → cible, pour /tpcancel. */
    private final Map<UUID, UUID> pendingByInitiator = new HashMap<>();

    /** Joueurs ayant désactivé la réception de demandes. */
    private final Set<UUID> toggled = new HashSet<>();

    public TpaManager(EasyTPA plugin) {
        this.plugin = plugin;
    }

    public boolean sendRequest(Player initiator, Player target, TpaType type) {
        UUID iId = initiator.getUniqueId();
        UUID tId = target.getUniqueId();

        // Annule l'ancienne demande de cet initiateur si elle existe
        cancelByInitiator(iId, false);

        // Si un autre joueur a déjà une demande vers cette cible, nettoie son état
        // pour éviter des entrées orphelines dans pendingByInitiator
        TpaRequest existingForTarget = pendingByTarget.get(tId);
        if (existingForTarget != null) {
            existingForTarget.cancelTimeout();
            pendingByInitiator.remove(existingForTarget.getInitiator());
        }

        TpaRequest request = new TpaRequest(iId, tId, type);
        pendingByTarget.put(tId, request);
        pendingByInitiator.put(iId, tId);

        // Capturer les noms maintenant — les objets Player peuvent devenir invalides plus tard
        String initiatorName = initiator.getName();
        String targetName = target.getName();

        int timeout = plugin.getConfigManager().getTpaTimeout();
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            TpaRequest pending = pendingByTarget.get(tId);
            if (pending != null && pending.getInitiator().equals(iId)) {
                pendingByTarget.remove(tId);
                pendingByInitiator.remove(iId);

                Player init = plugin.getServer().getPlayer(iId);
                Player tgt = plugin.getServer().getPlayer(tId);

                if (init != null) plugin.getMessageManager().send(init, "tpa-timeout",
                        Map.of("player", targetName));
                if (tgt != null) plugin.getMessageManager().send(tgt, "tpa-timeout-notify",
                        Map.of("player", initiatorName));
            }
        }, timeout * 20L);

        request.setTimeoutTask(task);
        return true;
    }

    public TpaRequest getRequestForTarget(UUID targetId) {
        return pendingByTarget.get(targetId);
    }

    public UUID getTargetOfInitiator(UUID initiatorId) {
        return pendingByInitiator.get(initiatorId);
    }

    public void cancelByTarget(UUID targetId) {
        TpaRequest req = pendingByTarget.remove(targetId);
        if (req != null) {
            req.cancelTimeout();
            pendingByInitiator.remove(req.getInitiator());
        }
    }

    /** Annule la demande envoyée par initiatorId. Si notifyTarget, avertit la cible. */
    public TpaRequest cancelByInitiator(UUID initiatorId, boolean notifyTarget) {
        UUID targetId = pendingByInitiator.remove(initiatorId);
        if (targetId == null) return null;

        TpaRequest req = pendingByTarget.remove(targetId);
        if (req != null) {
            req.cancelTimeout();
            if (notifyTarget) {
                Player target = plugin.getServer().getPlayer(targetId);
                Player initiator = plugin.getServer().getPlayer(initiatorId);
                String name = initiator != null ? initiator.getName() : "Quelqu'un";
                if (target != null) plugin.getMessageManager().send(target, "tpcancel-notify",
                        Map.of("player", name));
            }
        }
        return req;
    }

    public boolean hasRequestAsTarget(UUID targetId) {
        return pendingByTarget.containsKey(targetId);
    }

    public boolean hasRequestAsInitiator(UUID initiatorId) {
        return pendingByInitiator.containsKey(initiatorId);
    }

    public boolean isToggled(UUID playerId) {
        return toggled.contains(playerId);
    }

    /** Retourne true si le toggle passe à "désactivé". */
    public boolean toggle(UUID playerId) {
        if (toggled.remove(playerId)) return false;
        toggled.add(playerId);
        return true;
    }

    public void cleanupPlayer(UUID playerId) {
        cancelByInitiator(playerId, true);
        cancelByTarget(playerId);
    }

    public void cancelAll() {
        for (TpaRequest req : pendingByTarget.values()) req.cancelTimeout();
        pendingByTarget.clear();
        pendingByInitiator.clear();
    }
}
