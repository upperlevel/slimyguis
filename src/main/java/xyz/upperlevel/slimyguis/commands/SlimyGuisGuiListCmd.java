package xyz.upperlevel.slimyguis.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.upperlevel.slimyguis.SlimyGuis;
import xyz.upperlevel.uppercore.command.Command;
import xyz.upperlevel.uppercore.command.Executor;
import xyz.upperlevel.uppercore.command.WithPermission;
import xyz.upperlevel.uppercore.gui.GuiId;

import java.util.Collection;

/**
 * Lists all guis registered by this plugin.
 */
@WithPermission(value = "list", desc = "Allows you to list all slimy guis loaded")
public class SlimyGuisGuiListCmd extends Command {
    public SlimyGuisGuiListCmd() {
        super("list");
    }

    @Executor
    public void run(CommandSender sender) {
        Collection<GuiId> guis = SlimyGuis.get().getGuiRegistry().get();
        if (guis.isEmpty())
            sender.sendMessage(ChatColor.RED + "No slimy gui registered.");
        else {
            sender.sendMessage(ChatColor.GREEN + "--Found " + guis.size() + " slimy GUIs!--");
            for (GuiId gui : guis)
                sender.sendMessage(ChatColor.AQUA + "- " + gui.getId());
        }
    }
}
