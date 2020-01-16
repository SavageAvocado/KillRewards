package net.savagedev.killrewards.api.events;

import net.savagedev.killrewards.user.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class KillRewardEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final User killer;
    private final User victim;

    private boolean cancelled;

    public KillRewardEvent(final User killer, final User victim) {
        this.killer = killer;
        this.victim = victim;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public User getKiller() {
        return this.killer;
    }

    public User getVictim() {
        return this.victim;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override @Nonnull
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
