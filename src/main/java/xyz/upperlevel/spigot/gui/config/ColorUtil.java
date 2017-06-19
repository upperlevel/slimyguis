package xyz.upperlevel.spigot.gui.config;

import org.bukkit.Color;

public final class ColorUtil {

    public static Color parseColor(String str) {
        String[] parts = str.split(";");
        if(parts.length != 3)
            throw new IllegalArgumentException("Invalid color format, use \"R;G;B\"");
        return Color.fromRGB(Byte.parseByte(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    private ColorUtil(){}
}
