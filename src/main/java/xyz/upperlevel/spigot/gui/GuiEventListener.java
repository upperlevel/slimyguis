package xyz.upperlevel.spigot.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.book.CustomBookOpenEvent;
import xyz.upperlevel.spigot.gui.hotbar.Hotbar;
import xyz.upperlevel.spigot.gui.hotbar.HotbarManager;

import java.util.Set;
import java.util.stream.Collectors;

public class GuiEventListener implements Listener{

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    protected void onPlayerClick(InventoryClickEvent e) {
        Hotbar hotbar = HotbarManager.get((Player) e.getWhoClicked());
        if(e.getAction() == InventoryAction.HOTBAR_SWAP || e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            //isSlotLink is really fast but it only works with the main player inventory where the first 9 indexes are
            //for the hotbar. this isn't exactly the main inventory but it works aswell
            if(hotbar != null && hotbar.isSlotLink(e.getHotbarButton())) {
                e.setCancelled(true);
                return;
            }
        }

        if(e.getClickedInventory() == e.getInventory())  //getInventory returns always the top inv. so it's like saying that the clicked inventory must be the top one
            GuiManager.onClick(e);
        if(hotbar != null && hotbar.anyItemLink(e.getCurrentItem(), e.getCursor())) {//TODO use normal slots
            e.setCancelled(true);
            ((Player) e.getWhoClicked()).updateInventory();
        } else if(e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if(GuiManager.get((Player) e.getWhoClicked()) != null)
                e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    protected void onPlayerQuit(PlayerQuitEvent e) {
        GuiManager.close(e.getPlayer());
        HotbarManager.remove(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    protected void onInventoryClose(InventoryCloseEvent e) {
        if(e.getPlayer() instanceof Player && !GuiManager.isCalled()) {
            //Cannot call Inventory actions in an inventory event
            Bukkit.getScheduler().runTaskLater(
                    Main.getInstance(),
                    () ->  GuiManager.back((Player) e.getPlayer()),
                    0
            );
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    protected void onCustomBookOpen(CustomBookOpenEvent e) {
        GuiManager.close(e.getPlayer());
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent e) {//TODO put in first slot
        if(HotbarManager.isItemSimilarToLink(e.getPlayer(), e.getItem().getItemStack()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getHand() == EquipmentSlot.HAND && e.getAction() != Action.PHYSICAL)
            HotbarManager.onClick(e);//This method cancels the event by himself
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if(HotbarManager.isItemLink(e.getPlayer(), e.getItemDrop().getItemStack()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        HotbarManager.reprint(e.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(e.getKeepInventory() || e.getDrops().isEmpty()) return;
        Set<ItemStack> items = HotbarManager.linkStream(e.getEntity()).collect(Collectors.toSet());
        if(!items.isEmpty())
            e.getDrops().removeIf(items::contains);
    }
}
