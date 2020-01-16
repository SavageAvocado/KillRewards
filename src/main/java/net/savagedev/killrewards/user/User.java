package net.savagedev.killrewards.user;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public interface User {
    void addKillTime(UUID uuid, long time);

    void removeKillTime(UUID uuid);

    void saveAsync();

    void saveSync();

    Optional<Long> getKillTime(UUID uuid);

    Player getPlayer();
}
