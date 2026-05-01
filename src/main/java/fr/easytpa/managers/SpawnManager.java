// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.managers;

import fr.easytpa.EasyTPA;
import fr.easytpa.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SpawnManager {

    private final EasyTPA plugin;
    private File spawnFile;
    private FileConfiguration spawnConfig;
    private Location spawnLocation;

    public SpawnManager(EasyTPA plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        spawnFile = new File(plugin.getDataFolder(), "spawn.yml");
        if (!spawnFile.exists()) {
            try { spawnFile.createNewFile(); } catch (IOException e) {
                plugin.getLogger().severe("Impossible de créer spawn.yml: " + e.getMessage());
            }
        }
        spawnConfig = YamlConfiguration.loadConfiguration(spawnFile);
        spawnLocation = null;

        if (spawnConfig.contains("spawn")) {
            spawnLocation = LocationUtils.fromSection(spawnConfig.getConfigurationSection("spawn"));
        }
    }

    public void save() {
        spawnConfig.set("spawn", null);
        if (spawnLocation != null) {
            LocationUtils.toSection(spawnConfig, "spawn", spawnLocation);
        }
        try {
            spawnConfig.save(spawnFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder spawn.yml: " + e.getMessage());
        }
    }

    public boolean hasSpawn() {
        return spawnLocation != null;
    }

    public Location getSpawn() {
        return spawnLocation;
    }

    public void setSpawn(Location location) {
        if (location == null || location.getWorld() == null) return;
        this.spawnLocation = location;
        save();
    }
}
