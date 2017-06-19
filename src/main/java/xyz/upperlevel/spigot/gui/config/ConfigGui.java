package xyz.upperlevel.spigot.gui.config;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.config.itemstack.CustomItem;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.Collection;
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
            for(int slot : item.slots)
                slotMapped[slot] = item;
    }


    public static ConfigGui deserialize(Map<String, Object> config) {
        String id;

        Object rawId = config.get("id");
        if(rawId == null) {
            Bukkit.getLogger().severe("Error: every Gui must have an id!");
            return null;
        }
        id = rawId.toString();


        ConfigItem[] items = (ConfigItem[]) ((Collection<Map<String, Object>>)config.get("items"))
                .stream()
                .map(ConfigItem::deserialize)
                .toArray();

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
        PlaceholderValue<String> title = PlaceholderValue.strValue((String) config.get("title"));
        if(title == null) {
            Bukkit.getLogger().severe("Error in gui " + id + ": the title cannot be empty!");
            return null;
        }

        return new ConfigGui(id, items, type, size, title);
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
            final ItemStack printed = item.item.toItemStack(player);
            for (int slot : item.slots)
                contents[slot] = printed;
        }
        inv.setContents(contents);
    }

    @Override
    public void onOpen(Player player) {}

    @Override
    public void onClose(Player player) {}

    public static class ConfigItem {
        public int slots[];
        @Getter
        private CustomItem item;
        @Getter
        private ItemClick click;

        @SuppressWarnings("unchecked")
        public static ConfigItem deserialize(Map<String, Object> config) {
            ConfigItem res = new ConfigItem();
            if(config.containsKey("slots"))
                res.slots = ((List<Integer>)config.get("slots")).stream().mapToInt(Integer::intValue).toArray();
            else if(config.containsKey("slot"))
                res.slots = new int[] { (int) config.getOrDefault("slot", -1) };
            else
                throw new IllegalArgumentException("No slot specified");
            if(config.containsKey("item"))
                res.item = CustomItem.deserialzie((Map<String, Object>) config.get("item"));
            if(config.containsKey("click"))
                res.click = ItemClick.deserialize((Map<String, Object>) config.get("click"));

            return res;
        }
    }

    public static class ItemClick {
        private String permission;
        private PlaceholderValue<String> noPermissionError;
        private Sound noPermissionSound;

        private int cost;
        private String economy;
        private String noMoneyError;
        private Sound noMoneySound;

        private Action[] actions;

        public boolean checkPermission(Player player) {
            if(!player.hasPermission(permission)) {
                player.sendMessage(noPermissionError.get(player));
                player.playSound(player.getLocation(), noPermissionSound, 1.0f, 1.0f);
                return false;
            } else return true;
        }

        public boolean pay(Player player) {
            if(cost > 0)
                throw new NotImplementedException();
            return true;
        }

        public void onClick(Player player) {
            if(checkPermission(player) && pay(player)) {
                for(Action action : actions)
                    action.run(player);
            }
        }


        public static ItemClick deserialize(Map<String, Object> config) {
            ItemClick res = new ItemClick();
            res.permission = (String) config.get("permission");
            res.noPermissionError = MessageUtil.process((String) config.getOrDefault("noPermissionMessage", "You don't have permission!"));
            if(config.containsKey("noPermissionSound"))
                res.noPermissionSound = Sound.valueOf(((String)config.getOrDefault("noPermissionSound", "")).toUpperCase());

            res.cost = (int) config.getOrDefault("cost", 0);
            res.economy = (String) config.getOrDefault("economy", "");
            res.noMoneyError = (String) config.getOrDefault("noMoneyError", "You don't have enough money");
            if(config.containsKey("noMoneySound"))
                res.noMoneySound = Sound.valueOf(((String)config.get("noMoneySound")).toUpperCase());

            List<Map<String, Object>> actions = (List<Map<String, Object>>) config.getOrDefault("actions", null);
            if(actions == null)
                res.actions = null;
            else
                res.actions = actions.stream().map(ActionType::deserialize).toArray(Action[]::new);
            return res;
        }
    }
}
