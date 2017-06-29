package xyz.upperlevel.spigot.gui;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.config.InvalidGuiConfigurationException;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;
import xyz.upperlevel.spigot.gui.config.util.Config;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class CustomGui implements Gui {

    private final String id;

    private PlaceholderValue<String> title;
    private int size;
    private InventoryType type;
    private GuiItem[] items;

    // used just when deserialize
    private CustomGui(String id) {
        this.id = id;
    }

    public CustomGui(String id, InventoryType type) {
        this(id, type, type.getDefaultTitle());
    }

    public CustomGui(String id, int size) {
        this(id, size, InventoryType.CHEST.getDefaultTitle());
    }

    public CustomGui(String id, int size, String title) {
        this.id = id;
        this.title = PlaceholderValue.strValue(title);
        this.size = size;

        items = new GuiItem[size];
    }

    public CustomGui(String id, InventoryType type, String title) {
        this.id = id;
        this.type = type;
        this.title = PlaceholderValue.strValue(title);

        items = new GuiItem[type.getDefaultSize()];
    }

    public void setTitle(String title) {
        this.title = PlaceholderValue.strValue(title);
    }

    public void setTitle(PlaceholderValue<String> title) {
        this.title = title;
    }

    /**
     * Gets the item at the given slot.
     *
     * @param slot the slot to get the item in
     */
    public GuiItem getItem(int slot) {
        return items[slot];
    }

    /**
     * Returns first slot empty.
     */
    public int firstEmpty() {
        for (int i = 0; i < size; i++)
            if (items[i] == null)
                return i;
        return -1;
    }

    /**
     * Adds an item in the first slot empty.
     *
     * @param item the item to add
     */
    public boolean addItem(ItemStack item) {
        return addItem(new GuiItem(item));
    }

    public boolean addItem(GuiItem item) {
        int i = firstEmpty();
        if (i >= 0) {
            items[i] = item;
            return true;
        }
        return false;
    }

    /**
     * Adds the given items.
     *
     * @param items the items to add
     * @return true if all items have been added, otherwise false
     */
    public boolean addItems(ItemStack... items) {
        for (ItemStack item : items)
            if (!addItem(item))
                return false;
        return true;
    }

    /**
     * Adds the given items.
     *
     * @param items
     * @return
     */
    public boolean addItems(GuiItem... items) {
        for (GuiItem item : items)
            if (!addItem(item))
                return false;
        return true;
    }

    public void setItem(int slot, ItemStack item) {
        setItem(slot, new GuiItem(item));
    }

    /**
     * Sets the given item at the given slot.
     *
     * @param slot the slot where to set the item
     * @param item the item to set
     */
    public void setItem(int slot, GuiItem item) {
        items[slot] = item;
    }

    public void setItem(int[] slots, ItemStack item) {
        setItem(slots, new GuiItem(item));
    }

    public void setItem(int[] slots, GuiItem item) {
        for (int slot : slots)
            items[slot] = item;
    }

    /**
     * Gets a list of items non null.
     *
     * @return items not null
     */
    public List<GuiItem> getItems() {
        return Arrays.stream(items)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void show(Player player) {
        player.openInventory(create(player));
    }

    @Override
    public void onOpen(Player player) {
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        GuiItem item = items[event.getSlot()];
        if (item != null)
            item.onClick(event);
    }

    @Override
    public void onClose(Player player) {
    }

    /**
     * Creates the inventory based on the player who's opening it.
     *
     * @param player the opener
     * @return the inventory created
     */
    public Inventory create(Player player) {
        Inventory inv;
        if (type != null)
            inv = Bukkit.createInventory(null, type, title.get(player));
        else
            inv = Bukkit.createInventory(null, size, title.get(player));

        for (int slot = 0; slot < items.length; slot++)
            if (items[slot] != null)
                inv.setItem(slot, items[slot].getItem().toItemStack(player));

        return inv;
    }

    /**
     * Loads a gui from id and configuration section.
     *
     * @param id     the id of the gui
     * @param config the config where to load the gui
     * @return the gui created
     */
    @SuppressWarnings("unchecked")
    public static CustomGui deserialize(String id, Config config) {
        try {
            CustomGui res = new CustomGui(id);

            if (config.has("type")) {

                res.type = config.getEnum("type", InventoryType.class);
                res.size = -1;
                res.items = new GuiItem[res.type.getDefaultSize()];
            } else if (config.has("size")) {

                res.type = null;
                res.size = config.getInt("size");
                if (res.size % 9 != 0) {
                    SlimyGuis.logger().warning("In gui " + id + ": size must be a multiple of 9");
                    res.size = GuiSize.min(res.size);
                }
                res.items = new GuiItem[res.size];
            } else
                throw new InvalidGuiConfigurationException("Both 'type' and 'size' are empty!");

            res.title = config.getMessageRequired("title");

            for (Map<String, Object> data : (Collection<Map<String, Object>>) config.getCollection("items")) {
                GuiItem item = GuiItem.deserialize(Config.wrap(data));
                res.items[(int) data.get("slot")] = item;
            }

            return res;
        } catch (InvalidGuiConfigurationException e) {
            e.addLocalizer("in gui " + id);
            throw e;
        }
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {

        private final CustomGui gui;

        public Builder(String id) {
            gui = new CustomGui(id);
        }

        public Builder(CustomGui gui) {
            this.gui = gui;
        }

        public Builder type(InventoryType type) {
            gui.type = type;
            return this;
        }

        public Builder size(int size) {
            gui.size = size;
            return this;
        }

        public Builder title(String title) {
            gui.setTitle(title);
            return this;
        }

        public Builder add(ItemStack item) {
            gui.addItem(item);
            return this;
        }

        public Builder add(GuiItem item) {
            gui.addItem(item);
            return this;
        }

        public Builder addAll(ItemStack... items) {
            gui.addItems(items);
            return this;
        }

        public Builder addAll(GuiItem... items) {
            gui.addItems(items);
            return this;
        }

        public Builder set(int slot, ItemStack item) {
            gui.setItem(slot, item);
            return this;
        }

        public Builder set(int slot, GuiItem item) {
            gui.setItem(slot, item);
            return this;
        }

        public Builder set(int[] slots, ItemStack item) {
            gui.setItem(slots, item);
            return this;
        }

        public Builder set(int[] slots, GuiItem item) {
            gui.setItem(slots, item);
            return this;
        }

        public CustomGui build() {
            return gui;
        }
    }
}
