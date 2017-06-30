package xyz.upperlevel.spigot.gui.commands.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.upperlevel.spigot.gui.SlimyGuis;
import xyz.upperlevel.spigot.gui.commands.Command;

import java.util.List;
import java.util.Set;

public class ScriptsCommand extends Command{
    public ScriptsCommand(Command parent) {
        super(parent, "scripts");
    }

    @Override
    public String getDescription() {
        return "Sends a list of all loaded scripts.";
    }

    @Override
    public String getPermission() {
        return "slimyguis.command.scripts";
    }

    @Override
    public void run(CommandSender sender, List<String> args) {
        Set<String> scripts = SlimyGuis.getScriptSystem().get().keySet();
        int size = scripts.size();
        if(size > 0) {
            sender.sendMessage(ChatColor.GREEN + "Displaying " + size + " scripts:");
            for(String script : scripts)
                sender.sendMessage(ChatColor.GREEN + "- " + script);
        } else
            sender.sendMessage(ChatColor.RED + "No script to registered.");
    }
}
