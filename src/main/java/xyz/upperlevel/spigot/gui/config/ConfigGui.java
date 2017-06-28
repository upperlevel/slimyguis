package xyz.upperlevel.spigot.gui.config;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.GuiSize;
import xyz.upperlevel.spigot.gui.SlimyGuis;
import xyz.upperlevel.spigot.gui.commands.CommandUtil;
import xyz.upperlevel.spigot.gui.config.ConfigItem.ItemClick;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;
import xyz.upperlevel.spigot.gui.config.util.Config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class ConfigGui implements Gui {//TODO better error handling

    @Getter
    private final String id;
    private ConfigItem[] items;

    @Getter
    private InventoryType type;

    @Getter
    private PlaceholderValue<String> title;

    @Getter
    private int size;

    public ConfigGui(String id, ConfigItem[] items, InventoryType type, int size, PlaceholderValue<String> title) {
        this.id = id;
        this.type = type;
        this.title = title;

        this.size = type != null ? type.getDefaultSize() : size;
        this.items = new ConfigItem[size];
        for (ConfigItem item : items)
            for (int slot : item.getSlots())
                this.items[slot] = item;
    }

    @SuppressWarnings("unchecked")
    public static ConfigGui deserialize(Config config, String id) {
        try {
            ConfigItem[] items;
            items = ((Collection<Map<String, Object>>) config.getCollection("items"))
                    .stream()
                    .map(c -> ConfigItem.deserialize(Config.wrap(c)))
                    .toArray(ConfigItem[]::new);

            List<String> commands;
            if (config.has("openCommands")) {
                commands = (List<String>) config.getCollection("openCommands");
            } else if (config.has("openCommand")) {
                commands = Collections.singletonList(config.getString("openCommand"));
            } else commands = Collections.emptyList();

            InventoryType type;
            int size;

            if (config.has("type")) {
                type = config.getEnum("type", InventoryType.class);
                size = -1;
            } else if (config.has("size")) {
                type = null;
                size = config.getInt("size");
                if (size % 9 != 0) {
                    SlimyGuis.logger().warning("In gui " + id + ": size must be a multiple of 9");
                    size = GuiSize.min(size);
                }
            } else {
                throw new InvalidGuiConfigurationException("both both \"type\" and \"size\" are empty!");
            }
            PlaceholderValue<String> title = config.getMessageRequired("title");

            ConfigGui gui = new ConfigGui(id, items, type, size, title);

            CommandUtil.register(new OpenGuiCommand(commands, gui));

            return gui;
        } catch (InvalidGuiConfigurationException e) {
            e.addLocalizer("in gui " + id);
            throw e;
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ConfigItem item = items[event.getSlot()];
        if (item != null) {
            ItemClick clickHandler = item.getClick();
            if (clickHandler != null)
                clickHandler.onClick((Player) event.getWhoClicked());
        }
    }

    @Override
    public void print(Player player) {
        Inventory inv;
        if (type != null)
            inv = Bukkit.createInventory(player, type, title.get(player));
        else
            inv = Bukkit.createInventory(player, size, title.get(player));
        ItemStack[] contents = inv.getContents();
        for (ConfigItem item : items) {
            final ItemStack printed = item.getItem().toItemStack(player);
            for (int slot : item.getSlots())
                contents[slot] = printed;
        }
        inv.setContents(contents);
        player.openInventory(inv);
    }

    @Override
    public void onOpen(Player player) {
    }

    @Override
    public void onClose(Player player) {
    }
}
