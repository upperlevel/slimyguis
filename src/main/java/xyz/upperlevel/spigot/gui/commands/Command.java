package xyz.upperlevel.spigot.gui.commands;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.upperlevel.spigot.gui.SlimyGuis;

import java.util.*;

public abstract class Command implements CommandExecutor {

    // infinite args count
    public static final int UNLIMITED = -1;

    @Getter
    private final String name;
    @Getter
    private final Command parent;

    public Command(Command parent, String name) {
        this.parent = parent;
        this.name = name.toLowerCase();
    }

    public abstract String getDescription();

    public String getUsage(CommandSender sender) {
        return "";
    }

    public String getAbsUsage(CommandSender sender) {
        StringJoiner joiner = new StringJoiner(" ");
        List<String> family = new ArrayList<>();
        {//Get family
            Command current = this;
            do {
                family.add(current.getName());
                current = current.parent;
            } while(current != null);

            ListIterator<String> i = family.listIterator(family.size());
            while (i.hasPrevious())
                joiner.add(i.previous());
        }
        return "usage: /" + joiner.toString() + " " + getUsage(sender);
    }

    public boolean isAlias(String alias) {
        for (String alter : getAliases())
            if (alter.equalsIgnoreCase(alias))
                return true;
        return false;
    }

    public List<String> getAliases() {
        return Collections.emptyList();
    }

    public Sender getSender() {
        return Sender.ALL;
    }

    public int getArgumentsCount() {
        return 0;
    }

    public String getPermission() {
        return null;
    }

    public void execute(CommandSender sender, List<String> args) {
        final String permission = getPermission();
        if(permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage("You don't have permission to execute this!");
            return;
        }
        if (!getSender().isCorrect(sender)) {
            sender.sendMessage("Invalid command sender. You must be a: " + getSender());
            return;
        }
        if (getArgumentsCount() != UNLIMITED && getArgumentsCount() != args.size()) {
            sender.sendMessage("Invalid args count, " + getAbsUsage(sender));
            return;
        }
        run(sender, args);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command c, String alias, String[] args) {
        execute(sender, Arrays.asList(args));
        return true;
    }

    public abstract void run(CommandSender sender, List<String> args);

    public void registerBukkit() {
        PluginCommand command = Bukkit.getPluginCommand(name);
        if(command == null) {
            SlimyGuis.logger().severe("Cannot register command \"" + name + "\"");
            return;
        }
        command.setExecutor(this);
        command.setAliases(getAliases());
        command.setDescription(getDescription());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
