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
import xyz.upperlevel.spigot.gui.BaseGui;
import xyz.upperlevel.spigot.gui.GuiAction;
import xyz.upperlevel.spigot.gui.GuiSize;
import xyz.upperlevel.spigot.gui.GuiUtils;
import xyz.upperlevel.spigot.gui.impl.link.Link;

@Accessors(fluent = true, chain = true)
public class ConfirmGui extends BaseGui {

    private static final ItemStack ACCEPT = GuiUtils.wool(DyeColor.GREEN, ChatColor.GREEN + "ACCEPT");
    private static final ItemStack DECLINE = GuiUtils.wool(DyeColor.RED, ChatColor.RED + "DECLINE");

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
        clear();
        return this;
    }


    public ConfirmGui acceptItem(ItemStack accept) {
        this.acceptItem = accept;
        clear();
        return this;
    }

    public ConfirmGui declineItem(ItemStack decline) {
        this.declineItem = decline;
        clear();
        return this;
    }

    public ConfirmGui descriptionItem(ItemStack confirm) {
        this.descriptionItem = confirm;
        clear();
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
    protected Inventory render() {
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
