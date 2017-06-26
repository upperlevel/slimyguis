package xyz.upperlevel.spigot.gui.config;

import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import xyz.upperlevel.spigot.gui.config.itemstack.CustomItem;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;
import xyz.upperlevel.spigot.gui.config.util.Config;
import xyz.upperlevel.spigot.gui.link.Link;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ConfigItem {
    @Getter
    private int slots[];
    @Getter
    private CustomItem item;
    @Getter
    private ItemClick click;

    @SuppressWarnings("unchecked")
    public static ConfigItem deserialize(Config config) {
        ConfigItem res = new ConfigItem();
        try {
            if (config.has("slots"))
                res.slots = ((List<Integer>) config.get("slots")).stream().mapToInt(Integer::intValue).toArray();
            else
                res.slots = new int[]{config.getInt("slot", -1)};
            if (config.has("item"))
                res.item = CustomItem.deserialize(config.getConfig("item"));
            if (config.has("click"))
                res.click = ItemClick.deserialize(config.getConfig("click"));

            return res;
        } catch (InvalidGuiConfigurationException e) {
            if(res.slots != null)
                e.addLocalizer("in slot " + Arrays.toString(res.slots));
            else
                e.addLocalizer("In gui item");
            throw e;
        }
    }

    public static List<ConfigItem> deserialize(Collection<Config> config) {
        return config.stream().map(ConfigItem::deserialize).collect(Collectors.toList());
    }

    public static class ItemClick implements Link {
        private String permission;
        private PlaceholderValue<String> noPermissionError;
        private Sound noPermissionSound;

        private int cost;
        private String economy;
        private PlaceholderValue<String> noMoneyError;
        private Sound noMoneySound;

        private Action[] actions;

        public boolean checkPermission(Player player) {
            if(permission != null && !player.hasPermission(permission)) {
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


        public static ItemClick deserialize(Config config) {
            ItemClick res = new ItemClick();
            res.permission = (String) config.get("permission");
            res.noPermissionError = config.getMessage("noPermissionMessage", "You don't have permission!");
            res.noPermissionSound = config.getSound("noPermissionSound");

            res.cost = config.getInt("cost", 0);
            res.economy = config.getString("economy", "");
            res.noMoneyError = config.getMessage("noMoneyError", "You don't have enough money");
            res.noMoneySound = config.getSound("noMoneySound");

            List<Object> actions = (List<Object>) config.get("actions");
            if(actions == null)
                res.actions = null;
            else
                res.actions = actions.stream().map(ActionType::deserialize).toArray(Action[]::new);
            return res;
        }

        @Override
        public void run(Player player) {
            onClick(player);
        }

        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(", ");
            joiner.add("permission: " + permission);
            joiner.add("noPermissionError: " + noPermissionError);
            joiner.add("noPermissionSound: " + noPermissionSound);
            joiner.add("coost: " + cost);
            joiner.add("economy: " + economy);
            joiner.add("noMoneyError: " + noMoneyError);
            joiner.add("noMoneySound: " + noMoneySound);
            return '{' + joiner.toString() + '}';
        }
    }

    @Override
    public String toString() {
        return "{slots:" + Arrays.toString(slots) + ", item:" + item + ", click:" + click + "}";
    }
}
