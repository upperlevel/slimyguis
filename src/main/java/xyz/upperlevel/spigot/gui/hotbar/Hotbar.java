package xyz.upperlevel.spigot.gui.hotbar;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class Hotbar implements Iterable<HotbarLink>{
    private final Player player;
    private HotbarLink[] links = new HotbarLink[9];
    private List<HotbarLink> unmodifiableView = Collections.unmodifiableList(Arrays.asList(links));
    private int nextFree = 0;
    private int size = 0;

    /**
     * Reprints all the links without calling player.updateInventory()
     */
    public void reprint() {
        Inventory inv = player.getInventory();
        for(int i = 0; i < 9; i++)
            if(links[i] != null)
                inv.setItem(i, links[i].getDisplay());
    }

    /**
     * Reprints the link in the slot passed as argument (if it's present) without calling player.updateInventory()
     * @param slot the slot to reprint
     */
    public void reprint(int slot) {
        if(links[slot] != null)
            player.getInventory().setItem(slot, links[slot].getDisplay());
    }

    /**
     * Returns the link present at that slot
     * @param slot the link's slot
     * @return the link present at that slot
     */
    public HotbarLink getLink(int slot) {
        return links[slot];
    }

    /**
     * Returns an unmodifiable list of links
     * @return the player's links
     */
    public List<HotbarLink> getLinks() {
        return unmodifiableView;
    }

    /**
     * @return true only if the player has one or more links
     */
    public boolean isEmpty() {
        return nextFree == 0;
    }

    /**
     * @return true only if the hotbar is full
     */
    public boolean isFull() {
        return nextFree == -1;
    }

    /**
     * returns the hotbar size
     * @return the number of links in the hotbar
     */
    public int size() {
        return size;
    }

    /**
     * @return the number of slots available
     */
    public int free() {
        return 9 - size;
    }

    private void findNextFree() {
        do {
            if(++nextFree >= 9) {
                nextFree = -1;
                break;
            }
        } while (links[nextFree] != null);
    }

    /**
     * Sets the link passed as parameter in the specified slot, if empty
     * @param link the link to set
     * @param slot the slot in which the link will go
     */
    public void setLink(HotbarLink link, int slot) {
        if(link == null) {
            remove(slot);
            return;
        }

        if(links[slot] != null)
            throw new IllegalArgumentException("Slot already full");
        links[slot] = link;
        reprint(slot);
        size++;
        if(nextFree == slot)
            findNextFree();
    }

    /**
     * Removes any link matching the predicate
     * @param predicate the predicate that decides which item to remove
     */
    public void remove(Predicate<HotbarLink> predicate) {
        final Inventory inv = player.getInventory();
        for(int i = 0; i < 9; i++) {
            if(predicate.test(links[i])) {
                links[i] = null;
                size--;
                inv.setItem(i, null);
            }
        }
    }

    /**
     * Removes any link that is contained in the collection
     * @param links the collection with the links to remove
     */
    public void remove(Collection<HotbarLink> links) {
        remove(links::contains);
    }

    /**
     * Removes any link contained in the array
     * @param links the array containing the links to remove
     */
    public void remove(HotbarLink[] links) {
        remove(Arrays.asList(links));
    }

    /**
     * Removes the link in that slot (if any is present)
     * @param slot the slot with the link to remove
     * @return true only if any links were in that slot
     */
    public boolean remove(int slot) {
        if(links[slot] == null)
            return false;
        links[slot] = null;
        player.getInventory().setItem(slot, null);
        size--;
        if(nextFree == -1 || nextFree > slot)
            nextFree = slot;
        return true;
    }

    /**
     * Removes all the links
     */
    public void clear() {
        final Inventory inv = player.getInventory();
        for(int i = 0; i < 9; i++) {
            links[i] = null;
            inv.setItem(i, null);
        }
        size = 0;
    }

    /**
     * Adds all the links present in the collection, throws an exception if there isn't enough space
     * @param toAdd the collection with the links to add
     * @throws HotbarOutOfSpaceException if toAdd.size &gt; free()
     */
    public void add(Collection<HotbarLink> toAdd) {
        if(toAdd.size() > (9 - size))
            throw new HotbarOutOfSpaceException(this, toAdd.size());
        final Inventory inv = player.getInventory();

        for (HotbarLink l : toAdd) {
            links[nextFree] = l;
            size++;
            inv.setItem(nextFree, l.getDisplay());
            findNextFree();
        }
    }

    /**
     * Adds all the links present in the collection, throws an exception if there isn't enough space
     * @param links the collection with the links to add
     * @throws HotbarOutOfSpaceException if links.length &gt; free()
     */
    public void add(HotbarLink[] links) {
        add(Arrays.asList(links));
    }

    /**
     * Adds the link in the first free space or throws an exception if the hotbar is full
     * @param toAdd the link to add
     * @throws HotbarOutOfSpaceException if isFull()
     */
    public void add(HotbarLink toAdd) {
        if(isFull())
            throw new HotbarOutOfSpaceException(this, 1);
        links[nextFree] = toAdd;
        size++;
        player.getInventory().setItem(nextFree, toAdd.getDisplay());
        findNextFree();
    }

    /**
     * Returns true only if all the items in the collection are present
     * @param link the collection with the links to check
     * @return true only if the collection is contained in the links
     */
    public boolean contains(Collection<HotbarLink> link) {
        return unmodifiableView.containsAll(link);
    }

    /**
     * Returns true only if all the items in the array are present
     * @param link the array with the links to check
     * @return true only if the array is contained in the links
     */
    public boolean contains(HotbarLink[] link) {
        return contains(Arrays.asList(link));
    }

    /**
     * Returns true only if the link is present
     * @param link the link to check
     * @return true only if the link is present
     */
    public boolean contains(HotbarLink link) {
        if(link == null) return !isFull();
        return stream().anyMatch(link::equals);
    }

    /**
     * Returns true only if a link is stored in that slot
     * @param slot the slot with the link to check
     * @return true only if the slot passed as argument contains a link
     */
    public boolean isSlotLink(int slot) {
        return links[slot] != null;
    }

    /**
     * Returns true if any of the items passed as argument is the display of a link
     * @param items the items to check
     * @return true if any of the items passed as argument is the display of a link
     */
    public boolean anyItemLink(ItemStack... items) {
        return anyItemLink(Arrays.asList(items));
    }

    /**
     * Returns true if any of the items contained in the collection is the display of a link
     * @param items the items to check
     * @return true if any of the items contained in the collection is the display of a link
     */
    public boolean anyItemLink(Collection<ItemStack> items) {
        return linkStream().anyMatch(items::contains);
    }

    /**
     * Returns a stream of the displays of the links of the hotbar
     * @return a stream of the displays of the links of the hotbar
     */
    public Stream<ItemStack> linkStream() {
        return stream().map(HotbarLink::getDisplay);
    }

    /**
     * Returns a stream of the links contained in the hotbar
     * @return a stream of the links contained in the hotbar
     */
    public Stream<HotbarLink> stream() {
        return unmodifiableView.stream().filter(Objects::nonNull);
    }

    @Override
    public Iterator<HotbarLink> iterator() {
        return new HotbarIterator();
    }

    public class HotbarIterator implements Iterator<HotbarLink> {
        private int old = -1;
        private int i = -1;

        public HotbarIterator() {
            findNext();
        }

        private void findNext() {
            do {
                if(++i > 9) {
                    i = -1;
                    break;
                }
            } while (links[i] == null);
        }

        @Override
        public boolean hasNext() {
            return i > 0;
        }

        @Override
        public HotbarLink next() {
            if(!hasNext())
                throw new NoSuchElementException();
            old = i;
            findNext();
            return links[old];
        }

        @Override
        public void remove() {
            if(old < 0)
                throw new NoSuchElementException();
            Hotbar.this.remove(old);
        }
    }

    public static class HotbarOutOfSpaceException extends RuntimeException {
        @Getter
        private final Hotbar hotbar;
        @Getter
        private final int toAdd;

        public HotbarOutOfSpaceException(Hotbar hotbar, int toAdd) {
            super("Error adding links to hotbar: trying to add " + toAdd + " but only " + hotbar.free() + " empty!");
            this.hotbar = hotbar;
            this.toAdd = toAdd;
        }
    }

}
