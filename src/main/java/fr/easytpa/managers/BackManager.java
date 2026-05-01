// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.managers;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackManager {

    private final Map<UUID, Location> backLocations = new HashMap<>();

    public void setLocation(UUID playerId, Location location) {
        if (location != null) backLocations.put(playerId, location.clone());
    }

    public Location getLocation(UUID playerId) {
        return backLocations.get(playerId);
    }

    public boolean hasLocation(UUID playerId) {
        return backLocations.containsKey(playerId);
    }

    public void removeLocation(UUID playerId) {
        backLocations.remove(playerId);
    }
}
