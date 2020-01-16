package net.savagedev.killrewards.user;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KillRewardsUser extends YamlConfiguration implements User {
    private final Lock ioLock = new ReentrantLock();
    private final Path path;

    private final JavaPlugin plugin;
    private final Player player;

    KillRewardsUser(final Player player, final Path path, final JavaPlugin plugin) {
        this.plugin = plugin;
        this.player = player;
        this.path = path;

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.ioLock.lock();
            try {
                this.load(Files.newBufferedReader(path, StandardCharsets.UTF_8));
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            } finally {
                this.ioLock.unlock();
            }
        });
    }

    @Override
    public void addKillTime(UUID uuid, long time) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.ioLock.lock();
            try {
                super.set(uuid.toString(), time);
            } finally {
                this.ioLock.unlock();
                this.save();
            }
        });
    }

    @Override
    public void removeKillTime(UUID uuid) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.ioLock.lock();
            try {
                super.set(uuid.toString(), null);
            } finally {
                this.ioLock.unlock();
                this.save();
            }
        });
    }

    @Override
    public void saveAsync() {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, this::saveSync);
    }

    @Override
    public void saveSync() {
        this.ioLock.lock();
        try {
            this.save();
        } finally {
            this.ioLock.unlock();
        }
    }

    private void save() {
        try {
            super.save(this.path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Long> getKillTime(UUID uuid) {
        if (this.contains(uuid.toString())) {
            return Optional.of(this.getLong(uuid.toString()));
        }
        return Optional.empty();
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }
}
