package xyz.upperlevel.spigot.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public interface BaseGui extends Gui {

    Inventory create(Player player);

    @Override
    default void print(Player player) {
        player.openInventory(create(player));
    }

    @Override
    default void onOpen(Player player) {
    }

    @Override
    default void onClick(InventoryClickEvent e) {
    }

    @Override
    default void onClose(Player player) {
    }
}
