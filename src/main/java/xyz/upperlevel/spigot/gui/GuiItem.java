package xyz.upperlevel.spigot.gui;

import lombok.Data;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.config.InvalidGuiConfigurationException;
import xyz.upperlevel.spigot.gui.config.action.Action;
import xyz.upperlevel.spigot.gui.config.action.ActionType;
import xyz.upperlevel.spigot.gui.config.economy.EconomyManager;
import xyz.upperlevel.spigot.gui.config.itemstack.CustomItem;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;
import xyz.upperlevel.spigot.gui.config.util.Config;
import xyz.upperlevel.spigot.gui.link.Link;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Data
public class GuiItem {

    public static class ItemClick implements Link {

        private String permission;
        private PlaceholderValue<String> noPermissionError;
        private Sound noPermissionSound;

        private PlaceholderValue<Double> cost;
        private PlaceholderValue<String> noMoneyError;
        private Sound noMoneySound;

        private List<Action> actions = new ArrayList<>();

        private ItemClick() {
        }

        public boolean checkPermission(Player player) {
            if (permission != null && !player.hasPermission(permission)) {
                player.sendMessage(noPermissionError.get(player));
                if (noPermissionSound != null)
                    player.playSound(player.getLocation(), noPermissionSound, 1.0f, 1.0f);
                return false;
            } else return true;
        }

        public boolean pay(Player player) {
            double c = cost.get(player);
            if (c > 0) {
                final Economy economy = EconomyManager.getEconomy();
                if (economy == null) {
                    SlimyGuis.logger().severe("Cannot use economy: vault not found!");
                    return true;
                }
                EconomyResponse res = economy.withdrawPlayer(player, c);
                if (!res.transactionSuccess()) {
                    player.sendMessage(noMoneyError.get(player));
                    if (noMoneySound != null)
                        player.playSound(player.getLocation(), noMoneySound, 1.0f, 1.0f);
                    System.out.println(res.errorMessage);
                    return false;
                } else return true;
            }
            return true;
        }

        @SuppressWarnings("unchecked")
        public static ItemClick deserialize(Config config) {
            ItemClick res = new ItemClick();
            res.permission = (String) config.get("permission");
            res.noPermissionError = config.getMessage("no-permission-message", "You don't have permission!");
            res.noPermissionSound = config.getSound("no-permission-sound");

            res.cost = PlaceholderValue.doubleValue(config.getString("cost", "0"));
            res.noMoneyError = config.getMessage("no-money-error", "You don't have enough money");
            res.noMoneySound = config.getSound("no-money-sound");

            List<Object> actions = (List<Object>) config.get("actions");
            if (actions == null)
                res.actions = Collections.emptyList();
            else
                res.actions = actions.stream()
                        .map(ActionType::deserialize)
                        .collect(Collectors.toList());
            return res;
        }

        @Override
        public void run(Player player) {
            if (checkPermission(player) && pay(player)) {
                for (Action action : actions)
                    action.run(player);
            }
        }

        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(", ");
            joiner.add("permission: " + permission);
            joiner.add("noPermissionError: " + noPermissionError);
            joiner.add("noPermissionSound: " + noPermissionSound);
            joiner.add("cost: " + cost);
            joiner.add("noMoneyError: " + noMoneyError);
            joiner.add("noMoneySound: " + noMoneySound);
            return '{' + joiner.toString() + '}';
        }
    }

    private int slot;
    private CustomItem item;
    private ItemClick click;

    public GuiItem() {
        slot = -1;
        item = null;
        click = null;
    }

    public GuiItem(int slot, ItemStack item) {
        this.slot = slot;
        this.item = new CustomItem(item);
    }

    public GuiItem(int slot, CustomItem item) {
        this.slot = slot;
        this.item = item;
    }

    public GuiItem(int slot, CustomItem item, ItemClick click) {
        this.slot = slot;
        this.item = item;
        this.click = click;

    }

    public GuiItem(int slot, ItemStack item, ItemClick click) {
        this.slot = slot;
        this.item = new CustomItem(item);
        this.click = click;
    }

    public void onClick(InventoryClickEvent e) {
        if (click != null)
            click.run((Player) e.getWhoClicked());
    }

    public static GuiItem deserialize(Config config) {
        GuiItem res = new GuiItem();

        try {
            if (config.has("slot"))
                res.slot = config.getInt("slot");

            if (config.has("item"))
                res.item = CustomItem.deserialize(config.getConfig("item"));

            if (config.has("click"))
                res.click = ItemClick.deserialize(config.getConfig("click"));

            return res;
        } catch (InvalidGuiConfigurationException e) {
            if (res.slot != -1)
                e.addLocalizer("in slot " + res.slot);
            else
                e.addLocalizer("in gui item");
            throw e;
        }
    }
}