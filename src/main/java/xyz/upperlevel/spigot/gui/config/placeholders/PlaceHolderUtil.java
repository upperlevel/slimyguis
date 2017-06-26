package xyz.upperlevel.spigot.gui.config.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.Main;
import xyz.upperlevel.spigot.gui.config.placeholders.managers.CustomPlaceholderManager;
import xyz.upperlevel.spigot.gui.config.placeholders.managers.OfficialPlaceholderManager;

public final class PlaceHolderUtil {
    private static PlaceholderManager manager = null;


    public static PlaceholderValue<Long> parseLong(Object obj) {
        if(obj instanceof Number) {
            final long value = ((Number) obj).longValue();
            return p -> value;
        } else if(obj instanceof String)
            return PlaceholderValue.longValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Integer> parseInt(Object obj) {
        if(obj instanceof Number) {
            final int value = ((Number) obj).intValue();
            return p -> value;
        } else if(obj instanceof String)
            return PlaceholderValue.intValue((String) obj);
        else return null;
    }

    public static PlaceholderValue<Short> parseShort(Object obj) {
        if(obj instanceof Number) {
            final short value = ((Number) obj).shortValue();
            return p -> value;
        } else if(obj instanceof String)
            return PlaceholderValue.shortValue((String) obj);
        else return null;
    }


    public static PlaceholderValue<Byte> parseByte(Object obj) {
        if(obj instanceof Number) {
            final byte value = ((Number) obj).byteValue();
            return p -> value;
        } else if(obj instanceof String)
            return PlaceholderValue.byteValue((String) obj);
        else return null;
    }


    public static void tryHook() {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            manager = new OfficialPlaceholderManager();
            Main.logger().info("Successfully hooked into PlaceholderAPI");
        } else {
            manager = new CustomPlaceholderManager();
            Main.logger().warning("Cannot find PlaceholderAPI");
        }
    }

    public static PlaceholderValue<String> process(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        return PlaceholderValue.strValue(message);
    }


    public static String placeholders(Player player, String str) {
        return manager.apply(player, str);
    }

    public static boolean hasPlaceholders(String str) {
        return manager.hasPlaceholders(str);
    }


    private PlaceHolderUtil(){}
}
