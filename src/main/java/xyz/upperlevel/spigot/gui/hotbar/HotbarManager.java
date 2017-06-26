package xyz.upperlevel.spigot.gui.hotbar;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class HotbarManager {
    private static Map<Player, Hotbar> players = new HashMap<>();

    /**
     * Adds the passed HotbarLinks to the player, reprinting the inventory
     * @param player the player
     * @param links the links to add to the player's hotbar
     */
    public static void add(Player player, HotbarLink... links) {
        getOrCreate(player).add(links);
        player.updateInventory();
    }

    /**
     * Adds the passed HotbarLinks to the player, reprinting the inventory
     * @param player the player
     * @param links the links to add to the player's hotbar
     */
    public static void add(Player player, Collection<HotbarLink> links) {
        getOrCreate(player).add(links);
        player.updateInventory();
    }


    /**
     * Adds the passed HotbarLinks to the player, reprinting the inventory
     * @param player the player
     * @param links the links to add to the player's hotbar
     */
    public static void remove(Player player, HotbarLink... links) {
        Hotbar h = get(player);
        if(h != null) {
            h.remove(links);
            player.updateInventory();
        }
    }

    /**
     * Adds the passed HotbarLinks to the player, reprinting the inventory
     * @param player the player
     * @param links the links to add to the player's hotbar
     */
    public static void remove(Player player, Collection<HotbarLink> links) {
        Hotbar h = get(player);
        if(h != null) {
            h.remove(links);
            player.updateInventory();
        }
    }

    /**
     * Sets the passed Hotbar as the ONLY hotbar that the player has, removing the others set before
     * @param player the player
     * @param links the links to set
     */
    public static void set(Player player, HotbarLink... links) {
        Hotbar hotbar = getOrCreate(player);
        hotbar.clear();
        hotbar.add(links);
        player.updateInventory();
    }

    /**
     * Sets the passed Hotbar as the ONLY hotbar that the player has, removing the others set before
     * @param player the player
     * @param links the links to set
     */
    public static void set(Player player, Collection<HotbarLink> links) {
        Hotbar hotbar = getOrCreate(player);
        hotbar.clear();
        hotbar.add(links);
        player.updateInventory();
    }

    /**
     * Removes every Hotbar for the player (and their respective inventory links)
     * @param player the player
     */
    public static void clear(Player player) {
        Hotbar data = players.remove(player);
        if(data != null)
            data.clear();
        player.updateInventory();
    }


    public static void clearAll() {
        for(Map.Entry<Player, Hotbar> e : players.entrySet()) {
            e.getValue().clear();
            e.getKey().updateInventory();
        }
        players.clear();
    }

    /**
     * Reprints the player's inventory with the Hotbar links, this should be used only for visual bugs, please report them instead of brute-reprinting the inventory
     * @param player the player
     */
    public static void reprint(Player player) {
        Hotbar data = players.get(player);
        if(data != null)
            data.reprint();
        player.updateInventory();
    }

    public static void onClick(PlayerInteractEvent event) {
        if(onClick(event.getPlayer(), event.getPlayer().getInventory().getHeldItemSlot()))
            event.setCancelled(true);
    }

    public static boolean onClick(Player player, int slot) {
        Hotbar data = players.get(player);
        if(data == null) return false;
        HotbarLink link = data.getLink(slot);
        if(link == null) return false;
        link.getAction().run(player);
        return true;
    }

    /**
     * Gets the HotbarData for the passed player
     * @param player the player
     * @return the player's HotbarData
     */
    public static Hotbar get(Player player) {
        return players.get(player);
    }

    /**
     * Checks if the item held by the player is a link
     * @param player the player
     * @return true only if the item he's holding is a link
     */
    public static boolean hasLinkInHand(Player player) {
        System.out.println(player.getInventory().getHeldItemSlot());
        return isInventorySlotLink(player, player.getInventory().getHeldItemSlot());
    }

    /**
     * Gets all the links in the player's inventory
     * @param player the player
     * @return a stream of ItemStacks representing the link's display item
     */
    public static Stream<ItemStack> linkStream(Player player) {
        final Hotbar data = players.get(player);
        if (data == null) return Stream.empty();
        return data.linkStream();
    }

    /**
     * Returns true only if the player has a link that is similar to the passed item
     * @param player the player
     * @param item the item to check if is similar to any of the links
     * @return true only if the passed item is similar to the any of the link display items
     */
    public static boolean isItemSimilarToLink(Player player, ItemStack item) {
        return item != null && linkStream(player).anyMatch(item::isSimilar);
    }

    /**
     * Returns true only if the passed item is the same as one (or more) of the link's display items
     * @param player the player
     * @param item the item to check if is the same as any of the links
     * @return true only if the passed item is the same to any of the link display items
     */
    public static boolean isItemLink(Player player, ItemStack item) {
        return item != null && linkStream(player).anyMatch(item::equals);
    }

    /**
     * Returns true if any of the passed items is the same to any of the links
     * @param player the player
     * @param items the items
     * @return true if any of the passed items is the same to any of the links
     */
    public static boolean anyItemLink(Player player, ItemStack... items) {
        return linkStream(player).anyMatch(i -> {
            for(ItemStack s : items)
                if(s.equals(i))
                    return true;
            return false;
        });
    }

    /**
     * Returns true only if the slot passed is a Link. This works only with the Player's main Inventory (Hotbar: [0; 8])
     * @param player the player
     * @param slot the slot to check
     * @return true only if the slot passed is a link
     */
    public static boolean isInventorySlotLink(Player player, int slot) {
        Hotbar data = players.get(player);
        return data != null && data.isSlotLink(slot);
    }

    public static Hotbar getOrCreate(Player p) {
        return players.computeIfAbsent(p, Hotbar::new);
    }
}
