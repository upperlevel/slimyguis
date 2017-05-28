package xyz.upperlevel.guiapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class BaseGUI implements Gui {
    protected Inventory buffer = null;

    @Override
    public void print(Player player) {
        player.openInventory(needsUpdate() ? (buffer = render()) : buffer);
    }

    /**
     * this methods returns true oly if the next reprint should re-render the inventory
     * @return true oly if the next reprint should re-render the inventory
     */
    protected boolean needsUpdate() {
        return buffer == null;
    }

    /**
     * clears the buffer forcing a re-renderization at the next reprint
     */
    public void clear() {
        buffer = null;
    }

    @Override
    public void onOpen(Player player){}

    @Override
    public void onClose(Player player){}

    /**
     * renders the Inventory
     * @return the inventory to open at the player
     */
    protected abstract Inventory render();
}
