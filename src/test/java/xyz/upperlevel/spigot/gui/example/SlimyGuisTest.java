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
import xyz.upperlevel.spigot.gui.hotbar.Hotbar;
import xyz.upperlevel.spigot.gui.hotbar.HotbarManager;
import xyz.upperlevel.spigot.gui.hotbar.HotbarView;
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

public class SlimyGuisTest extends JavaPlugin implements Listener {
    //Example of creating strange ItemStacks
    private static final ItemStack BAN_ITEM = wool(DyeColor.RED, "Ban");

    static {
        // Hooks a placeholder handler
        PlaceHolderUtil.tryHook();
    }

    private static final Hotbar hotbar = Hotbar.builder()
            .add(
                    wool(DyeColor.BLUE, ChatColor.BLUE + "Kick"),
                    Link.consoleCommand("kick %player_name%"))
            .add(
                    wool(DyeColor.ORANGE, ChatColor.GOLD + "Ping"),
                    p -> p.sendMessage("pong"))
            .add(
                    wool(DyeColor.WHITE, ChatColor.WHITE + "Help"),
                    Link.command("help"))
            .add(
                    wood(TreeSpecies.ACACIA, ChatColor.GRAY + "Tools"),
                    CustomGui.builder()
                            .title("Tools")
                            .size(9)
                            .add(BAN_ITEM, CustomGui.builder()
                                    // example of confirmation gui!
                                    .title("Sure to ban?")
                                    .size(9)
                                    .set(0, wool(DyeColor.GREEN, ChatColor.GREEN + "YES"), p -> Link.consoleCommand("ban %player_name%"))
                                    .set(8, wool(DyeColor.RED, ChatColor.RED + "NO"), p -> p.sendMessage(ChatColor.RED + p.getName() + " not banned!"))
                                    .build())
                            .add(
                                    wool(DyeColor.YELLOW, "Kick"),
                                    GuiAction.close().and(Link.consoleCommand("kick %player_name%")))
                            .add(
                                    wool(DyeColor.GREEN, "Poke"),
                                    close().and(Link.consoleCommand("say %player_name% is STUPID!")))
                            .add(
                                    wool(DyeColor.LIGHT_BLUE, "Rainbow :)"),
                                    change(new RainbowGui()))
                            .build())
            .add(
                    wool(DyeColor.GREEN, "Rainbow!"),
                    new RainbowGui())
            .add(
                    wool(DyeColor.BLACK, "Dispenser"),
                    new DispenserGui())
            .add(
                    wool(DyeColor.YELLOW, "Check Age"),
                    new AnvilInputGui()
                            .message("Put your age")
                            .listener(InputFilters.filterInt((player, age) -> {
                                GuiManager.close(player);
                                player.sendMessage(age >= 18 ? "You can watch this" : "Go away!");
                            })))
            .add(
                    GuiUtil.setNameAndLores(new ItemStack(Material.ANVIL), "Anvil tests"),
                    CustomGui.builder()
                            .title("Choose anvil")
                            .size(54)
                            .add(
                                    wool(DyeColor.WHITE, "Text"),
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
                                            ))
                            .add(
                                    wool(DyeColor.RED, "Kick"),
                                    new AnvilInputGui()
                                            .message("Put player to kick")
                                            .listener(
                                                    InputFilters.filterPlayer((player, selected) -> {
                                                        GuiManager.change(
                                                                player,
                                                                CustomGui.builder()
                                                                        .title("Kick " + selected.getName() + "?")
                                                                        .size(9)
                                                                        .set(0, wool(DyeColor.GREEN, ChatColor.GREEN + "YES"), p -> p.sendMessage(ChatColor.GREEN + "The player " + p.getName() + " has been kicked!"))
                                                                        .set(8, wool(DyeColor.RED, ChatColor.RED + "NO"), p -> p.sendMessage(ChatColor.RED + "Ok, no."))
                                                                        .build()
                                                        );
                                                    })
                                            ))
                            .add(
                                    wool(DyeColor.GREEN, "Drugs"),
                                    new AnvilInputGui()
                                            .message("Put your age")
                                            .listener(
                                                    InputFilters.filterInt((player, age) -> BookUtil.openPlayer(player, getDrugsBook(age)))
                                            ))
                            .build()
            )
            .add(
                    wool(DyeColor.BLUE, "Deep shit"),
                    CustomGui.builder()
                            .title("deep-test")
                            .size(54)
                            .add(
                                    wool(DyeColor.BROWN, "Deep Rainbow"),
                                    new RainbowGui())
                            .build()
            )
            .build();

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
        //HotbarManager.addIcons adds the Hotbar to the others already applied (if any)
        //were HotbarManager.give would replace the Hotbar without adding it
        //the addIcons-way is more suggested because it can support other plugins
        HotbarView h = HotbarManager.get(event.getPlayer());
        h.addHotbar(hotbar);
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
        }

        @Override
        public void onSetup() {
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
        }

        @Override
        public void onSetup() {
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
