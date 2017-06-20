package xyz.upperlevel.spigot.gui.config;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.commands.CommandUtil;
import xyz.upperlevel.spigot.gui.config.ConfigItem.ItemClick;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

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
    private ConfigItem[] slotMapped;
    @Getter
    private int size;

    public ConfigGui(String id, ConfigItem[] items, InventoryType type, int size, PlaceholderValue<String> title) {
        this.id = id;
        this.items = items;
        this.type = type;
        this.title = title;

        this.size = type != null ? type.getDefaultSize() : size;
        this.slotMapped = new ConfigItem[size];
        for(ConfigItem item : items)
            for(int slot : item.getSlots())
                slotMapped[slot] = item;
    }


    @SuppressWarnings("unchecked")
    public static ConfigGui deserialize(Map<String, Object> config, String id) {
        ConfigItem[] items = ((Collection<Map<String, Object>>)config.get("items"))
                .stream()
                .map(ConfigItem::deserialize)
                .toArray(ConfigItem[]::new);

        List<String> commands;
        if(config.containsKey("openCommands")) {
            commands = (List<String>) config.get("openCommands");
        } else if(config.containsKey("openCommand")) {
            commands = Collections.singletonList((String) config.get("openCommand"));
        } else commands = Collections.emptyList();

        InventoryType type;
        int size;

        if(config.containsKey("type")) {
            type = InventoryType.valueOf(((String)config.get("type")).toUpperCase());
            size = -1;
        } else if(config.containsKey("size")) {
            type = null;
            size = ((Number)config.get("size")).intValue();
        } else {
            Bukkit.getLogger().severe("Error in gui " + id + ": both \"type\" and \"size\" are empty!");
            return null;
        }
        PlaceholderValue<String> title = MessageUtil.process((String) config.get("title"));
        if(title == null) {
            Bukkit.getLogger().severe("Error in gui " + id + ": the title cannot be empty!");
            return null;
        }

        ConfigGui gui = new ConfigGui(id, items, type, size, title);

        CommandUtil.register(new OpenGuiCommand(commands, gui));

        return gui;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ConfigItem item = slotMapped[event.getSlot()];
        if(item != null) {
            ItemClick clickHandler = item.getClick();
            if(clickHandler != null)
                clickHandler.onClick((Player) event.getWhoClicked());
        }
    }

    @Override
    public void print(Player player) {
        Inventory inv;
        if(type != null)
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
    public void onOpen(Player player) {}

    @Override
    public void onClose(Player player) {}
}
