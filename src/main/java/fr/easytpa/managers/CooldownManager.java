// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Long> lastTeleport = new HashMap<>();

    public void set(UUID playerId) {
        lastTeleport.put(playerId, System.currentTimeMillis());
    }

    /** Returns remaining cooldown in seconds, or 0 if none. */
    public int getRemaining(UUID playerId, int cooldownSeconds) {
        if (cooldownSeconds <= 0) return 0;
        Long last = lastTeleport.get(playerId);
        if (last == null) return 0;
        long elapsed = (System.currentTimeMillis() - last) / 1000L;
        int remaining = (int) (cooldownSeconds - elapsed);
        return Math.max(0, remaining);
    }

    public void clear(UUID playerId) {
        lastTeleport.remove(playerId);
    }
}
