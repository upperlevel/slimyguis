package xyz.upperlevel.spigot.gui.hotbar;


import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.ItemLink;
import xyz.upperlevel.spigot.gui.config.util.Config;
import xyz.upperlevel.spigot.gui.link.Link;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Data
public class Hotbar {

    private String id;

    private ItemLink[] items = new ItemLink[9];
    private List<ItemLink> unmodifiableView = Collections.unmodifiableList(Arrays.asList(items));
    private int nextFree = 0;
    private int size = 0;

    private String permission;
    private boolean onJoin;

    public Hotbar() {
        this(null);
    }

    public Hotbar(String id) {
        this.id = id;
    }

    public boolean hasId() {
        return id != null;
    }

    /**
     * Returns the link present at that slot.
     *
     * @param slot the link's slot
     * @return the link present at that slot
     */
    public ItemLink getLink(int slot) {
        return items[slot];
    }

    /**
     * Returns an unmodifiable list of items.
     *
     * @return the player's items
     */
    public List<ItemLink> getItems() {
        return unmodifiableView;
    }

    /**
     * Checks if the hotbar is empty.
     *
     * @return true only if the player has one or more items
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Checks if the hotbar is full.
     *
     * @return true only if the hotbar is full
     */
    public boolean isFull() {
        return nextFree == -1;
    }

    /**
     * Returns the number of items in the hotbar.
     *
     * @return the number of items in the hotbar
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the number of slots available.
     *
     * @return the number of slots available
     */
    public int getFree() {
        return 9 - size;
    }

    private void findNextFree() {
        do {
            if (++nextFree >= 9) {
                nextFree = -1;
                break;
            }
        } while (items[nextFree] != null);
    }

    /**
     * Sets the link passed as parameter in the specified slot, if empty.
     *
     * @param link the link to give
     * @param slot the slot in which the link will go
     * @return true only if the operation changed the hotbar
     */
    public boolean setLink(int slot, ItemLink link) {
        if (link == null)
            return remove(slot);
        if (items[slot] != null)
            return false;
        items[slot] = link;
        size++;
        if (nextFree == slot)
            findNextFree();
        return true;
    }

    /**
     * Removes any link matching the predicate.
     *
     * @param predicate the predicate that decides which item to remove
     * @return true if the hotbar changed
     */
    public boolean remove(Predicate<ItemLink> predicate) {
        int initialSize = size;
        for (int i = 0; i < 9; i++) {
            if (predicate.test(items[i])) {
                if (items[i] == null)
                    size--;
                items[i] = null;
            }
        }
        return size != initialSize;
    }

    /**
     * Removes any link that is contained in the collection.
     *
     * @param links the collection with the items to remove
     */
    public void remove(Collection<ItemLink> links) {
        remove(links::contains);
    }

    /**
     * Removes any link contained in the array.
     *
     * @param links the array containing the items to remove
     */
    public void remove(ItemLink[] links) {
        remove(Arrays.asList(links));
    }

    /**
     * Removes the link in that slot (if any is present).
     *
     * @param slot the slot with the link to remove
     * @return true only if any items were in that slot
     */
    public boolean remove(int slot) {
        if (items[slot] == null)
            return false;
        items[slot] = null;
        size--;
        return true;
    }

    /**
     * Removes all the items.
     */
    public void clearLinks() {
        for (int slot = 0; slot < items.length; slot++)
            items[slot] = null;
        size = 0;
    }

    public void addItem(ItemStack item) {
        addLink(new ItemLink(item));
    }

    /**
     * Adds all the items present in the collection, throws an exception if there isn't enough space.
     *
     * @param links the collection with the items to add
     * @throws HotbarOutOfSpaceException if toAdd.size &gt; free()
     */
    public void addLinks(Collection<ItemLink> links) {
        if (links.size() > (9 - size))
            throw new HotbarOutOfSpaceException(this, links.size());

        for (ItemLink link : links) {
            this.items[nextFree] = link;
            size++;
            findNextFree();
        }
    }

    public void addLinks(ItemLink[] links) {
        addLinks(Arrays.asList(links));
    }

    public boolean addLink(ItemStack item, Link link) {
        return addLink(new ItemLink(item, link));
    }

    public boolean addLink(ItemLink link) {
        if (isFull())
            return false;
        items[nextFree] = link;
        findNextFree();
        return true;
    }

    /**
     * Returns true only if all the items in the collection are present
     *
     * @param links the collection with the items to check
     * @return true only if the collection is contained in the items
     */
    public boolean contains(Collection<ItemLink> links) {
        return unmodifiableView.containsAll(links);
    }

    /**
     * Returns true only if all the items in the array are present
     *
     * @param links the array with the items to check
     * @return true only if the array is contained in the items
     */
    public boolean contains(ItemLink[] links) {
        return contains(Arrays.asList(links));
    }

    /**
     * Returns true only if the link is present
     *
     * @param link the link to check
     * @return true only if the link is present
     */
    public boolean contains(ItemLink link) {
        if (link == null) return !isFull();
        return stream().anyMatch(link::equals);
    }

    /**
     * Returns true only if a link is stored in that slot
     *
     * @param slot the slot with the link to check
     * @return true only if the slot passed as argument contains a link
     */
    public boolean isSlotLink(int slot) {
        return items[slot] != null;
    }

    /**
     * Returns a stream of the items contained in the hotbar.
     *
     * @return a stream of the items contained in the hotbar
     */
    public Stream<ItemLink> stream() {
        return unmodifiableView.stream().filter(Objects::nonNull);
    }

    public void onGive() {
    }

    public void onClick(InventoryClickEvent e) {
        ItemLink link = items[e.getSlot()];
        if (link != null)
            link.onClick(e);
    }

    public void onRemove() {
    }

    /**
     * Prints the hotbar to the given player.
     *
     * @param player the player that receives the hotbar
     * @return true if the player received the hotbar, otherwise false
     */
    public boolean give(Player player) {
        if (!player.hasPermission(permission))
            return false;
        Inventory inv = player.getInventory();
        for (int i = 0; i < 9; i++)
            if (items[i] != null)
                inv.setItem(i, items[i].getDisplay().toItemStack(player));
        player.openInventory(inv);
        return true;
    }

    public void remove(Player player) {
        for (int slot = 0; slot < 9; slot++) {
            if (items[slot] != null)
                player.getInventory().setItem(slot, null);
        }
        player.updateInventory();
    }

    /**
     * Deserializes the hotbar by the given id and the given config.
     *
     * @param id     the id of the hotbar deserialized
     * @param config the config where load the hotbar
     * @return the hotbar created
     */
    public static Hotbar deserialize(String id, Config config) {
        Hotbar hotbar = new Hotbar(id);
        hotbar.permission = (String) config.get("permission");
        for (Config section : config.getConfigList("items")) {
            ItemLink item = ItemLink.deserialize(section);
            int slot = section.getInt("slot", -1);
            if (slot == -1)
                hotbar.addLink(item);
            else
                hotbar.setLink(slot, item);
        }
        hotbar.onJoin = config.getBool("on-join", false);
        return hotbar;
    }

    public static class HotbarOutOfSpaceException extends RuntimeException {
        @Getter
        private final Hotbar hotbar;
        @Getter
        private final int toAdd;

        public HotbarOutOfSpaceException(Hotbar hotbar, int toAdd) {
            super("Error adding items to hotbar: trying to add " + toAdd + " but only " + hotbar.getFree() + " empty!");
            this.hotbar = hotbar;
            this.toAdd = toAdd;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Hotbar hotbar;

        public Builder() {
            hotbar = new Hotbar();
        }

        public Builder(Hotbar hotbar) {
            this.hotbar = hotbar;
        }

        public Builder id(String id) {
            hotbar.setId(id);
            return this;
        }

        public Builder permission(String permission) {
            hotbar.setPermission(permission);
            return this;
        }

        public Builder onJoin(boolean onJoin) {
            hotbar.setOnJoin(onJoin);
            return this;
        }

        public Builder add(ItemLink link) {
            hotbar.addLink(link);
            return this;
        }

        public Builder add(ItemStack item, Link link) {
            hotbar.addLink(item, link);
            return this;
        }

        public Builder add(ItemLink... links) {
            hotbar.addLinks(links);
            return this;
        }

        public Builder add(Collection<ItemLink> links) {
            hotbar.addLinks(links);
            return this;
        }

        public Builder set(int slot, ItemLink link) {
            hotbar.setLink(slot, link);
            return this;
        }

        public Hotbar build() {
            return hotbar;
        }
    }
}
