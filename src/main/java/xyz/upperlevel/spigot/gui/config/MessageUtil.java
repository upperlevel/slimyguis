package xyz.upperlevel.spigot.gui.config;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

public final class MessageUtil {

    public static PlaceholderValue<String> process(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        return PlaceholderValue.strValue(message);
    }


    public static String placeholders(Player player, String str) {
        return str;
    }


    private MessageUtil(){}

    public static boolean hasPlaceholders(String str) {
        return false;
    }
}
