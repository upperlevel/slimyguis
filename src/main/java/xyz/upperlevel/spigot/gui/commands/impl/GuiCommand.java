package xyz.upperlevel.spigot.gui.commands.impl;

import xyz.upperlevel.spigot.gui.commands.Command;
import xyz.upperlevel.spigot.gui.commands.NodeCommand;

import java.util.Arrays;
import java.util.List;

public class GuiCommand extends NodeCommand {
    public GuiCommand(Command parent) {
        super(parent, "gui");
        addCommand(new OpenCommand(this));
        addCommand(new ListCommand(this));
        addCommand(new ScriptsCommand(this));
    }

    @Override
    public String getDescription() {
        return "Commands for SlimyGuis.";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList(
                "slimygui"
        );
    }
}
