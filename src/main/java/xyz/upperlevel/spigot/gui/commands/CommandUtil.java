package xyz.upperlevel.spigot.gui.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import xyz.upperlevel.spigot.gui.config.Nms;

public final class CommandUtil {
    private static final CommandMap commands = Nms.getCommandMap(Bukkit.getServer());

    public static void register(Command command) {
        commands.register(command.getName(), command);
    }

    private CommandUtil(){}
}
