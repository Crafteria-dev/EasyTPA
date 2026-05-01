// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.utils;

import fr.easytpa.EasyTPA;
import org.bukkit.Bukkit;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {

    private static final String API_URL = "https://api.github.com/repos/Crafteria-dev/EasyTPA/releases/latest";
    private static final Pattern TAG_PATTERN = Pattern.compile("\"tag_name\"\\s*:\\s*\"v?([^\"]+)\"");

    private final EasyTPA plugin;

    public UpdateChecker(EasyTPA plugin) {
        this.plugin = plugin;
    }

    public void checkAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(5))
                        .build();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("Accept", "application/vnd.github.v3+json")
                        .timeout(Duration.ofSeconds(5))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) return;

                Matcher matcher = TAG_PATTERN.matcher(response.body());
                if (!matcher.find()) return;

                String latest = matcher.group(1);
                String current = plugin.getPluginMeta().getVersion();

                if (!latest.equals(current)) {
                    plugin.getLogger().warning("================================================");
                    plugin.getLogger().warning("  Mise a jour disponible pour EasyTPA !");
                    plugin.getLogger().warning("  Version actuelle : " + current);
                    plugin.getLogger().warning("  Nouvelle version : " + latest);
                    plugin.getLogger().warning("  Telecharger : github.com/Crafteria-dev/EasyTPA/releases/latest");
                    plugin.getLogger().warning("================================================");
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.FINE, "Impossible de verifier les mises a jour : " + e.getMessage());
            }
        });
    }
}
