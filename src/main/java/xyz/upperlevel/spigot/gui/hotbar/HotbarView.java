package xyz.upperlevel.spigot.gui.hotbar;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.ItemLink;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class HotbarView {

    private final Player player;
    private final ItemLink[] links = new ItemLink[9];

    // this list stores last items created when the hotbar has been printed
    private final Set<ItemStack> items = new HashSet<>();
    private final Set<Hotbar> hotbars = new HashSet<>();

    public HotbarView(Player player) {
        this.player = player;
    }

    /**
     * Updates hotbar items.
     */
    public void update() {
        print();
    }

    /**
     * Prints the current hotbar to the referring player.
     */
    public void print() {
        items.clear();
        Inventory inv = player.getInventory();
        for (int slot = 0; slot < links.length; slot++) {
            if (links[slot] != null) {
                ItemStack i = links[slot].getDisplay().toItemStack(player);
                inv.setItem(slot, i);
                items.add(i);
            } else
                inv.setItem(slot, null);
        }
        player.updateInventory();
    }

    /**
     * Gets link by the given slot.
     *
     * @param slot the slot
     * @return the link at this slot, null if none
     */
    public ItemLink getLink(int slot) {
        return links[slot];
    }

    /**
     * Checks if the given item is a link.
     *
     * @param item the item to test
     * @return true if it is a link, otherwise false
     */
    public boolean isLink(ItemStack item) {
        return items.contains(item);
    }

    /**
     * Checks if the given slot contains a link.
     *
     * @param slot the given slot
     * @return true if the slot passed contains an item, otherwise false
     */
    public boolean isSlotLink(int slot) {
        return links[slot] != null;
    }

    /**
     * Removes all the items of the hotbars hold by the player.
     */
    public void clear() {
        for (int slot = 0; slot < links.length; slot++)
            links[slot] = null;
        items.clear();
        hotbars.clear();
        update();
    }

    /**
     * Adds the given link to this hotbar view.
     *
     * @param link the link to add
     * @return true if added, otherwise false (if the hotbar is full)
     */
    public boolean add(ItemLink link) {
        for (int slot = 0; slot < links.length; slot++) {
            if (links[slot] == null) {
                links[slot] = link;
                return true;
            }
        }
        update();
        return false;
    }

    /**
     * Sets the given link to the given slot.
     *
     * @param slot the slot to set the item in
     * @param link the link to set
     */
    public void set(int slot, ItemLink link) {
        links[slot] = link;
        update();
    }

    /**
     * Sets the given hotbar links to the current view in the exact position they are.
     *
     * @param hotbar the hotbar to set
     * @return true if all the hotbar has been set, false if at least one item is overriding another one
     */
    public boolean set(Hotbar hotbar) {
        if (hotbars.contains(hotbar))
            return false;
        for (int slot = 0; slot < hotbar.getLinks().size(); slot++) {
            ItemLink l = hotbar.getLink(slot);
            if (links[slot] != null)
                return false;
            links[slot] = l;
        }
        hotbars.add(hotbar);
        update();
        return true;
    }

    /**
     * Checks if the player has the given hotbar opened.
     *
     * @param hotbar the hotbar
     * @return true if the player is holding the hotbar, otherwise false
     */
    public boolean isHolding(Hotbar hotbar) {
        return hotbars.contains(hotbar);
    }

    public boolean remove(Hotbar hotbar) {
        if (!hotbars.contains(hotbar))
            return false;
        for (int slot = 0; slot < hotbar.getLinks().size(); slot++) {
            ItemLink l = hotbar.getLink(slot);
            if (l != null)
                links[slot] = null;
        }
        hotbars.remove(hotbar);
        update();
        return true;
    }
}
