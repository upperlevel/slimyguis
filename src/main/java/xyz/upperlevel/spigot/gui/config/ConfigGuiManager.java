package xyz.upperlevel.spigot.gui.config;

import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.GuiManager;

import java.util.HashMap;
import java.util.Map;

public class ConfigGuiManager {
    private static Map<String, Gui> guis = new HashMap<>();

    public static void add(String id, Gui gui) {
        guis.put(id, gui);
    }

    public static void remove(String id) {
        guis.remove(id);
    }

    public static Gui get(String id) {
        return guis.get(id);
    }

    public static void open(Player player, String id) {
        GuiManager.open(player, get(id));
    }
}
