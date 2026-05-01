// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.managers;

import fr.easytpa.EasyTPA;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;

public class MessageManager {

    private final EasyTPA plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private FileConfiguration messages;

    // Cache du préfixe parsé pour éviter de le redéserialiser à chaque message
    private String cachedPrefixRaw;
    private Component cachedPrefix;

    public MessageManager(EasyTPA plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) plugin.saveResource("messages.yml", false);
        messages = YamlConfiguration.loadConfiguration(file);
        migrateMessages(file);
        cachedPrefixRaw = null;
        cachedPrefix = null;
    }

    /** Ajoute les clés manquantes depuis le messages.yml embarqué dans le JAR. */
    private void migrateMessages(File file) {
        try (InputStream stream = plugin.getResource("messages.yml")) {
            if (stream == null) return;
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(stream, StandardCharsets.UTF_8));
            boolean changed = false;
            for (String key : defaults.getKeys(false)) {
                if (!messages.contains(key)) {
                    messages.set(key, defaults.getString(key));
                    plugin.getLogger().info("[Messages] Nouvelle cle ajoutee : '" + key + "'");
                    changed = true;
                }
            }
            if (changed) messages.save(file);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Erreur lors de la migration de messages.yml", e);
        }
    }

    private Component getPrefix() {
        String raw = plugin.getConfigManager().getPrefix();
        if (!raw.equals(cachedPrefixRaw)) {
            cachedPrefixRaw = raw;
            cachedPrefix = mm.deserialize(raw);
        }
        return cachedPrefix;
    }

    /** Parse un message depuis messages.yml en Component avec préfixe. */
    public Component parse(String key, Map<String, String> placeholders) {
        String raw = messages.getString(key, "<red>Message manquant: " + key);

        Component body;
        if (placeholders == null || placeholders.isEmpty()) {
            body = mm.deserialize(raw);
        } else {
            TagResolver[] resolvers = placeholders.entrySet().stream()
                    .map(e -> Placeholder.unparsed(e.getKey(), e.getValue()))
                    .toArray(TagResolver[]::new);
            body = mm.deserialize(raw, resolvers);
        }

        return getPrefix().append(body);
    }

    public Component parse(String key) {
        return parse(key, null);
    }

    public void send(CommandSender sender, String key) {
        sender.sendMessage(parse(key));
    }

    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        sender.sendMessage(parse(key, placeholders));
    }

    public Component parseRaw(String miniMessageString) {
        return mm.deserialize(miniMessageString);
    }
}
