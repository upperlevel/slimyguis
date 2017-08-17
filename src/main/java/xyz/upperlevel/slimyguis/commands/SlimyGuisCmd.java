package xyz.upperlevel.slimyguis.commands;

import xyz.upperlevel.uppercore.command.NodeCommand;
import xyz.upperlevel.uppercore.command.WithPermission;

@WithPermission(value = "slimyguis", desc = "Allows you to run main command for SlimyGuis")
public class SlimyGuisCmd extends NodeCommand {
    public SlimyGuisCmd() {
        super("slimyguis");
        register(new SlimyGuisGuiListCmd());
        register(new SlimyGuisGuiOpenCmd());
    }
}
