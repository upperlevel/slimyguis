package xyz.upperlevel.spigot.gui;

import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This manager is the class that manages the player guis in a stack-like system
 * it has multiple operations for interacting with the Gui stack:
 * open: appends the gui to the stack
 * close: clears the stack
 * back: removes the last gui from the stack
 * change: back + open
 * It's suggested NOT to chain operations when not needed, it could cause client flickering
 *
 * This system does not support recursion
 */
public class GuiManager {
    private static Map<Player, LinkedList<Gui>> guis = new HashMap<>();
    @Getter
    private static boolean called = false;

    /**
     * Opens a Gui to a player, adding it to the stack. If the closeOthers parameter is specified it will remove the stack first
     * @param player the player that is opening the api
     * @param gui the gui to be opened
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
     * @param player the player that is opening the api
     * @param gui the gui to be opened
     */
    public static void open(Player player, Gui gui) {
        open(player, gui, false);
    }

    /**
     * Closes *ALL* the player's Guis, clearing his stack history
     * @param player the player
     */
    public static void close(Player player) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = guis.remove(player);
            if (g == null || g.isEmpty())
                return;
            g.peek().onClose(player);
            player.closeInventory();
            g.clear();
        } finally {
            called = false;
        }
    }

    /**
     * Closes only the currently open Gui, opening the previous Gui in the stack if present, otherwise it will close the player's inventory
     * @param player the player
     */
    public static void back(Player player) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = guis.get(player);
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
     * @param player the player
     * @param gui the Gui that will be appended instead of the last one
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
     * @param event the click event
     */
    public static void onClick(InventoryClickEvent event) {
        HumanEntity h = event.getWhoClicked();
        if (!(h instanceof Player))
            return;
        LinkedList<Gui> g = guis.get(h);
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
     * @param player the player
     * @return the player's Gui history
     */
    public static LinkedList<Gui> get(Player player) {
        return guis.get(player);
    }

    private static LinkedList<Gui> getOrCreate(Player player) {
        return guis.computeIfAbsent(player, (pl) -> new LinkedList<>());
    }
}
