package net.savagedev.killrewards;

import net.savagedev.killrewards.api.KillRewardsAPI;
import net.savagedev.killrewards.listeners.PlayerListeners;
import net.savagedev.killrewards.user.UserManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class KillRewards extends JavaPlugin {
    private TimeUnit timeUnit = TimeUnit.MINUTES;
    private long period;

    private UserManager userManager;

    @Override
    public void onEnable() {
        this.initConfig();
        this.initListeners();
        this.initApi();
    }

    @Override
    public void onDisable() {
        if (this.userManager != null) {
            this.userManager.shutdown();
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
    }

    private void initListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerListeners(this), this);
    }

    private void initApi() {
        if (this.getConfig().getBoolean("api-enabled")) {
            new KillRewardsAPI(this.getUserManager());
        }
    }

    public UserManager getUserManager() {
        if (this.userManager == null) {
            this.userManager = new UserManager(this);
        }
        return this.userManager;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public long getPeriod() {
        return this.period;
    }
}
