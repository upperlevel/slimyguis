package xyz.upperlevel.spigot.gui.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public abstract class NodeCommand extends Command {

    private final Set<Command> commands = new HashSet<>();

    public NodeCommand(Command parent, String name) {
        super(parent, name);
    }

    public void addCommand(Command cmd) {
        commands.add(cmd);
    }

    public Command getCommand(String name) {
        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(name) || command.isAlias(name))
                return command;
        }
        return null;
    }

    public boolean removeCommand(Command cmd) {
        return commands.remove(cmd);
    }

    @Override
    public int getArgumentsCount() {
        return UNLIMITED;
    }

    @Override
    public void run(CommandSender sender, List<String> args) {
        if (args.size() == 0) {
            sender.sendMessage(getAbsUsage(sender));
            return;
        }
        Command command = getCommand(args.get(0).toLowerCase());
        if (command == null) {
            sender.sendMessage(getAbsUsage(sender));
            return;
        }
        command.execute(sender, args.subList(1, args.size()));
    }

    @Override
    public String getUsage(CommandSender sender) {
        StringJoiner joiner = new StringJoiner("|");
        for(Command c : commands) {
            final String permission = c.getPermission();
            if (permission == null || sender.hasPermission(permission))
                joiner.add(c.getName());
        }
        return "<" + joiner.toString() +  ">";
    }

    public Set<Command> getCommands() {
        return commands;
    }
}
