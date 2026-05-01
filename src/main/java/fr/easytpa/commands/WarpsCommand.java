// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa.commands;

import fr.easytpa.EasyTPA;
import fr.easytpa.utils.PermissionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WarpsCommand implements CommandExecutor {

    private final EasyTPA plugin;

    public WarpsCommand(EasyTPA plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "player-only");
            return true;
        }

        if (!PermissionUtils.hasPermission(player, "teleport.warps", plugin)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        var names = plugin.getWarpManager().getWarpNames();

        if (names.isEmpty()) {
            plugin.getMessageManager().send(player, "warp-list-empty");
            return true;
        }

        Component list = Component.join(
                JoinConfiguration.separator(Component.text(", ", NamedTextColor.GRAY)),
                names.stream()
                        .sorted()
                        .map(name -> Component.text(name, NamedTextColor.GOLD)
                                .decorate(TextDecoration.UNDERLINED)
                                .hoverEvent(HoverEvent.showText(
                                        Component.text("Cliquer pour aller à " + name, NamedTextColor.YELLOW)))
                                .clickEvent(ClickEvent.runCommand("/warp " + name)))
                        .toList()
        );

        plugin.getMessageManager().send(player, "warp-list");
        player.sendMessage(list);

        return true;
    }
}
