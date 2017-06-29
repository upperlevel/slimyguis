package xyz.upperlevel.spigot.gui.commands.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.GuiManager;
import xyz.upperlevel.spigot.gui.commands.Command;
import xyz.upperlevel.spigot.gui.commands.Sender;

import java.util.List;

public class OpenCommand extends Command {
    public OpenCommand(Command parent) {
        super(parent, "open");
    }

    @Override
    public String getDescription() {
        return "Opens the gui with the specified id";
    }

    @Override
    public String getUsage(CommandSender sender) {
        return "<id>";
    }

    @Override
    public Sender getSender() {
        return Sender.PLAYER;
    }

    @Override
    public int getArgumentsCount() {
        return 1;
    }

    @Override
    public String getPermission() {
        return "slimyguis.command.open";
    }

    @Override
    public void run(CommandSender sender, List<String> args) {
        String id = args.get(0);
        Gui gui = GuiManager.get(id);
        if(gui == null)
            sender.sendMessage("Cannot find gui \"" + id + "\"");
        else
            GuiManager.open((Player)sender, gui);
    }
}
