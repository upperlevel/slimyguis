package xyz.upperlevel.spigot.gui.example;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.spigot.gui.BaseGUI;
import xyz.upperlevel.spigot.gui.GuiAction;
import xyz.upperlevel.spigot.gui.GuiManager;
import xyz.upperlevel.spigot.gui.GuiSize;
import xyz.upperlevel.spigot.gui.hotbar.Hotbar;
import xyz.upperlevel.spigot.gui.hotbar.HotbarLink;
import xyz.upperlevel.spigot.gui.hotbar.HotbarManager;
import xyz.upperlevel.spigot.gui.hotbar.handlers.SimpleHotbar;
import xyz.upperlevel.spigot.gui.impl.ConfirmGui;
import xyz.upperlevel.spigot.gui.impl.FolderGUI;
import xyz.upperlevel.spigot.gui.impl.link.Link;

import static xyz.upperlevel.spigot.gui.GuiAction.change;
import static xyz.upperlevel.spigot.gui.GuiAction.close;
import static xyz.upperlevel.spigot.gui.GuiUtils.wood;
import static xyz.upperlevel.spigot.gui.GuiUtils.wool;
import static xyz.upperlevel.spigot.gui.hotbar.HotbarLink.newLink;

public class Main extends JavaPlugin implements Listener {
    //Example of creating strange ItemStacks
    private static final ItemStack BAN_ITEM = wool(DyeColor.RED, "Ban");

    private static final Hotbar hotbar = SimpleHotbar.of(
            HotbarLink.newLink(Link.consoleCommand("kick <player>"), wool(DyeColor.BLUE, ChatColor.BLUE + "Kick")),
            newLink(p -> p.sendMessage("pong"), wool(DyeColor.ORANGE, ChatColor.GOLD +"Ping")),
            newLink(Link.command("help"), wool(DyeColor.WHITE, ChatColor.WHITE + "Help")),
            newLink(
                    new FolderGUI("Tools")
                            .addLink(
                                    GuiAction.change(
                                            new ConfirmGui()
                                                    .title("Sure to ban?")
                                                    .descriptionItem(BAN_ITEM) //Optional, but suggesed
                                                    .onConfirm(Link.consoleCommand("ban <player>"))
                                    ),
                                    BAN_ITEM)
                            .addLink(GuiAction.close().and(Link.consoleCommand("kick <player>")), wool(DyeColor.YELLOW, "Kick"))
                            .addLink(close().and(Link.consoleCommand("say <player> is STUPID!")), wool(DyeColor.GREEN, "Poke"))
                            .addLink(change(new RainbowGUI()), wool(DyeColor.LIGHT_BLUE, "Rainbow :)")), //Example of the stack-like Gui system
                    wood(TreeSpecies.ACACIA, ChatColor.GRAY + "Tools")
            ),
            newLink(new RainbowGUI(), wool(DyeColor.GREEN, "Rainbow!")),
            newLink(new DispenserGUI(), wool(DyeColor.BLACK, "Dispenser"))
    );

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public void openRainbow(Player player) {
        //Example showing that the GUI can be opened or closed without any Hotbar
        GuiManager.open(player, new RainbowGUI());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //HotbarManager.add adds the Hotbar to the others already applied (if any)
        //were HotbarManager.set would replace the Hotbar without adding it
        //the add-way is more suggested because it can support other plugins
        HotbarManager.add(event.getPlayer(), hotbar);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            openRainbow((Player) sender);
            return true;
        }
        return false;
    }


    private static class RainbowGUI extends BaseGUI {
        //Example of a simple GUI

        @Override
        public void onClick(InventoryClickEvent event) {
            //This should process the click
            if(event.getCurrentItem().getType() != Material.AIR)
                event.getWhoClicked().sendMessage("Clicked: " + DyeColor.getByWoolData(event.getCurrentItem().getData().getData()));
        }

        @Override
        protected Inventory render() {
            DyeColor[] colors = DyeColor.values();
            Inventory inv = Bukkit.createInventory(null, GuiSize.min(colors.length));
            for(int i = 0; i < colors.length; i++)
                inv.setItem(i, wool(colors[i], colors[i].name().toLowerCase()));
            return inv;
        }
    }

    private static class DispenserGUI extends BaseGUI {
        @Override
        public void onClick(InventoryClickEvent event) {
        }

        @Override
        protected Inventory render() {
            Inventory inv = Bukkit.createInventory(null, InventoryType.DISPENSER);
            for(int i = 0; i < 9; i++)
                inv.setItem(i, new ItemStack(Material.DIAMOND, 64));
            return inv;
        }
    }
}
