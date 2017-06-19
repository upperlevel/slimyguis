package xyz.upperlevel.spigot.gui.config.placeholders;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.config.ColorUtil;
import xyz.upperlevel.spigot.gui.config.MessageUtil;

import java.util.function.Function;

public interface PlaceholderValue<T> {
    T get(Player player);

    static PlaceholderValue<Long> longValue(String l) {
        return value(l, Long::parseLong, (long) -1);
    }

    static PlaceholderValue<Integer> intValue(String i) {
        return value(i, Integer::parseInt, -1);
    }

    static PlaceholderValue<Short> shortValue(String s) {
        return value(s, Short::parseShort, (short) -1);
    }

    static PlaceholderValue<Byte> byteValue(String b) {
        return value(b, Byte::parseByte, (byte) -1);
    }

    static PlaceholderValue<String> strValue(String str) {
        if(str == null) return null;
        if(MessageUtil.hasPlaceholders(str))
            return p -> MessageUtil.placeholders(p, str);
        else
            return p -> str;
    }

    static PlaceholderValue<Color> colorValue(String c) {
        return value(c, ColorUtil::parseColor, Color.BLACK);
    }


    static <T> PlaceholderValue<T> value(String i, Function<String, T> parser, T onError) {
        if(i == null) return null;
        T parsed;
        try {
            parsed = parser.apply(i);
        } catch (Exception e) {
            if(!MessageUtil.hasPlaceholders(i))
                Bukkit.getLogger().severe("Invalid value: " + i);
            return player -> {
                try {
                    return parser.apply(MessageUtil.placeholders(player, i));
                } catch (Exception e1) {
                    Bukkit.getLogger().severe("Invalid value: " + i);
                    return onError;
                }
            };
        }
        return player -> parsed;
    }
}
