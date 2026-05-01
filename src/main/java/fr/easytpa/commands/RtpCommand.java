// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.commands;

import fr.easytpa.EasyTPA;
import fr.easytpa.utils.LocationUtils;
import fr.easytpa.utils.PermissionUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RtpCommand implements CommandExecutor {

    private final EasyTPA plugin;

    public RtpCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        if (!PermissionUtils.hasPermission(player, "teleport.rtp", plugin)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        if (!player.hasPermission("teleport.cooldown.bypass")) {
            int cooldown = plugin.getConfigManager().getTeleportCooldown();
            int remaining = plugin.getCooldownManager().getRemaining(player.getUniqueId(), cooldown);
            if (remaining > 0) {
                plugin.getMessageManager().send(player, "teleport-cooldown",
                        Map.of("remaining", String.valueOf(remaining)));
                return true;
            }
        }

        plugin.getMessageManager().send(player, "rtp-searching");
        tryRtp(player, 0);
        return true;
    }

    private void tryRtp(Player player, int attempt) {
        if (!player.isOnline()) return;

        if (attempt >= plugin.getConfigManager().getRtpMaxAttempts()) {
            plugin.getMessageManager().send(player, "rtp-failed");
            return;
        }

        World world = player.getWorld();
        int maxRadius = plugin.getConfigManager().getRtpRadius();
        int minRadius = plugin.getConfigManager().getRtpMinRadius();

        ThreadLocalRandom rng = ThreadLocalRandom.current();
        double angle = rng.nextDouble() * 2 * Math.PI;
        double distance = minRadius + rng.nextDouble() * (maxRadius - minRadius);
        int x = (int) (Math.cos(angle) * distance);
        int z = (int) (Math.sin(angle) * distance);

        world.getChunkAtAsync(x >> 4, z >> 4).thenAccept(chunk -> {
            if (!player.isOnline()) return;

            int y = world.getHighestBlockYAt(x, z);
            if (y <= world.getMinHeight()) {
                tryRtp(player, attempt + 1);
                return;
            }
            Location candidate = new Location(world, x + 0.5, y + 1, z + 0.5);

            if (LocationUtils.isSafe(candidate)) {
                plugin.getDelayManager().scheduleTeleport(player, candidate, success -> {
                    if (success) plugin.getMessageManager().send(player, "rtp-success");
                });
            } else {
                tryRtp(player, attempt + 1);
            }
        });
    }
}
