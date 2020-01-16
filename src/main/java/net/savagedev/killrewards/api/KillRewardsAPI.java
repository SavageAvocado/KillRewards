package net.savagedev.killrewards.api;

import net.savagedev.killrewards.user.User;
import net.savagedev.killrewards.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("unused")
public class KillRewardsAPI {
    private static UserManager userManager = null;

    public KillRewardsAPI(UserManager userManager) {
        KillRewardsAPI.userManager = userManager;
    }

    public static User getUser(UUID uuid) {
        return KillRewardsAPI.getUser(Bukkit.getPlayer(uuid));
    }

    public static User getUser(Player player) {
        if (KillRewardsAPI.userManager == null) {
            throw new IllegalStateException("The KillRewards API is not enabled!");
        }
        return KillRewardsAPI.userManager.get(player);
    }
}
