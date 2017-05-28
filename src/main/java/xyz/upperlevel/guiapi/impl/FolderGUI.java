package xyz.upperlevel.guiapi.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.guiapi.BaseGUI;
import xyz.upperlevel.guiapi.GuiSize;
import xyz.upperlevel.guiapi.impl.link.Link;

import java.util.LinkedHashMap;
import java.util.Map;

import static xyz.upperlevel.guiapi.GuiUtils.itemStack;

public class FolderGUI extends BaseGUI {
    private final Map<ItemStack, Link> components;

    private final int size;

    @Getter
    @Setter
    private String title;

    public FolderGUI(String title, int size) {
        if(size > 0)
            components = new LinkedHashMap<>(size);
        else
            components = new LinkedHashMap<>();
        this.size = size;
        this.title = title;
    }

    public FolderGUI(String title, GuiSize size) {
        this(title, size != null ? size.size() : -1);
    }

    public FolderGUI(String title) {
        this(title, -1);
    }

    public FolderGUI addLink(Link link, ItemStack display) {
        components.put(display, link);
        return this;
    }

    public FolderGUI addLink(Link link, Material mat, String name, String... lores) {
        addLink(link, itemStack(mat, name, lores));
        return this;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Link a = components.get(event.getCurrentItem());
        if(a != null)
            a.run((Player) event.getWhoClicked());
    }

    @Override
    protected Inventory render() {
        Inventory inv = Bukkit.createInventory(null, size > 0 ? size : GuiSize.min(components.size()), title);
        int i = 0;
        for(Map.Entry<ItemStack, Link> comp : components.entrySet())
            inv.setItem(i++, comp.getKey());
        return inv;
    }
}
