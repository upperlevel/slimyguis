package xyz.upperlevel.spigot.gui.config;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.upperlevel.spigot.gui.Main;
import xyz.upperlevel.spigot.gui.config.util.Config;
import xyz.upperlevel.spigot.gui.hotbar.Hotbar;
import xyz.upperlevel.spigot.gui.hotbar.HotbarLink;
import xyz.upperlevel.spigot.gui.hotbar.HotbarManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
public class ConfigHotbar {
    private static List<ConfigHotbar> hotbars = new ArrayList<>();

    private String permission;
    private List<ConfigItem> items;

    public void print(Player player) {
        if(!player.hasPermission(permission))
            return;

        Hotbar hotbar = HotbarManager.getOrCreate(player);
        Logger logger = Bukkit.getLogger();//TODO better logging
        for(ConfigItem item : items) {
            for (int slot : item.getSlots()) {
                if(hotbar.isFull()) {
                    logger.severe("Hotbar full, cannot add item " + item);
                    return;
                }
                HotbarLink link = HotbarLink.of(item.getClick(), item.getItem().toItemStack(player));
                if(slot > 0) {
                    if(!hotbar.setLink(link, slot))
                        logger.severe("Slot " + slot + " already occupied, cannot add item " + item);
                } else
                    hotbar.add(link);
            }
        }
        if(hotbar.isEmpty())
            HotbarManager.clear(player);
    }

    public static void onLoad(File folder) {
        loadFolder(folder);

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                printAll(event.getPlayer());
            }
        }, Main.getInstance());
        for(Player player : Bukkit.getOnlinePlayers())
            printAll(player);
    }

    public static void printAll(Player p) {
        for(ConfigHotbar h : hotbars)
            h.print(p);
    }

    @SuppressWarnings("unchecked")
    public static void load(Config config) {
        String permission = (String) config.get("permission");
        List<ConfigItem> items = ConfigItem.deserialize(config.getConfigList("items"));
        hotbars.add(new ConfigHotbar(permission, items));
    }

    public static void loadFolder(File folder) {
        hotbars.clear();
        if(folder.exists()) {
            if(folder.isDirectory()) {
                File[] files = folder.listFiles();
                if(files == null) {
                    Bukkit.getLogger().severe("Error while reading " + folder + " files");
                    return;
                }
                for(File file : files) {
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    try {
                        load(Config.wrap(config));
                    } catch (InvalidGuiConfigurationException e) {
                        Bukkit.getLogger().severe(e.getErrorMessage("Invalid configuration in file \"" + file + "\""));
                    } catch (Exception e) {
                        Bukkit.getLogger().log(Level.SEVERE, "Unknown error thrown while reading config in file \"" + file + "\"");
                    }
                }
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
}
