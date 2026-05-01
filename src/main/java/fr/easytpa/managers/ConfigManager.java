// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.managers;

import fr.easytpa.EasyTPA;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class ConfigManager {

    private final EasyTPA plugin;
    private FileConfiguration config;

    public ConfigManager(EasyTPA plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        if (migrateConfig()) {
            plugin.reloadConfig();
            this.config = plugin.getConfig();
        }
    }

    /** Ajoute les clés manquantes depuis le config.yml embarqué. Retourne true si des clés ont été ajoutées. */
    private boolean migrateConfig() {
        try (InputStream stream = plugin.getResource("config.yml")) {
            if (stream == null) return false;
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(stream, StandardCharsets.UTF_8));
            boolean changed = false;
            for (String key : defaults.getKeys(true)) {
                if (!defaults.isConfigurationSection(key) && !config.contains(key)) {
                    config.set(key, defaults.get(key));
                    plugin.getLogger().info("[Config] Nouvelle cle ajoutee : '" + key + "'");
                    changed = true;
                }
            }
            if (changed) plugin.saveConfig();
            return changed;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erreur lors de la migration de config.yml", e);
            return false;
        }
    }

    public boolean isPermissionsEnabled() {
        return config.getBoolean("permissions", false);
    }

    public int getTeleportDelay() {
        return Math.max(0, config.getInt("teleport-delay", 3));
    }

    public int getTpaTimeout() {
        return Math.max(5, config.getInt("tpa-timeout", 60));
    }

    public boolean isBackOnDeath() {
        return config.getBoolean("back-on-death", true);
    }

    public int getMaxHomes() {
        return Math.max(1, config.getInt("max-homes", 1));
    }

    public int getMaxWarps() {
        return Math.max(1, config.getInt("max-warps", 30));
    }

    public int getRtpRadius() {
        return Math.max(100, config.getInt("rtp-radius", 5000));
    }

    public int getRtpMinRadius() {
        return Math.max(0, config.getInt("rtp-min-radius", 100));
    }

    public int getRtpMaxAttempts() {
        return Math.max(1, config.getInt("rtp-max-attempts", 20));
    }

    public int getTeleportCooldown() {
        return Math.max(0, config.getInt("teleport-cooldown", 0));
    }

    public String getPrefix() {
        return config.getString("messages-prefix", "<dark_gray>[<aqua>Teleport<dark_gray>]<reset> ");
    }
}
