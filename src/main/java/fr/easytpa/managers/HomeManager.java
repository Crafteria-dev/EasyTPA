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

public class HomeManager {

    private final EasyTPA plugin;
    private File homesFile;
    private FileConfiguration homesConfig;

    /** UUID du joueur -> (nom du home -> Location) */
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();

    public HomeManager(EasyTPA plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        homesFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!homesFile.exists()) {
            try { homesFile.createNewFile(); } catch (IOException e) {
                plugin.getLogger().severe("Impossible de créer homes.yml: " + e.getMessage());
            }
        }
        homesConfig = YamlConfiguration.loadConfiguration(homesFile);
        homes.clear();
        load();
    }

    private void load() {
        ConfigurationSection section = homesConfig.getConfigurationSection("homes");
        if (section == null) return;

        for (String uuidStr : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                ConfigurationSection playerSection = section.getConfigurationSection(uuidStr);
                if (playerSection == null) continue;

                Map<String, Location> playerHomes = new HashMap<>();
                for (String homeName : playerSection.getKeys(false)) {
                    ConfigurationSection locSection = playerSection.getConfigurationSection(homeName);
                    if (locSection == null) continue;
                    Location loc = LocationUtils.fromSection(locSection);
                    if (loc != null) playerHomes.put(homeName, loc);
                }
                if (!playerHomes.isEmpty()) homes.put(uuid, playerHomes);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save() {
        homesConfig.set("homes", null);
        for (Map.Entry<UUID, Map<String, Location>> entry : homes.entrySet()) {
            String base = "homes." + entry.getKey().toString();
            for (Map.Entry<String, Location> homeEntry : entry.getValue().entrySet()) {
                LocationUtils.toSection(homesConfig, base + "." + homeEntry.getKey(), homeEntry.getValue());
            }
        }
        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder homes.yml: " + e.getMessage());
        }
    }

    public Location getHome(UUID uuid, String name) {
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null) return null;
        return playerHomes.get(name.toLowerCase());
    }

    public Map<String, Location> getHomes(UUID uuid) {
        return homes.getOrDefault(uuid, Collections.emptyMap());
    }

    public void setHome(UUID uuid, String name, Location location) {
        if (location == null || location.getWorld() == null) return;
        homes.computeIfAbsent(uuid, k -> new HashMap<>()).put(name.toLowerCase(), location);
        save();
    }

    public boolean deleteHome(UUID uuid, String name) {
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null) return false;
        boolean removed = playerHomes.remove(name.toLowerCase()) != null;
        if (playerHomes.isEmpty()) homes.remove(uuid);
        if (removed) save();
        return removed;
    }

    public boolean hasHome(UUID uuid, String name) {
        Map<String, Location> playerHomes = homes.get(uuid);
        return playerHomes != null && playerHomes.containsKey(name.toLowerCase());
    }

    public int getHomeCount(UUID uuid) {
        Map<String, Location> playerHomes = homes.get(uuid);
        return playerHomes == null ? 0 : playerHomes.size();
    }

    public int getMaxHomes(org.bukkit.entity.Player player) {
        int configMax = plugin.getConfigManager().getMaxHomes();
        if (!plugin.getConfigManager().isPermissionsEnabled()) return configMax;

        for (int i = 100; i > configMax; i--) {
            if (player.hasPermission("teleport.home.multiple." + i)) return i;
        }
        return configMax;
    }
}
