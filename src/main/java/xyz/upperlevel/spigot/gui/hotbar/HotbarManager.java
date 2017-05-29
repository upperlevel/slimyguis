package xyz.upperlevel.spigot.gui.hotbar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.hotbar.handlers.HotbarJoiner;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class HotbarManager {
    private static Map<Player, HotbarData> players = new HashMap<>();

    /**
     * Adds the passed Hotbar to the player, reprinting the inventory
     * @param player the player
     * @param hotbar the hotbar to add to the player's inventory
     */
    public static void add(Player player, Hotbar hotbar) {
        getOrCreate(player).addHandler(hotbar);
    }

    /**
     * Sets the passed Hotbar as the ONLY hotbar that the player has, removing the others set before
     * @param player the player
     * @param hotbar the hotbar to set
     */
    public static void set(Player player, Hotbar hotbar) {
        getOrCreate(player).setHandler(hotbar);
    }

    /**
     * Removes every Hotbar for the player (and their respective inventory links)
     * @param player the player
     */
    public static void remove(Player player) {
        HotbarData data = players.remove(player);
        if(data != null)
            data.remove();
    }

    /**
     * Reprints the player's inventory with the Hotbar links, this should be used only for visual bugs, please report them instead of brute-reprinting the inventory
     * @param player the player
     */
    public static void reprint(Player player) {
        HotbarData data = players.get(player);
        if(data != null)
            data.reprint();
    }

    public static void onClick(PlayerInteractEvent event) {
        HotbarData data = players.get(event.getPlayer());
        if (data != null) {
            //event.getItem() may change depending on the hand "used" by the click so we get it directly
            if (data.onClick(event.getPlayer().getInventory().getItemInMainHand(), event.getAction(), event.getClickedBlock(), event.getBlockFace()))
                event.setCancelled(true);
        }
    }

    public static boolean onClick(Player player, ItemStack item, Action action, Block clickedBlock, BlockFace clickedFace) {
        HotbarData data = players.get(player);
        if(data == null) return false;
        else return data.onClick(item, action, clickedBlock, clickedFace);
    }

    /**
     * Gets the HotbarData for the passed player
     * @param player the player
     * @return the player's HotbarData
     */
    public static HotbarData get(Player player) {
        return players.get(player);
    }

    /**
     * Checks if the item held by the player is a link
     * @param player the player
     * @return true only if the item he's holding is a link
     */
    public static boolean hasLinkInHand(Player player) {
        return isInventorySlotLink(player, player.getInventory().getHeldItemSlot());
    }

    /**
     * Gets all the links in the player's inventory
     * @param player the player
     * @return a stream of ItemStacks representing the link's display item
     */
    public static Stream<ItemStack> getLinks(Player player) {
        final HotbarData data = players.get(player);
        if (data == null) return Stream.empty();
        return data.getLinks();
    }

    /**
     * Returns true only if the player has a link that is similar to the passed item
     * @param player the player
     * @param item the item to check if is similar to any of the links
     * @return true only if the passed item is similar to the any of the link display items
     */
    public static boolean isItemSimilarToLink(Player player, ItemStack item) {
        return item != null && getLinks(player).anyMatch(item::isSimilar);
    }

    /**
     * Returns true only if the passed item is the same as one (or more) of the link's display items
     * @param player the player
     * @param item the item to check if is the same as any of the links
     * @return true only if the passed item is the same to any of the link display items
     */
    public static boolean isItemLink(Player player, ItemStack item) {
        return item != null && getLinks(player).anyMatch(item::equals);
    }

    /**
     * Returns true if any of the passed items is the same to any of the links
     * @param player the player
     * @param items the items
     * @return true if any of the passed items is the same to any of the links
     */
    public static boolean anyItemLink(Player player, ItemStack... items) {
        return getLinks(player).anyMatch(i -> {
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
        HotbarData data = players.get(player);
        return data != null && slot < data.lastSize;
    }




    private static HotbarData getOrCreate(Player p) {
        return players.computeIfAbsent(p, HotbarData::new);
    }

    @RequiredArgsConstructor
    public static class HotbarData {
        @Getter
        private final Player player;
        private Hotbar handler;
        private HotbarView lastPrint;

        private int lastSize = -1;

        public void addHandler(Hotbar handler) {
            if (this.handler == null)
                this.handler = handler;
            else if (this.handler instanceof HotbarJoiner)
                ((HotbarJoiner) this.handler).add(handler);
            else {
                this.handler = new HotbarJoiner()
                        .add(handler)
                        .add(handler);
            }
            reprint();
        }

        public void setHandler(Hotbar handler) {
            this.handler = handler;
            reprint();
        }

        public void reprint() {
            if(handler == null) return;
            HotbarView view = handler.print(player);

            final Inventory inv = player.getInventory();

            Collection<ItemStack> items = view.getItems();//Get the compiled links
            final int size = items.size();//And save the size of them for later computation

            if(size > 9)//check if the items aren't too many
                throw new IllegalStateException("Too many links!");

            //copy the links (items) into the real toolbar
            int index = 0;
            for(ItemStack item : items)
                inv.setItem(index++, item);//The toolbar is from 0 to 8, so offset: 0

            //remove the items that were in the inventory from the last view, if any
            for(int i = size; i < lastSize; i++)
                inv.setItem(i, null);
            player.updateInventory();
            lastPrint = view;

            lastSize = size;//Update the lastSize fot the next reprint
        }

        public boolean onClick(ItemStack item, Action action, Block clickedBlock, BlockFace clickedFace) {
            //If there's no print the player could not onClick it (lol)
            if(lastPrint == null) return false;
            return lastPrint.onClick(player, item, player.getInventory().getHeldItemSlot(), action, clickedBlock, clickedFace);
        }

        public void remove() {
            handler = null;
            final Inventory inv = player.getInventory();
            for(int i = 0; i < lastSize; i++)
                inv.setItem(i, null);
            player.updateInventory();
        }

        public boolean isIndexLink(int index) {
            return index < lastSize;
        }

        public Stream<ItemStack> getLinks() {
            final ListIterator<ItemStack> i = player.getInventory().iterator();
            return StreamSupport.stream(Spliterators.spliterator(new Iterator<ItemStack>() {
                @Override
                public boolean hasNext() {
                    return i.nextIndex() < lastSize;
                }

                @Override
                public ItemStack next() {
                    return i.next();
                }
            }, lastSize, 0), false);
        }

        public boolean isItemLink(ItemStack item) {
            return getLinks().anyMatch(item::equals);
        }

        public boolean isItemSimilarToLink(ItemStack item) {
            return item != null && getLinks().anyMatch(item::isSimilar);
        }

        public boolean anyItemLink(ItemStack... items) {
            return getLinks().anyMatch((i) -> {
                for(ItemStack s : items)
                    if(s != null && s.equals(i))
                        return true;
                return false;
            });
        }
    }
}
