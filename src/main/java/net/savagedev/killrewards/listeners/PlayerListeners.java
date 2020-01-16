package net.savagedev.killrewards.listeners;

import net.savagedev.killrewards.KillRewards;
import net.savagedev.killrewards.api.events.KillRewardEvent;
import net.savagedev.killrewards.user.User;
import net.savagedev.killrewards.user.UserManager;
import net.savagedev.killrewards.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;
import java.util.Optional;

public class PlayerListeners implements Listener {
    private final KillRewards plugin;

    public PlayerListeners(final KillRewards plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(final PlayerJoinEvent e) {
        this.plugin.getUserManager().load(e.getPlayer());
    }

    @EventHandler
    public void on(final PlayerQuitEvent e) {
        this.plugin.getUserManager().get(e.getPlayer()).saveAsync();
    }

    @EventHandler
    public void on(final PlayerDeathEvent e) {
        final Player killer = e.getEntity().getKiller();
        final Player victim = e.getEntity();

        if (killer == null) {
            return;
        }

        final UserManager userManager = this.plugin.getUserManager();
        final User user = userManager.get(killer);

        final Optional<Long> killTime = user.getKillTime(victim.getUniqueId());
        if (killTime.isPresent()) {
            if ((System.currentTimeMillis() - killTime.get()) < this.plugin.getTimeUnit().toMillis(this.plugin.getPeriod())) {
                return;
            }
            user.removeKillTime(victim.getUniqueId());
        }

        if (this.plugin.isApiEnabled()) {
            KillRewardEvent event = new KillRewardEvent(user, userManager.get(victim));
            this.plugin.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
        }

        MessageUtils.message(killer, Objects.requireNonNull(this.plugin.getConfig().getString("message")).replace("%player%", killer.getName()).replace("%victim%", victim.getName()));
        for (String reward : this.plugin.getConfig().getStringList("rewards")) {
            this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), reward.replace("%player%", killer.getName()));
        }
        user.addKillTime(victim.getUniqueId(), System.currentTimeMillis());
    }
}
