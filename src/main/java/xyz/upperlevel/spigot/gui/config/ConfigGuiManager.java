package xyz.upperlevel.spigot.gui.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.GuiManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigGuiManager {
    private static Map<String, Gui> guis = new HashMap<>();

    public static void add(String id, Gui gui) {
        guis.put(id, gui);
    }

    public static void add(ConfigGui gui) {
        guis.put(gui.getId(), gui);
    }

    public static void remove(String id) {
        guis.remove(id);
    }

    public static void clear() {
        guis.clear();
    }

    public static Gui get(String id) {
        return guis.get(id);
    }

    public static void open(Player player, String id) {
        GuiManager.open(player, get(id));
    }

    public static void add(File file) {//TODO better logger
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while loading the file \"" + file + "\"", e);
            return;
        } catch (InvalidConfigurationException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Invalid configuration in file \"" + file + "\":", e);
            return;
        }
        final String id = file.getName().replaceFirst("[.][^.]+$", "");
        Gui gui;
        try {
            gui = ConfigGui.deserialize(config.getValues(true), id);
        } catch (InvalidGuiConfigurationException e) {
            Bukkit.getLogger().severe(e.getErrorMessage("Invalid configuration in file \"" + file + "\""));
            return;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Unknown error thrown while reading config in file \"" + file + "\"", e);
            return;
        }
        guis.put(id, gui);
        Bukkit.getLogger().log(Level.INFO, "[GuiApi] Successfully loaded gui " + id);
    }

    public static void loadFolder(File folder) {
        guis.clear();
        if(folder.exists()) {
            if(folder.isDirectory()) {
                File[] files = folder.listFiles();
                if(files == null) {
                    Bukkit.getLogger().severe("Error while reading " + folder + " files");
                    return;
                }
                for(File file : files)
                    add(file);
            } else {
                //TODO: better logging
                Bukkit.getLogger().severe("\"" + folder.getName() + "\" isn't a folder!");
            }
        } else {
            try {
                folder.mkdirs();
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "Error creating the directory " + folder.getName(), e);
            }
        }
    }

    public static void onLoad(File folder) {
        loadFolder(folder);
    }

}