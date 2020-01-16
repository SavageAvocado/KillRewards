package net.savagedev.killrewards.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtils {
    private MessageUtils() {
        throw new UnsupportedOperationException("This class contains only static methods. No reason to instantiate it.");
    }

    public static void message(CommandSender sender, final String message) {
        if (message != null && !message.equals("none")) {
            sender.sendMessage(color(message));
        }
    }

    private static String color(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
