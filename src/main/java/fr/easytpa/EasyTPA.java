// EasyTPA © 2026 ZO3N | https://github.com/Crafteria-dev/EasyTPA | Toute utilisation commerciale sans autorisation est interdite.
package fr.easytpa;

import fr.easytpa.commands.*;
import fr.easytpa.listeners.TeleportListener;
import fr.easytpa.managers.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class EasyTPA extends JavaPlugin {

    private static EasyTPA instance;

    private ConfigManager configManager;
    private MessageManager messageManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private SpawnManager spawnManager;
    private BackManager backManager;
    private TpaManager tpaManager;
    private TeleportDelayManager delayManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("messages.yml", false);

        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        homeManager = new HomeManager(this);
        warpManager = new WarpManager(this);
        spawnManager = new SpawnManager(this);
        backManager = new BackManager();
        tpaManager = new TpaManager(this);
        cooldownManager = new CooldownManager();
        delayManager = new TeleportDelayManager(this);

        registerCommands();
        getServer().getPluginManager().registerEvents(new TeleportListener(this), this);

        getLogger().info("================================================");
        getLogger().info("  EasyTPA v" + getDescription().getVersion() + " - by ZO3N");
        getLogger().info("  https://github.com/Crafteria-dev/EasyTPA");
        getLogger().info("================================================");
        getLogger().info("EasyTPA activé !");
    }

    @Override
    public void onDisable() {
        if (homeManager != null) homeManager.save();
        if (warpManager != null) warpManager.save();
        if (spawnManager != null) spawnManager.save();
        if (tpaManager != null) tpaManager.cancelAll();
        if (delayManager != null) delayManager.cancelAll();
        getLogger().info("EasyTPA désactivé.");
    }

    public void reload() {
        reloadConfig();
        configManager.reload();
        messageManager.reload();
        homeManager.reload();
        warpManager.reload();
        spawnManager.reload();
    }

    private void registerCommands() {
        TpaCommand tpaCmd = new TpaCommand(this);
        TpaHereCommand tpaHereCmd = new TpaHereCommand(this);
        TpAcceptCommand acceptCmd = new TpAcceptCommand(this);
        TpDenyCommand denyCmd = new TpDenyCommand(this);
        TpCancelCommand cancelCmd = new TpCancelCommand(this);
        TpToggleCommand toggleCmd = new TpToggleCommand(this);

        HomeCommand homeCmd = new HomeCommand(this);
        SetHomeCommand setHomeCmd = new SetHomeCommand(this);
        DelHomeCommand delHomeCmd = new DelHomeCommand(this);

        WarpCommand warpCmd = new WarpCommand(this);
        SetWarpCommand setWarpCmd = new SetWarpCommand(this);
        DelWarpCommand delWarpCmd = new DelWarpCommand(this);
        WarpsCommand warpsCmd = new WarpsCommand(this);

        SpawnCommand spawnCmd = new SpawnCommand(this);
        SetSpawnCommand setSpawnCmd = new SetSpawnCommand(this);

        BackCommand backCmd = new BackCommand(this);
        RtpCommand rtpCmd = new RtpCommand(this);
        TpReloadCommand reloadCmd = new TpReloadCommand(this);

        setExec("tpa", tpaCmd);
        setExec("tpahere", tpaHereCmd);
        setExec("tpaccept", acceptCmd);
        setExec("tpdeny", denyCmd);
        setExec("tpcancel", cancelCmd);
        setExec("tptoggle", toggleCmd);
        setExec("home", homeCmd);
        setExec("sethome", setHomeCmd);
        setExec("delhome", delHomeCmd);
        setExec("warp", warpCmd);
        setExec("setwarp", setWarpCmd);
        setExec("delwarp", delWarpCmd);
        setExec("warps", warpsCmd);
        setExec("spawn", spawnCmd);
        setExec("setspawn", setSpawnCmd);
        setExec("back", backCmd);
        setExec("rtp", rtpCmd);
        setExec("tpreload", reloadCmd);

        setTabCompleter("tpa", tpaCmd);
        setTabCompleter("tpahere", tpaHereCmd);
        setTabCompleter("home", homeCmd);
        setTabCompleter("sethome", setHomeCmd);
        setTabCompleter("delhome", delHomeCmd);
        setTabCompleter("warp", warpCmd);
        setTabCompleter("setwarp", setWarpCmd);
        setTabCompleter("delwarp", delWarpCmd);
    }

    private void setExec(String name, org.bukkit.command.CommandExecutor executor) {
        PluginCommand cmd = getCommand(name);
        if (cmd != null) cmd.setExecutor(executor);
    }

    private void setTabCompleter(String name, org.bukkit.command.TabCompleter completer) {
        PluginCommand cmd = getCommand(name);
        if (cmd != null) cmd.setTabCompleter(completer);
    }

    public static EasyTPA getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public MessageManager getMessageManager() { return messageManager; }
    public HomeManager getHomeManager() { return homeManager; }
    public WarpManager getWarpManager() { return warpManager; }
    public SpawnManager getSpawnManager() { return spawnManager; }
    public BackManager getBackManager() { return backManager; }
    public TpaManager getTpaManager() { return tpaManager; }
    public TeleportDelayManager getDelayManager() { return delayManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
}
