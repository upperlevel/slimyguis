package xyz.upperlevel.spigot.gui;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.config.ConfigItem;
import xyz.upperlevel.spigot.gui.config.InvalidGuiConfigurationException;
import xyz.upperlevel.spigot.gui.config.itemstack.CustomItem;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;
import xyz.upperlevel.spigot.gui.config.util.Config;

import java.util.*;
import java.util.stream.Collectors;

import static lombok.AccessLevel.NONE;

@Data
public class CustomGui implements Gui {

    private final String id;

    private PlaceholderValue<String> title;
    private int size;
    private InventoryType type;

    @Getter(NONE)
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

    /**
     * Gets the item at the given slot.
     *
     * @param slot the slot to get the item in
     */
    public GuiItem getItem(int slot) {
        return items[slot];
    }

    /**
     * Adds an item in the first slot empty.
     *
     * @param item the item to add
     */
    public boolean addItem(GuiItem item) {
        for (int i = 0; i < size; i++) {
            if (items[i] == null) {
                items[i] = item;
                return true;
            }
        }
        return false;
    }

    public void setItem(int slot, ItemStack item) {
        setItem(slot, new GuiItem(slot, item, null));
    }

    /**
     * Sets the given item at the given slot.
     *
     * @param slot the slot to set the item in
     * @param item the item to set
     */
    public void setItem(int slot, GuiItem item) {
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
    public void print(Player player) {
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

        for (GuiItem item : getItems()) {
            inv.setItem(item.getSlot(), item.getItem().toItemStack(player));
        }

        return inv;
    }

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
                res.items[item.getSlot()] = item;
            }

            return res;
        } catch (InvalidGuiConfigurationException e) {
            e.addLocalizer("in gui " + id);
            throw e;
        }
    }
}
