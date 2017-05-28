package xyz.upperlevel.guiapi.hotbar;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public interface HotbarView {
    /**
     * returns the printed view, this should NOT change over time, even because this method doesn't get called in stacked views
     * @return the printed view
     */
    Collection<ItemStack> getItems();

    /**
     * Called when the player clicks an item in this hotbar view
     * @param player the clicker
     * @param item the ItemStack that's being clicked
     * @param relativeSlot the slot relative to the view (it changes when multiple views are stacked)
     * @param action the action of the player onClick
     * @param blockClicked the block that could be clicked on
     * @param blockFace the face of the block clicked
     * @return true if the player is clicking an item in the view
     */
    boolean onClick(Player player, ItemStack item, int relativeSlot, Action action, Block blockClicked, BlockFace blockFace);
}
