package xyz.upperlevel.spigot.gui.hotbar;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.upperlevel.spigot.gui.ItemLink;
import xyz.upperlevel.spigot.gui.SlimyGuis;
import xyz.upperlevel.spigot.gui.config.InvalidGuiConfigurationException;
import xyz.upperlevel.spigot.gui.config.util.Config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class HotbarManager {

    private static final Map<String, Hotbar> hotbars = new HashMap<>();
    private static final Map<Player, Hotbar> players = new HashMap<>();

    /**
     * Registers the given hotbar with the associated id.
     *
     * @param id     the id of the hotbar to register
     * @param hotbar the hotbar to register
     */
    public static void register(String id, Hotbar hotbar) {
        hotbars.put(id, hotbar);
    }

    /**
     * Registers the given hotbar.
     *
     * @param hotbar the hotbar to register
     */
    public static void register(Hotbar hotbar) {
        hotbars.put(hotbar.getId(), hotbar);
    }

    /**
     * Unregisters the hotbar by its id.
     *
     * @param id the id of the hotbar to remove
     * @return the hotbar removed
     */
    public static Hotbar unregister(String id) {
        return hotbars.remove(id);
    }

    public static Hotbar unregister(Hotbar hotbar) {
        return hotbars.remove(hotbar.getId());
    }

    /**
     * Gets an hotbar by its id.
     *
     * @param id the id of the hotbar
     * @return the hotbar fetched
     */
    public static Hotbar get(String id) {
        return hotbars.get(id);
    }

    /**
     * Gets the hotbar held by the given player.
     *
     * @param player the player
     * @return the hotbar held by the player
     */
    public static Hotbar get(Player player) {
        return players.get(player);
    }

    /**
     * Loads a new hotbar from the given file.
     *
     * @param file the file where to load the hotbar
     * @return the hotbar loaded
     */
    public static Hotbar load(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        final String id = file.getName().replaceFirst("[.][^.]+$", "");
        Hotbar hotbar;
        try {
            hotbar = Hotbar.deserialize(id, Config.wrap(config));
        } catch (InvalidGuiConfigurationException e) {
            SlimyGuis.logger().severe(e.getErrorMessage("Invalid configuration in file \"" + file + "\""));
            return null;
        } catch (Exception e) {
            SlimyGuis.logger().log(Level.SEVERE, "Unknown error thrown while reading config in file \"" + file + "\"", e);
            return null;
        }
        return hotbar;
    }

    /**
     * Loads all hotbars found in the given folder.
     *
     * @param folder the folder where to load the hotbars
     */
    public static void loadFolder(File folder) {
        if (folder.exists()) {
            if (folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files == null) {
                    SlimyGuis.logger().severe("Error while reading " + folder + " files");
                    return;
                }
                for (File file : files)
                    load(file);
            } else
                SlimyGuis.logger().severe("\"" + folder.getName() + "\" isn't a folder!");
        } else {
            try {
                folder.mkdirs();
            } catch (Exception e) {
                SlimyGuis.logger().log(Level.SEVERE, "Error creating the directory " + folder.getName(), e);
            }
        }
    }

    /**
     * Gets a list of all hotbars with onJoin field set on true.
     *
     * @return a list of hotbars with onJoin set to true
     */
    public static List<Hotbar> getJoinHotbars() {
        return hotbars.values().stream()
                .filter(Hotbar::isOnJoin)
                .collect(Collectors.toList());
    }

    /**
     * Gives an hotbar to a player.
     *
     * @param player the player
     * @param hotbar the hotbar
     */
    public static void give(Player player, Hotbar hotbar) {
        players.put(player, hotbar);
        hotbar.give(player);
    }

    /**
     * Checks if the given player is holding any hotbar.
     *
     * @param player the player
     * @return true if is holding any hotbar, otherwise false
     */
    public static boolean isHolding(Player player) {
        return players.containsKey(player);
    }

    /**
     * Checks if the given player is holding the given hotbar.
     *
     * @param player the player
     * @param hotbar the hotbar
     * @return true if is holding the passed hotbar, otherwise false
     */
    public static boolean isHolding(Player player, Hotbar hotbar) {
        Hotbar h = players.get(player);
        return h != null && h.equals(hotbar);
    }

    /**
     * Removes an hotbar from a player.
     *
     * @param player the player
     */
    public static void remove(Player player) {
        Hotbar h = players.remove(player);
        if (h != null)
            h.remove(player);
    }

    public static boolean onClick(PlayerInteractEvent event) {
        if (onClick(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot())) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    public static boolean onClick(Player player, int slot) {
        Hotbar data = players.get(player);
        if (data == null) return false;
        ItemLink item = data.getLink(slot);
        if (item == null) return false;
        item.getLink().run(player);
        return true;
    }

    /**
     * Checks if the item held by the player is a link.
     *
     * @param player the player
     * @return true only if the item he's holding is a link
     */
    public static boolean hasLinkInHand(Player player) {
        System.out.println(player.getInventory().getHeldItemSlot());
        return isInventorySlotLink(player, player.getInventory().getHeldItemSlot());
    }

    /**
     * Returns true only if the slot passed is a Link. This works only with the Player's main Inventory (Hotbar: [0; 8])
     *
     * @param player the player
     * @param slot   the slot to check
     * @return true only if the slot passed is a link
     */
    public static boolean isInventorySlotLink(Player player, int slot) {
        Hotbar data = players.get(player);
        return data != null && data.isSlotLink(slot);
    }

    public static void clearAll() {
        for (Player p : players.keySet())
            remove(p);
        players.clear();
    }
}
