// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class LocationUtils {

    private LocationUtils() {}

    public static Location fromSection(ConfigurationSection section) {
        if (section == null) return null;
        String worldName = section.getString("world");
        if (worldName == null) return null;
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw", 0);
        float pitch = (float) section.getDouble("pitch", 0);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void toSection(FileConfiguration config, String path, Location location) {
        if (location == null || location.getWorld() == null) return;
        config.set(path + ".world", location.getWorld().getName());
        config.set(path + ".x", location.getX());
        config.set(path + ".y", location.getY());
        config.set(path + ".z", location.getZ());
        config.set(path + ".yaw", (double) location.getYaw());
        config.set(path + ".pitch", (double) location.getPitch());
    }

    /** Vérifie si une location est sûre : air aux pieds et à la tête, sol solide, pas de danger. */
    public static boolean isSafe(Location location) {
        if (location == null || location.getWorld() == null) return false;
        Location feet   = location.clone();
        Location head   = feet.clone().add(0, 1, 0);
        Location ground = feet.clone().subtract(0, 1, 0);

        // Le joueur doit tenir dans l'espace (2 blocs d'air)
        if (!feet.getBlock().getType().isAir()) return false;
        if (!head.getBlock().getType().isAir()) return false;

        // Le sol doit être solide
        Material groundMat = ground.getBlock().getType();
        if (!groundMat.isSolid()) return false;

        // Blocs dangereux sous les pieds
        if (groundMat == Material.MAGMA_BLOCK) return false;
        if (groundMat == Material.CAMPFIRE || groundMat == Material.SOUL_CAMPFIRE) return false;

        return true;
    }
}
