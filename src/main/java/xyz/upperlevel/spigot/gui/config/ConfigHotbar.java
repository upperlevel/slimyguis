package xyz.upperlevel.spigot.gui.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.Main;
import xyz.upperlevel.spigot.gui.config.util.Config;
import xyz.upperlevel.spigot.gui.hotbar.Hotbar;
import xyz.upperlevel.spigot.gui.hotbar.HotbarLink;
import xyz.upperlevel.spigot.gui.hotbar.HotbarManager;
import xyz.upperlevel.spigot.gui.link.Link;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@AllArgsConstructor
public class ConfigHotbar {
    private static Map<String, ConfigHotbar> hotbars = new HashMap<>();

    @Getter
    private final String id;
    private String permission;
    @Getter
    private List<ConfigItem> items;
    private boolean onJoin;

    public boolean print(Player player) {
        if(permission != null && !player.hasPermission(permission))
            return false;

        Hotbar hotbar = HotbarManager.getOrCreate(player);
        for(ConfigItem item : items) {
            for (int slot : item.getSlots()) {
                if(hotbar.isFull()) {
                    Main.logger().severe("Hotbar full, cannot add item " + item);
                    return false;
                }
                HotbarLink link = new ConfigHotbarLink(id, item.getClick(), item.getItem().toItemStack(player));
                if(slot >= 0) {
                    if(!hotbar.setLink(link, slot))
                        Main.logger().severe("Slot " + slot + " already occupied, cannot add item " + item);
                } else
                    hotbar.add(link);
            }
        }
        if(hotbar.isEmpty())
            HotbarManager.clear(player);
        return true;
    }

    public boolean isPrinted(Player player) {
        Hotbar hotbar = HotbarManager.get(player);
        if(hotbar == null) return false;
        return hotbar.stream().anyMatch(f -> (f instanceof ConfigHotbarLink) && ((ConfigHotbarLink) f).id.equals(id));
    }

    public void remove(Player player) {
        Hotbar hotbar = HotbarManager.get(player);
        if(hotbar == null) return;
        if(hotbar.remove(l -> (l instanceof ConfigHotbarLink) && ((ConfigHotbarLink) l).id.equals(id)))
            player.updateInventory();
    }

    public static void onLoad(File folder) {
        loadFolder(folder);

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                joinPrint(event.getPlayer());
            }
        }, Main.getInstance());
        for(Player player : Bukkit.getOnlinePlayers())
            joinPrint(player);
    }

    public static void joinPrint(Player p) {
        for(ConfigHotbar h : hotbars.values())
            if(h.onJoin)
                h.print(p);
            p.updateInventory();
    }

    @SuppressWarnings("unchecked")
    public static void load(Config config, String id) {
        String permission = (String) config.get("permission");
        List<ConfigItem> items = ConfigItem.deserialize(config.getConfigList("items"));
        hotbars.put(id, new ConfigHotbar(id, permission, items, config.getBool("onJoin", true)));
    }

    public static ConfigHotbar get(String id) {
        return hotbars.get(id);
    }

    public static void loadFolder(File folder) {
        hotbars.clear();
        if(folder.exists()) {
            if(folder.isDirectory()) {
                File[] files = folder.listFiles();
                if(files == null) {
                    Main.logger().severe("Error while reading " + folder + " files");
                    return;
                }
                for(File file : files) {
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    final String id = file.getName().replaceFirst("[.][^.]+$", "");
                    try {
                        load(Config.wrap(config), id);
                    } catch (InvalidGuiConfigurationException e) {
                        Main.logger().severe(e.getErrorMessage("Invalid configuration in file \"" + file + "\""));
                    } catch (Exception e) {
                        Main.logger().log(Level.SEVERE, "Unknown error thrown while reading config in file \"" + file + "\"", e);
                    }
                }
            } else {
                Main.logger().severe("\"" + folder.getName() + "\" isn't a folder!");
            }
        } else {
            try {
                folder.mkdirs();
            } catch (Exception e) {
                Main.logger().log(Level.SEVERE, "Error creating the directory " + folder.getName(), e);
            }
        }
    }

    @RequiredArgsConstructor
    public static class ConfigHotbarLink implements HotbarLink {
        @Getter
        private final String id;
        @Getter
        private final Link action;
        @Getter
        private final ItemStack display;
    }
}
