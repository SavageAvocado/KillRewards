package net.savagedev.killrewards;

import net.savagedev.killrewards.api.KillRewardsAPI;
import net.savagedev.killrewards.commands.KillRewardsCmd;
import net.savagedev.killrewards.listeners.PlayerListeners;
import net.savagedev.killrewards.user.UserManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class KillRewards extends JavaPlugin {
    private TimeUnit timeUnit = TimeUnit.MINUTES;
    private boolean apiEnabled;
    private long period;

    private UserManager userManager;

    @Override
    public void onEnable() {
        this.initConfig();
        this.initListeners();
        this.initCommands();
        this.initApi();
    }

    @Override
    public void onDisable() {
        if (this.userManager != null) {
            this.userManager.shutdown();
        }
    }

    public void reload() {
        this.reloadConfig();
        this.initConfig();

        if (this.userManager != null) {
            this.userManager.reload();
        }
    }

    private void initConfig() {
        this.saveDefaultConfig();

        try {
            this.timeUnit = TimeUnit.valueOf(Objects.requireNonNull(this.getConfig().getString("reward-period.time-unit")).toUpperCase());
        } catch (IllegalArgumentException ignored) {
            this.getLogger().log(Level.WARNING, "Invalid \'time-unit\' value in config! Defaulting to MINUTES.");
        }

        this.period = this.getConfig().getLong("reward-period.time", -1L);
        this.apiEnabled = this.getConfig().getBoolean("api-enabled");
    }

    private void initListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);
    }

    private void initCommands() {
        final PluginCommand command = this.getCommand("killrewards");
        if (command != null) {
            command.setExecutor(new KillRewardsCmd(this));
        }
    }

    private void initApi() {
        if (this.isApiEnabled()) {
            new KillRewardsAPI(this.getUserManager());
        }
    }

    public UserManager getUserManager() {
        if (this.userManager == null) {
            this.userManager = new UserManager(this);
        }
        return this.userManager;
    }

    public boolean isApiEnabled() {
        return this.apiEnabled;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public long getPeriod() {
        return this.period;
    }
}
