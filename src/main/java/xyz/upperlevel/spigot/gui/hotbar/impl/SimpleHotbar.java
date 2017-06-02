package xyz.upperlevel.spigot.gui.hotbar.impl;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.hotbar.Hotbar;
import xyz.upperlevel.spigot.gui.hotbar.HotbarLink;
import xyz.upperlevel.spigot.gui.hotbar.HotbarView;

import java.util.*;

public class SimpleHotbar implements Hotbar {//TODO use an arraylist
    private Map<ItemStack, HotbarLink> links = new LinkedHashMap<>();
    private SimpleHotbarView view = new SimpleHotbarView();

    public SimpleHotbar(List<HotbarLink> links) {
        checkSize(links.size());
        for(HotbarLink link : links)
            this.links.put(link.getDisplay(), link);
    }

    public SimpleHotbar(HotbarLink... links) {
        this(Arrays.asList(links));
    }

    private void checkSize(int size) {
        if(size > 9)
            throw new IllegalArgumentException("Cannot have more than 9 items in hotbar!");
    }

    @Override
    public HotbarView print(Player p) {
        return view;
    }

    protected class SimpleHotbarView implements HotbarView {

        @Override
        public Collection<ItemStack> getItems() {
            return links.keySet();
        }

        @Override
        public boolean onClick(Player player, ItemStack item, int relativeSlot, Action action, Block blockClicked, BlockFace blockFace) {
            HotbarLink link = links.get(item);
            if(link != null) {
                link.getAction().run(player);
                return true;
            } else return false;
        }
    }

    public static SimpleHotbar of(HotbarLink... links) {
        return new SimpleHotbar(Arrays.asList(links));
    }

    public static SimpleHotbar of(List<HotbarLink> links) {
        return new SimpleHotbar(links);
    }
}
