package xyz.upperlevel.slimyguis.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import xyz.upperlevel.slimyguis.SlimyGuis;
import xyz.upperlevel.uppercore.command.*;
import xyz.upperlevel.uppercore.gui.GuiId;

/**
 * This commands opens a gui registered by this plugin.
 */
@WithPermission(value = "open", desc = "Allows you to open a slimy gui for yourself")
public class SlimyGuisGuiOpenCmd extends Command {
    private Permission otherPerm;

    public SlimyGuisGuiOpenCmd() {
        super("open");
    }

    @Override
    public void calcPermissions() {
        super.calcPermissions();
        Permission def = getPermission();
        if (def != null) {
            otherPerm = new Permission(
                    def.getName() + ".other",
                    "Allows you to open guis to other players",
                    PermissionDefault.OP);
            if (getParent() != null)
                otherPerm.addParent(getParent().getAnyPerm(), true);
        } else otherPerm = null;
    }

    @Override
    public void registerPermissions(PluginManager manager) {
        super.registerPermissions(manager);
        if (otherPerm != null)
            manager.addPermission(otherPerm);
    }

    @Executor
    public void run(CommandSender sender, @Argument("gui") String gui, @Argument("player") @Optional Player player) {
        if (otherPerm != null && player != null && !sender.hasPermission(otherPerm)) {
            sender.sendMessage(ChatColor.RED + "You can't open slimy guis to other players.");
            return;
        }
        if (!(sender instanceof Player)) {
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "You must specify a player.");
                return;
            }
        } else if (player == null)
            player = (Player) sender;
        GuiId gId = SlimyGuis.get().getGuiRegistry().get(gui);
        if (gId == null) {
            sender.sendMessage(ChatColor.RED + "No slimy gui found called: \"" + gui + "\".");
            return;
        }
        gId.get().show(player);
    }
}
