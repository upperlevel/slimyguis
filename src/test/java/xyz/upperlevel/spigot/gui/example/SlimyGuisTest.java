package xyz.upperlevel.spigot.gui.example;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.spigot.book.BookUtil;
import xyz.upperlevel.spigot.gui.*;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.spigot.gui.events.*;
import xyz.upperlevel.spigot.gui.hotbar.HotbarLink;
import xyz.upperlevel.spigot.gui.hotbar.HotbarManager;
import xyz.upperlevel.spigot.gui.impl.anvil.AnvilInputGui;
import xyz.upperlevel.spigot.gui.impl.anvil.InputFilters;
import xyz.upperlevel.spigot.gui.link.Link;

import java.io.File;
import java.util.logging.Level;

import static xyz.upperlevel.spigot.gui.GuiAction.change;
import static xyz.upperlevel.spigot.gui.GuiAction.close;
import static xyz.upperlevel.spigot.gui.GuiManager.open;
import static xyz.upperlevel.spigot.gui.GuiUtil.wood;
import static xyz.upperlevel.spigot.gui.GuiUtil.wool;
import static xyz.upperlevel.spigot.gui.hotbar.HotbarLink.newLink;

public class SlimyGuisTest extends JavaPlugin implements Listener {
    //Example of creating strange ItemStacks
    private static final ItemStack BAN_ITEM = wool(DyeColor.RED, "Ban");

    static {
        // Hooks a placeholder handler
        PlaceHolderUtil.tryHook();
    }

    private static final HotbarLink[] links = new HotbarLink[]{
            HotbarLink.newLink(Link.consoleCommand("kick <player>"), wool(DyeColor.BLUE, ChatColor.BLUE + "Kick")),
            newLink(p -> p.sendMessage("pong"), wool(DyeColor.ORANGE, ChatColor.GOLD + "Ping")),
            newLink(Link.command("help"), wool(DyeColor.WHITE, ChatColor.WHITE + "Help")),
            newLink(
                    new FolderGui("Tools")
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
                            .addLink(change(new RainbowGui()), wool(DyeColor.LIGHT_BLUE, "Rainbow :)")), //Example of the stack-like Gui system
                    wood(TreeSpecies.ACACIA, ChatColor.GRAY + "Tools")
            ),
            newLink(new RainbowGui(), wool(DyeColor.GREEN, "Rainbow!")),
            newLink(new DispenserGui(), wool(DyeColor.BLACK, "Dispenser")),
            newLink(new AnvilInputGui()
                            .message("Put your age")
                            .listener(InputFilters.filterInt((player, age) -> {
                                GuiManager.close(player);
                                player.sendMessage(age >= 18 ? "You can watch this" : "Go away!");
                            })),
                    wool(DyeColor.YELLOW, "Check Age")
            ),
            newLink(
                    new FolderGui("Choose anvil")
                            .addLink(
                                    new AnvilInputGui()
                                            .message("Put text")
                                            .listener(
                                                    InputFilters.plain((p, t) ->
                                                            BookUtil.openPlayer(
                                                                    p,
                                                                    BookUtil.writtenBook()
                                                                            .pagesRaw(t)
                                                                            .build()
                                                            )
                                                    )
                                            ),
                                    wool(DyeColor.WHITE, "Text")
                            )
                            .addLink(
                                    new AnvilInputGui()
                                            .message("Put player to kick")
                                            .listener(
                                                    InputFilters.filterPlayer((player, selected) -> {
                                                        GuiManager.change(
                                                                player,
                                                                new ConfirmGui()
                                                                        .title("Do you really wanna kick " + selected.getName())
                                                                        .onConfirm(close().and(pl -> selected.kickPlayer("You've been kicked by " + pl.getName())))
                                                        );
                                                    })
                                            ),
                                    wool(DyeColor.RED, "Kick")
                            )
                            .addLink(
                                    new AnvilInputGui()
                                            .message("Put your age")
                                            .listener(
                                                    InputFilters.filterInt((player, age) -> {
                                                        BookUtil.openPlayer(player, getDrugsBook(age));
                                                    })
                                            ),
                                    wool(DyeColor.GREEN, "Drugs")
                            ),
                    GuiUtil.setNameAndLores(new ItemStack(Material.ANVIL), "Anvil tests")
            ),
            newLink(
                    new FolderGui("deep-test")
                            .addLink(
                                    new RainbowGui(),
                                    wool(DyeColor.BROWN, "Deep Rainbow")
                            ),
                    wool(DyeColor.BLUE, "Deep shit")
            )
    };

    private static ItemStack getDrugsBook(int age) {
        if (age < 18) {
            return BookUtil.writtenBook()
                    .title(ChatColor.RED + "You're underage!")
                    .pages(
                            new BookUtil.PageBuilder()
                                    .add(
                                            BookUtil.TextBuilder.of("You're underage!")
                                                    .color(ChatColor.RED)
                                                    .style(ChatColor.BOLD)
                                                    .onHover(BookUtil.HoverAction.showText("age < 18"))
                                                    .build()
                                    )
                                    .newLine()
                                    .add("Wait another " + (18 - age) + " years and then return here")
                                    .build()
                    )
                    .build();
        } else
            return BookUtil.writtenBook()
                    .title(ChatColor.GREEN + "Drugs book")
                    .pages(
                            new BookUtil.PageBuilder()
                                    .add(
                                            BookUtil.TextBuilder.of("Here's the drug")
                                                    .color(ChatColor.DARK_GREEN)
                                                    .style(ChatColor.BOLD)
                                                    .onHover(BookUtil.HoverAction.showText("Take the drugs!"))
                                                    .onClick(BookUtil.ClickAction.openUrl("https://www.google.it/search?q=drugs"))
                                                    .build()
                                    )
                                    .newLine()
                                    .add("Now run! the ")
                                    .add(
                                            BookUtil.TextBuilder.of("POLICE")
                                                    .color(ChatColor.BLUE)
                                                    .onHover(BookUtil.HoverAction.showText("RUUUN"))
                                                    .onClick(BookUtil.ClickAction.runCommand("The police caught me :("))
                                                    .build()
                                    )
                                    .add(" is following us")
                                    .build()
                    )
                    .build();
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        File folder = new File(getDataFolder(), "guis");
        folder.mkdirs();

        // Loads all the guis files inside the given folder
        SlimyGuis.logger().log(Level.INFO, "Loading guis...");
        GuiManager.loadFolder(folder);
        SlimyGuis.logger().log(Level.INFO, "Configurable guis loaded!");
    }

    public void openRainbow(Player player) {
        //Example showing that the GUI can be opened or closed without any Hotbar
        open(player, new RainbowGui());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //HotbarManager.add adds the Hotbar to the others already applied (if any)
        //were HotbarManager.set would replace the Hotbar without adding it
        //the add-way is more suggested because it can support other plugins
        HotbarManager.add(event.getPlayer(), links);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        // Opens up the gui when player break obsidian block
        if (e.getBlock().getType() == Material.OBSIDIAN) {

            // Gets a gui that may be loaded from the .yml file
            Gui gui = GuiManager.get("test");
            if (gui == null)
                e.getPlayer().sendMessage("this gui has not been loaded");
            else
                // Opens the gui 'test' loaded
                GuiManager.open(e.getPlayer(), gui);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            openRainbow((Player) sender);
            return true;
        }
        return false;
    }


    //Example of a simple GUI
    private static class RainbowGui extends CustomGui {

        public RainbowGui() {
            super("rainbow", GuiSize.min(DyeColor.values().length));

            DyeColor[] colors = DyeColor.values();
            for (int i = 0; i < DyeColor.values().length; i++)
                setItem(i, wool(colors[i], colors[i].name().toLowerCase()));
        }

        @Override
        public void onClick(InventoryClickEvent event) {
            //This should process the click
            if (event.getCurrentItem().getType() != Material.AIR)
                event.getWhoClicked().sendMessage("Clicked: " + DyeColor.getByWoolData(event.getCurrentItem().getData().getData()));
        }
    }

    private static class DispenserGui extends CustomGui {

        public DispenserGui() {
            super("dispenser", InventoryType.DISPENSER);

            for (int i = 0; i < 9; i++)
                setItem(i, new ItemStack(Material.DIAMOND, 64));
        }

        @Override
        public void onClick(InventoryClickEvent event) {
        }
    }

    @EventHandler
    public void onGuiBack(GuiBackEvent e) {
        System.out.println("Back: gui:" + e.getGui() + ", old:" + e.getOldGui());
    }

    @EventHandler
    public void onGuiChange(GuiChangeEvent e) {
        System.out.println("Change: gui:" + e.getGui() + ", old:" + e.getOldGui());
    }

    @EventHandler
    public void onGuiClick(GuiClickEvent e) {
        System.out.println("Click: gui:" + e.getGui() + ", click:" + e.getClick());
    }

    @EventHandler
    public void onGuiClose(GuiCloseEvent e) {
        System.out.println("Close: old:" + e.getOldGui());
    }

    @EventHandler
    public void onGuiOpen(GuiOpenEvent e) {
        System.out.println("Open: gui:" + e.getGui() + ", old:" + e.getOldGui() + ", closeOthers" + e.isCloseOthers());
    }
}
