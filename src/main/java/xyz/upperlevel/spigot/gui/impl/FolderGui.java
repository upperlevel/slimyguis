package xyz.upperlevel.spigot.gui.impl;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.BaseGui;
import xyz.upperlevel.spigot.gui.GuiManager;
import xyz.upperlevel.spigot.gui.GuiSize;
import xyz.upperlevel.spigot.gui.GuiUtils;
import xyz.upperlevel.spigot.gui.impl.link.Link;

import java.util.LinkedHashMap;
import java.util.Map;

import static xyz.upperlevel.spigot.gui.GuiUtils.itemStack;

@Accessors(fluent = true, chain = true)
public class FolderGui extends BaseGui {
    public static final ItemStack DEF_BACK_BUTTON = GuiUtils.itemStack(Material.BARRIER, ChatColor.RED + "Back");

    private final Map<ItemStack, Link> components;

    private final int size;

    @Getter
    private String title;

    @Getter
    private ItemStack backButton = DEF_BACK_BUTTON;

    public FolderGui(String title, int size) {
        if(size > 0)
            components = new LinkedHashMap<>(size);
        else
            components = new LinkedHashMap<>();
        this.size = size;
        this.title = title;
    }

    public FolderGui(String title, GuiSize size) {
        this(title, size != null ? size.size() : -1);
    }

    public FolderGui(String title) {
        this(title, -1);
    }

    public FolderGui addLink(Link link, ItemStack display) {
        components.put(display, link);
        return this;
    }

    public FolderGui addLink(Link link, Material mat, String name, String... lores) {
        addLink(link, itemStack(mat, name, lores));
        return this;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if(backButton != null && backButton.equals(event.getCurrentItem())) {
            GuiManager.back((Player) event.getWhoClicked());
        } else {
            Link a = components.get(event.getCurrentItem());
            if (a != null)
                a.run((Player) event.getWhoClicked());
        }
    }

    public FolderGui title(String title) {
        this.title = title;
        clear(); //Reprint
        return this;
    }

    public FolderGui backButton(ItemStack button) {
        this.backButton = button;
        clear(); //Reprint
        return this;
    }


    @Override
    protected Inventory render() {
        final int usedSize = size > 0 ? size : GuiSize.min(components.size() + (backButton != null ? 1 : 0));

        Inventory inv = Bukkit.createInventory(null, usedSize, title);
        int i = 0;
        for(Map.Entry<ItemStack, Link> comp : components.entrySet())
            inv.setItem(i++, comp.getKey());

        if(backButton != null)
            inv.setItem(usedSize - 1, backButton);

        return inv;
    }
}
