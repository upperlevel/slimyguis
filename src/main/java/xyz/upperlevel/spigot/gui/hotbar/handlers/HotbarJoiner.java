package xyz.upperlevel.spigot.gui.hotbar.handlers;

import lombok.Data;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.gui.hotbar.Hotbar;
import xyz.upperlevel.spigot.gui.hotbar.HotbarView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HotbarJoiner implements Hotbar {

    private List<Hotbar> handlers = new ArrayList<>();

    public HotbarJoiner add(Hotbar handler) {
        handlers.add(handler);
        return this;
    }

    @Override
    public HotbarView print(Player p) {
        return new JoinerView(handlers, p);
    }

    protected class JoinerView implements HotbarView {

        private ViewData[] views;
        private List<ItemStack> links;


        public JoinerView(List<Hotbar> handlers, Player viewer) {
            links = new ArrayList<>();
            int index = 0;
            int vi = 0;
            for(Hotbar h : handlers) {
                HotbarView view = h.print(viewer);
                Collection<ItemStack> l = view.getItems();
                int len = l.size();
                views[vi++] = new ViewData(index, index + len, view);
                index += len;
                if(links.size() + len > 9)
                    throw new IllegalStateException("Too many Links!");
                links.addAll(l);
            }
        }

        public ViewData get(int relativeSlot) {
            for(ViewData data : views)
                if(data.end > relativeSlot)
                    return data;
            return null;
        }


        @Override
        public Collection<ItemStack> getItems() {
            return links;
        }

        @Override
        public boolean onClick(Player player, ItemStack item, int relativeSlot, Action action, Block blockClicked, BlockFace blockFace) {
            ViewData data = get(relativeSlot);
            if(data == null)
                return false;
            return data.view.onClick(player, item, relativeSlot - data.begin, action, blockClicked, blockFace);
        }


        @Data
        private class ViewData {
            private final int begin, end;
            private final HotbarView view;
        }
    }
}
