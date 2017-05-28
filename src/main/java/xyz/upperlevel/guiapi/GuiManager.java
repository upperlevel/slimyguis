package xyz.upperlevel.guiapi;

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
    private static boolean called = false;


    public static void open(Player player, Gui gui, boolean closeOthers) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = getOrCreate(player);
            if (!g.isEmpty())
                g.peek().onClose(player);

            if(closeOthers)
                g.clear();

            gui.onOpen(player);
            gui.print(player);
            g.push(gui);
        } finally {
            called = false;
        }
    }

    public static void open(Player player, Gui gui) {
        open(player, gui, false);
    }

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

    public static void back(Player player) {
        if (called) return;
        called = true;
        try {
            LinkedList<Gui> g = guis.get(player);
            if (g == null || g.isEmpty())
                return;
            g.pop().onClose(player);
            if (!g.isEmpty()) {
                g.peek().onOpen(player);
                g.peek().print(player);
            } else
                player.closeInventory();
        } finally {
            called = false;
        }
    }

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

    public static LinkedList<Gui> get(Player p) {
        return guis.get(p);
    }

    private static LinkedList<Gui> getOrCreate(Player p) {
        return guis.computeIfAbsent(p, (pl) -> new LinkedList<>());
    }
}
