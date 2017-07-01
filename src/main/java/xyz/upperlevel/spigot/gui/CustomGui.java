package xyz.upperlevel.spigot.gui;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.config.InvalidGuiConfigurationException;
import xyz.upperlevel.spigot.gui.config.UpdaterTask;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;
import xyz.upperlevel.spigot.gui.config.util.Config;
import xyz.upperlevel.spigot.gui.link.Link;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class CustomGui implements Gui {
    private final Map<Player, UpdaterTask> updaters = new HashMap<>();

    private String id;

    private PlaceholderValue<String> title;
    private int size;
    private InventoryType type;
    private ItemLink[] items;

    public CustomGui() {
        this(54, "");
    }

    public CustomGui(int size, String title) {
        this(null, size, title);
    }

    public CustomGui(int size) {
        this(null, size);
    }

    public CustomGui(InventoryType type) {
        this(null, type);
    }

    private int updateInterval = -1;

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

    /**
     * Initializes the gui by its size and title. The id can be set to null if not needed.
     *
     * @param id    a unique id
     * @param size  the size of the gui
     * @param title the title of the gui
     */
    public CustomGui(String id, int size, String title) {
        this.id = id;
        this.title = PlaceholderValue.strValue(title);
        this.size = size;

        items = new ItemLink[size];

        onSetup();
    }

    /**
     * Initializes the gui by its type and title. The id can be set to null if not needed.
     *
     * @param id    a unique id
     * @param type  the type of the gui
     * @param title the title of the gui
     */
    public CustomGui(String id, InventoryType type, String title) {
        this.id = id;
        this.type = type;
        this.title = PlaceholderValue.strValue(title);

        items = new ItemLink[type.getDefaultSize()];

        onSetup();
    }

    public boolean hasId() {
        return id != null;
    }

    public void onSetup() {
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
    public ItemLink getItem(int slot) {
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

    public boolean addLink(ItemStack item, Link link) {
        return addItem(new ItemLink(item, link));
    }

    /**
     * Adds an item in the first slot empty.
     *
     * @param item the item to addLinks
     */
    public boolean addItem(ItemStack item) {
        return addItem(new ItemLink(item));
    }

    public boolean addItem(ItemLink item) {
        int i = firstEmpty();
        if (i >= 0) {
            items[i] = item;
            return true;
        }
        return false;
    }

    /**
     * Adds the given links.
     *
     * @param items the links to addLinks
     * @return true if all links have been added, otherwise false
     */
    public boolean addItems(ItemStack... items) {
        for (ItemStack item : items)
            if (!addItem(item))
                return false;
        return true;
    }

    /**
     * Adds the given links.
     *
     * @param items
     * @return
     */
    public boolean addItems(ItemLink... items) {
        for (ItemLink item : items)
            if (!addItem(item))
                return false;
        return true;
    }

    private void setItem(int slot, ItemStack item, Link link) {
        setItem(slot, new ItemLink(item, link));
    }

    public void setItem(int slot, ItemStack item) {
        setItem(slot, new ItemLink(item));
    }

    /**
     * Sets the given item at the given slot.
     *
     * @param slot the slot where to give the item
     * @param item the item to give
     */
    public void setItem(int slot, ItemLink item) {
        items[slot] = item;
    }

    public void setItem(int[] slots, ItemStack item) {
        setItem(slots, new ItemLink(item));
    }

    public void setItem(int[] slots, ItemLink item) {
        for (int slot : slots)
            items[slot] = item;
    }

    public void setUpdateInterval(int updateInterval) {//TODO: Optimize for the first run
        if (this.updateInterval != updateInterval) {
            if (updateInterval > 0) {//If this interval is valid
                if (updaters.size() > 0) {//And there are some players that are updating
                    updaters.replaceAll((player, old) -> {
                        old.stop();
                        UpdaterTask task = new UpdaterTask(updateInterval, () -> onUpdate(player));
                        task.start();
                        return task;
                    });
                } else if (this.updateInterval < 0) {//If there isn't any updater because the old updateInterval wasn't valid
                    GuiManager.getChronology().entrySet()
                            .stream()
                            .filter(e -> e.getValue().peek() == this)
                            .forEach((e) -> startUpdateTask(e.getKey()));
                }
            } else {
                updaters.forEach((p, t) -> t.stop());
                updaters.clear();
            }
        }
    }

    /**
     * Gets a list of links non null.
     *
     * @return links not null
     */
    public List<ItemLink> getItems() {
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
        if (updateInterval > 0)
            startUpdateTask(player);
    }

    protected void startUpdateTask(Player player) {
        UpdaterTask task = new UpdaterTask(updateInterval, () -> onUpdate(player));
        updaters.put(player, task);
        task.start();
    }

    protected void onUpdate(Player player) {
        GuiManager.reprint(player);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemLink item = items[event.getSlot()];
        if (item != null)
            item.onClick(event);
    }

    @Override
    public void onClose(Player player) {
        UpdaterTask task = updaters.remove(player);
        if (task != null)
            task.stop();
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
                inv.setItem(slot, items[slot].getDisplay().toItemStack(player));

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
                res.items = new ItemLink[res.type.getDefaultSize()];
            } else if (config.has("size")) {

                res.type = null;
                res.size = config.getInt("size");
                if (res.size % 9 != 0) {
                    SlimyGuis.logger().warning("In gui " + id + ": size must be a multiple of 9");
                    res.size = GuiSize.min(res.size);
                }
                res.items = new ItemLink[res.size];
            } else
                throw new InvalidGuiConfigurationException("Both 'type' and 'size' are empty!");

            res.updateInterval = config.getInt("update-interval", -1);

            res.title = config.getMessageRequired("title");

            for (Map<String, Object> data : (Collection<Map<String, Object>>) config.getCollection("items")) {
                ItemLink item = ItemLink.deserialize(Config.wrap(data));
                res.items[(int) data.get("slot")] = item;
            }

            return res;
        } catch (InvalidGuiConfigurationException e) {
            e.addLocalizer("in gui " + id);
            throw e;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final CustomGui gui;

        public Builder() {
            gui = new CustomGui();
        }

        public Builder(CustomGui gui) {
            this.gui = gui;
        }

        public Builder id(String id) {
            gui.setId(id);
            return this;
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

        public Builder add(ItemStack item, Link link) {
            gui.addLink(item, link);
            return this;
        }

        public Builder add(ItemLink link) {
            gui.addItem(link);
            return this;
        }

        public Builder addAll(ItemStack... items) {
            gui.addItems(items);
            return this;
        }

        public Builder addAll(ItemLink... items) {
            gui.addItems(items);
            return this;
        }

        public Builder set(int slot, ItemStack item, Link link) {
            gui.setItem(slot, item, link);
            return this;
        }

        public Builder set(int slot, ItemStack item) {
            gui.setItem(slot, item);
            return this;
        }

        public Builder set(int slot, ItemLink item) {
            gui.setItem(slot, item);
            return this;
        }

        public Builder set(int[] slots, ItemStack item) {
            gui.setItem(slots, item);
            return this;
        }

        public Builder set(int[] slots, ItemLink item) {
            gui.setItem(slots, item);
            return this;
        }

        public Builder updateInterval(int interval) {
            gui.updateInterval = interval;
            return this;
        }

        public CustomGui build() {
            return gui;
        }
    }

}
