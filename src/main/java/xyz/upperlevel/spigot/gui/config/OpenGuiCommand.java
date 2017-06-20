package xyz.upperlevel.spigot.gui.config;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.GuiManager;

import java.util.List;

public class OpenGuiCommand extends BukkitCommand {

    private final Gui gui;

    protected OpenGuiCommand(List<String> names, @NonNull Gui gui) {
        super(names.get(0));
        setAliases(names.subList(1, names.size()));
        this.gui = gui;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player)
            GuiManager.open((Player) commandSender, gui);
        return true;
    }
}
