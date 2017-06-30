package xyz.upperlevel.spigot.gui.commands.impl;

import org.bukkit.command.CommandSender;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.GuiManager;
import xyz.upperlevel.spigot.gui.commands.Command;

import java.util.Collection;
import java.util.List;

public class ListCommand extends Command {
    public ListCommand(Command parent) {
        super(parent, "list");
    }

    @Override
    public String getDescription() {
        return "Lists all the registered guis.";
    }

    @Override
    public String getPermission() {
        return "slimyguis.command.list";
    }

    @Override
    public void run(CommandSender sender, List<String> args) {
        Collection<Gui> guis = GuiManager.getGuis();
        int size = guis.size();
        if(size > 0) {
            sender.sendMessage("Displaying " + size + " guis:");
            for(Gui gui : guis)
                sender.sendMessage("- " + gui.getId());
        } else
            sender.sendMessage("No gui to registered");
    }
}
