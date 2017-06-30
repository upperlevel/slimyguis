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
import xyz.upperlevel.spigot.book.CustomBookOpenEvent;
import xyz.upperlevel.spigot.gui.hotbar.Hotbar;
import xyz.upperlevel.spigot.gui.hotbar.HotbarManager;

public class GuiEventListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    protected void onPlayerClick(InventoryClickEvent e) {
        Hotbar hotbar = HotbarManager.get((Player) e.getWhoClicked());
        //isSlotLink is really fast but it only works with the main player inventory where the first 9 indexes are
        //for the hotbar. this isn't exactly the main inventory but it works aswell
        if (hotbar != null && hotbar.isSlotLink(e.getHotbarButton())) {
            e.setCancelled(true);
            return;
        }

        if (e.getClickedInventory() == e.getInventory())  //getInventory returns always the top inv. so it's like saying that the clicked inventory must be the top one
            GuiManager.onClick(e);

        // if (hotbar != null && hotbar.anyItemLink(e.getCurrentItem(), e.getCursor())) {//TODO use normal slots
        //    e.setCancelled(true);
        //    ((Player) e.getWhoClicked()).updateInventory();
        if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (GuiManager.get((Player) e.getWhoClicked()) != null)
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
        if (e.getPlayer() instanceof Player && !GuiManager.isCalled()) {
            //Cannot call Inventory actions in an inventory event
            Bukkit.getScheduler().runTaskLater(
                    SlimyGuis.getInstance(),
                    () -> GuiManager.back((Player) e.getPlayer()),
                    0
            );
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    protected void onCustomBookOpen(CustomBookOpenEvent e) {
        GuiManager.close(e.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.HAND && e.getAction() != Action.PHYSICAL)
            HotbarManager.onClick(e); // this method cancels the event by himself
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Hotbar hotbar = HotbarManager.get(e.getPlayer());
        if (hotbar != null && hotbar.isSlotLink(e.getPlayer().getInventory().getHeldItemSlot()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Hotbar hotbar = HotbarManager.get(e.getPlayer());
        if (hotbar != null)
            hotbar.give(e.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Hotbar hotbar = HotbarManager.get(e.getEntity());
        if (hotbar != null)
            e.setKeepInventory(true);
    }
}
