package xyz.upperlevel.spigot.gui.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.*;
import xyz.upperlevel.spigot.gui.link.Link;

@Accessors(fluent = true, chain = true)
public class ConfirmGui implements BaseGui {

    private static final ItemStack ACCEPT = GuiUtil.wool(DyeColor.GREEN, ChatColor.GREEN + "ACCEPT");
    private static final ItemStack DECLINE = GuiUtil.wool(DyeColor.RED, ChatColor.RED + "DECLINE");

    @Getter
    @Accessors()
    private String id = null;

    @Getter
    @Setter
    private Link onConfirm = GuiAction.close(),
                 onDecline = GuiAction.close(),
                 onClose   = Link.EMPTY; //We cannot close the gui on the close event, even if the GuiManager is protected from this
    @Getter
    private ItemStack acceptItem = ACCEPT, declineItem = DECLINE;

    @Getter
    private String title = "Confirm";

    @Getter
    private ItemStack descriptionItem;

    public ConfirmGui title(String title) {
        this.title = title;
        return this;
    }

    public ConfirmGui acceptItem(ItemStack accept) {
        this.acceptItem = accept;
        return this;
    }

    public ConfirmGui declineItem(ItemStack decline) {
        this.declineItem = decline;
        return this;
    }

    public ConfirmGui descriptionItem(ItemStack confirm) {
        this.descriptionItem = confirm;
        return this;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        final ItemStack item = event.getCurrentItem();
        if(item == null) return;
        final int slot = event.getSlot();

        if (slot >= 27) {
            int x = slot % 9;
            if(x == 4)
                return;//Middle items
            if(x < 4) //formula: slot = y * 9 + x -> x = slot % 9
                onConfirm.run((Player) event.getWhoClicked());
            else
                onDecline.run((Player)event.getWhoClicked());
        }
    }

    @Override
    public Inventory create(Player player) {
        Inventory inv = Bukkit.createInventory(null, GuiSize.DOUBLE.size(), title);
        inv.setItem(4, descriptionItem);

        for (int x = 0; x < 9; x++) {
            if (x == 4) continue;
            for (int y = 3; y < 6; y++)
                inv.setItem(y * 9 + x, x < 4 ? acceptItem : declineItem);
        }
        return inv;
    }
}
