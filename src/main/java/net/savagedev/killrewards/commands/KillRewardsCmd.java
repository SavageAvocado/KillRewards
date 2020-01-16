package net.savagedev.killrewards.commands;

import net.savagedev.killrewards.KillRewards;
import net.savagedev.killrewards.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public class KillRewardsCmd implements CommandExecutor {
    private final KillRewards plugin;

    public KillRewardsCmd(final KillRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            MessageUtils.message(sender, "&cInvalid arguments! Try: \"/" + label + " reload\"");
            return true;
        }

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.plugin.reload();
            MessageUtils.message(sender, "&6" + this.plugin.getDescription().getName() + " &7v&6" + this.plugin.getDescription().getVersion() + " &7by &6" + this.plugin.getDescription().getAuthors().get(0) + " &7reloaded.");
        });
        return true;
    }
}
