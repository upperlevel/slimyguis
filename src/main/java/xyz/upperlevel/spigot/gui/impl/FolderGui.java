package xyz.upperlevel.spigot.gui.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.BaseGui;
import xyz.upperlevel.spigot.gui.GuiSize;
import xyz.upperlevel.spigot.gui.GuiUtil;
import xyz.upperlevel.spigot.gui.link.Link;

import java.util.HashMap;
import java.util.Map;

import static xyz.upperlevel.spigot.gui.GuiUtil.itemStack;

@Getter
@Accessors(fluent = true, chain = true)
public class FolderGui implements BaseGui {
    public static final ItemStack BACK_BUTTON = GuiUtil.itemStack(Material.BARRIER, ChatColor.RED + "Back");

    @Getter
    @Accessors()
    private String id = null;

    private Map<Integer, Item> items = new HashMap<>();
    private int nextFreeSlot = 0;

    private final int size;

    @Getter
    private String title;

    public FolderGui(String title, int size) {
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
        Item item = new Item(link, display);
        item.slot = nextFreeSlot;
        findNextFree();
        items.put(item.slot, item);
        return this;
    }

    public FolderGui addLink(Link link, Material mat, String name, String... lores) {
        addLink(link, itemStack(mat, name, lores));
        return this;
    }

    public FolderGui setLink(Link link, int slot, ItemStack display) {
        Item item = new Item(link, display);
        item.slot = slot;
        if (slot < 0 || (slot > size && size > 0))
            throw new IllegalArgumentException("slot out of borders!");
        if (slot == nextFreeSlot)
            findNextFree();
        items.put(item.slot, item);
        return this;
    }

    public FolderGui setLink(Link link, int slot, Material mat, String name, String... lores) {
        setLink(link, slot, itemStack(mat, name, lores));
        return this;
    }

    protected void findNextFree() {
        while (items.containsKey(++nextFreeSlot) && (size < 0 || nextFreeSlot < size)) ;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Item a = items.get(event.getSlot());
        if (a != null)
            a.link.run((Player) event.getWhoClicked());
    }

    public FolderGui title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Inventory create(Player player) {
        final int usedSize = size > 0 ? size : GuiSize.min(items.values().stream().mapToInt(i -> i.slot).max().orElse(8) + 1);

        Inventory inv = Bukkit.createInventory(null, usedSize, title);
        for (Item item : items.values())
            inv.setItem(item.slot, item.display);

        return inv;
    }

    @RequiredArgsConstructor
    public static class Item {

        public final Link link;
        public final ItemStack display;
        public int slot;
    }
}
