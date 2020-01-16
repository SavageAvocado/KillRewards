package net.savagedev.killrewards.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class UserManager implements Iterable<User> {
    private final Cache<UUID, User> userCache = CacheBuilder.newBuilder().expireAfterAccess(5L, TimeUnit.MINUTES).maximumSize(100).build();
    private final JavaPlugin plugin;

    public UserManager(final JavaPlugin plugin) {
        this.plugin = plugin;

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            this.load(player);
        }
    }

    public User load(Player player) {
        final Path path = this.plugin.getDataFolder().toPath().resolve(Paths.get("storage", player.getUniqueId().toString() + ".yml"));

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final User user = new KillRewardsUser(player, path, this.plugin);
        this.userCache.put(player.getUniqueId(), user);
        return user;
    }

    public User get(Player player) {
        try {
            return this.userCache.get(player.getUniqueId(), () -> this.load(player));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return this.load(player);
    }

    public void shutdown() {
        for (User user : this) {
            user.saveSync();
        }
        this.userCache.invalidateAll();
    }

    @Override @Nonnull
    public Iterator<User> iterator() {
        return this.userCache.asMap().values().iterator();
    }
}
