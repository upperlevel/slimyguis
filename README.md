# spigot-gui-api
This project is an abstraction of the Spigot Inventories that helps creating games-like GUIs with a stack-like history.
Follows a brief documentation on how to use it.

Here's an example of a Hotbar:

      private static final Hotbar hotbar = SimpleHotbar.of(
                HotbarLink.newLink(Link.consoleCommand("kick <player>"), wool(DyeColor.BLUE, ChatColor.BLUE + "Kick")),
                newLink(p -> p.sendMessage("pong"), wool(DyeColor.ORANGE, ChatColor.GOLD +"Ping")),
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
                                .addLink(change(new RainbowGui()), wool(DyeColor.LIGHT_BLUE, "Rainbow :)")), //Example of the stack-like Gui system
                        wood(TreeSpecies.ACACIA, ChatColor.GRAY + "Tools")
                )
        );
        
The API works a lot with Links, those are methods that get executed when a player clicks.
The HotbarLinks are just Links with an Itemstack for display, they can run commands, methods or even Guis
Here's a simple custom Gui:

    private static class RainbowGui extends BaseGui {
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
                inv.setItem(i, GuiUtils.wool(colors[i], colors[i].name().toLowerCase()));
            return inv;
        }
    }
    
And here's how to open them:
    
    HotbarManager.add(player, hotbar);//Open the hotbar
    //OR
    GuiManager.open(player, new RainbowGui());//Open only the Gui
    
For more documentation visit the Javadocs

