package xyz.upperlevel.spigot.gui;

import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.upperlevel.spigot.gui.config.ConfigGui;
import xyz.upperlevel.spigot.gui.config.InvalidGuiConfigurationException;
import xyz.upperlevel.spigot.gui.config.util.Config;
import xyz.upperlevel.spigot.gui.impl.FolderGui;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * This manager is the class that manages the player histories in a stack-like system
 * it has multiple operations for interacting with the Gui stack:
 * open: appends the gui to the stack
 * close: clears the stack
 * back: removes the last gui from the stack
 * change: back + open
 * It's suggested NOT to chain operations when not needed, it could cause client flickering
 * <p>
 * This system does not support recursion
 */
public class GuiManager {

    private static Map<String, Gui> guis = new HashMap<>();

    private static Map<Player, LinkedList<Gui>> histories = new HashMap<>();

    @Getter
    private static boolean called = false;

    /**
     * Loads the given gui file configuration.
     *
     * @param file loads the given file
     */
    public static void load(File file) {
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException e) {
            SlimyGuis.logger().log(Level.SEVERE, "Error while loading the file \"" + file + "\"", e);
            return;
        } catch (InvalidConfigurationException e) {
            SlimyGuis.logger().log(Level.SEVERE, "Invalid configuration in file \"" + file + "\":", e);
            return;
        }
        String id = file.getName().replaceFirst("[.][^.]+$", "");
        CustomGui gui;
        try {
            gui = CustomGui.deserialize(id, Config.wrap(config));
        } catch (InvalidGuiConfigurationException e) {
            SlimyGuis.logger().severe(e.getErrorMessage("Invalid configuration in file \"" + file + "\""));
            return;
        } catch (Exception e) {
            SlimyGuis.logger().log(Level.SEVERE, "Unknown error thrown while reading config in file \"" + file + "\"", e);
            return;
        }
        register(id, gui);
        SlimyGuis.logger().log(Level.INFO, "Successfully loaded gui " + id);
    }

    /**
     * Loads a folder that contains gui configurations.
     * This will not delete present guis.
     *
     * @param folder the folder to load
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
            } else {
                SlimyGuis.logger().severe("\"" + folder.getName() + "\" isn't a folder!");
            }
        } else {
            try {
                folder.mkdirs();
            } catch (Exception e) {
                SlimyGuis.logger().log(Level.SEVERE, "Error creating the directory " + folder.getName(), e);
            }
        }
    }

    /**
     * Registers the given gui with a custom id.
     *
     * @param id  the id to associate to the gui
     * @param gui the gui to register
     */
    public static void register(String id, Gui gui) {
        guis.put(id, gui);
    }

    /**
     * Registers the given gui.
     *
     * @param gui the gui to register
     */
    public static void register(Gui gui) {
        guis.put(gui.getId(), gui);
    }

    /**
     * Gets a gui by given id.
     *
     * @param id the gui's id
     * @return the gui fetched by id
     */
    public static Gui get(String id) {
        return guis.get(id);
    }

    /**
     * Unregisters the given gui by id.
     *
     * @param id the id of the gui to unregister
     * @return the gui unregistered
     */
    public static Gui unregister(String id) {
        return guis.remove(id);
    }

    /**
     * Unregisters the given gui.
     *
     * @param gui the gui to unregister
     * @return the gui unregistered
     */
    public static Gui unregister(Gui gui) {
        return guis.remove(gui.getId());
    }

    /**
     * Opens a Gui to a player, adding it to the stack. If the closeOthers parameter is specified it will remove the stack first
     *
     * @param player      the player that is opening the api
     * @param gui         the gui to be opened
     * @param closeOthers if set to true the GUI history would be cleaned
     */
    public static void open(Player player, Gui gui, boolean closeOthers) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = getOrCreate(player);
            if (!g.isEmpty()) {
                g.peek().onClose(player);

                if (closeOthers)
                    g.clear();
            }

            gui.onOpen(player);
            gui.print(player);
            g.push(gui);
        } finally {
            called = false;
        }
    }

    /**
     * Opens a Gui to a player, adding it to the stack
     *
     * @param player the player that is opening the api
     * @param gui    the gui to be opened
     */
    public static void open(Player player, Gui gui) {
        open(player, gui, false);
    }

    /**
     * Closes *ALL* the player's Guis, clearing his stack history
     *
     * @param player the player
     */
    public static void close(Player player) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = histories.remove(player);
            if (g == null || g.isEmpty())
                return;
            g.peek().onClose(player);
            player.closeInventory();
            g.clear();
        } finally {
            called = false;
        }
    }

    public static void closeAll() {
        if (called) return;
        called = true;
        try {
            for (Player player : histories.keySet())
                player.closeInventory();
            histories.clear();
        } finally {
            called = false;
        }
    }

    /**
     * Closes only the currently open Gui, opening the previous Gui in the stack if present, otherwise it will close the player's inventory
     *
     * @param player the player
     */
    public static void back(Player player) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = histories.get(player);
            if (g == null || g.isEmpty())
                return;
            g.pop().onClose(player);
            if (!g.isEmpty()) {
                final Gui gui = g.peek();
                gui.onOpen(player);
                gui.print(player);
            } else
                player.closeInventory();
        } finally {
            called = false;
        }
    }

    /**
     * Changes the last Gui in the player's stack (if any) with the one specified in the arguments, this could be thought as back + open
     *
     * @param player the player
     * @param gui    the Gui that will be appended instead of the last one
     */
    public static void change(Player player, Gui gui) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = getOrCreate(player);
            if (!g.isEmpty())
                g.pop().onClose(player);
            g.push(gui);
            gui.onOpen(player);
            gui.print(player);
        } finally {
            called = false;
        }
    }

    /**
     * Called when a player clicks on the inventory, the filters to check if the Player clicked on the GUi's inventory should be made outside of this method
     *
     * @param event the click event
     */
    public static void onClick(InventoryClickEvent event) {
        HumanEntity h = event.getWhoClicked();
        if (!(h instanceof Player))
            return;
        LinkedList<Gui> g = histories.get(h);
        if (g != null && !g.isEmpty()) {
            //Event cancelled BEFORE the method call to permit the un-cancelling
            event.setCancelled(true);
            g.peek().onClick(event);
            //Creative idiots could copy the items
            if (event.isShiftClick() && event.getWhoClicked().getGameMode() == GameMode.CREATIVE)
                ((Player) event.getWhoClicked()).updateInventory();
        }
    }

    /**
     * Gets the Gui history (also called stack) of the player. If
     *
     * @param player the player
     * @return the player's Gui history
     */
    public static LinkedList<Gui> get(Player player) {
        return histories.get(player);
    }

    private static LinkedList<Gui> getOrCreate(Player player) {
        return histories.computeIfAbsent(player, (pl) -> new LinkedList<>());
    }
}
