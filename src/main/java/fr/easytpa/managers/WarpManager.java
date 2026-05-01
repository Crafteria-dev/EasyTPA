// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.managers;

import fr.easytpa.EasyTPA;
import fr.easytpa.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WarpManager {

    private final EasyTPA plugin;
    private File warpsFile;
    private FileConfiguration warpsConfig;

    private final Map<String, Location> warps = new HashMap<>();
    /** Créateur de chaque warp (null = warp existant avant la mise à jour). */
    private final Map<String, UUID> creators = new HashMap<>();

    public WarpManager(EasyTPA plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        warpsFile = new File(plugin.getDataFolder(), "warps.yml");
        if (!warpsFile.exists()) {
            try { warpsFile.createNewFile(); } catch (IOException e) {
                plugin.getLogger().severe("Impossible de créer warps.yml: " + e.getMessage());
            }
        }
        warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
        warps.clear();
        creators.clear();
        load();
    }

    private void load() {
        ConfigurationSection section = warpsConfig.getConfigurationSection("warps");
        if (section == null) return;

        for (String name : section.getKeys(false)) {
            ConfigurationSection locSection = section.getConfigurationSection(name);
            if (locSection == null) continue;
            Location loc = LocationUtils.fromSection(locSection);
            if (loc == null) continue;
            warps.put(name.toLowerCase(), loc);

            String creatorStr = locSection.getString("creator");
            if (creatorStr != null) {
                try { creators.put(name.toLowerCase(), UUID.fromString(creatorStr)); }
                catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save() {
        warpsConfig.set("warps", null);
        for (Map.Entry<String, Location> entry : warps.entrySet()) {
            String key = entry.getKey();
            LocationUtils.toSection(warpsConfig, "warps." + key, entry.getValue());
            UUID creator = creators.get(key);
            if (creator != null) {
                warpsConfig.set("warps." + key + ".creator", creator.toString());
            }
        }
        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder warps.yml: " + e.getMessage());
        }
    }

    public Location getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    /** Crée ou écrase un warp en enregistrant le joueur créateur. */
    public void setWarp(String name, Location location, UUID creator) {
        if (location == null || location.getWorld() == null) return;
        String key = name.toLowerCase();
        warps.put(key, location);
        if (creator != null) creators.put(key, creator);
        save();
    }

    public boolean deleteWarp(String name) {
        String key = name.toLowerCase();
        boolean removed = warps.remove(key) != null;
        if (removed) {
            creators.remove(key);
            save();
        }
        return removed;
    }

    public boolean hasWarp(String name) {
        return warps.containsKey(name.toLowerCase());
    }

    public UUID getCreator(String name) {
        return creators.get(name.toLowerCase());
    }

    public Set<String> getWarpNames() {
        return Collections.unmodifiableSet(warps.keySet());
    }

    /** Nombre de warps créés par ce joueur. */
    public int getWarpCountByPlayer(UUID playerId) {
        int count = 0;
        for (UUID creator : creators.values()) {
            if (playerId.equals(creator)) count++;
        }
        return count;
    }
}
