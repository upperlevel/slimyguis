package xyz.upperlevel.spigot.gui.link.impl;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.link.Link;

@RequiredArgsConstructor
public class CommandLink implements Link {
    private final String command;

    @Override
    public void run(Player player) {
        Bukkit.dispatchCommand(player, command.replace("<player>", player.getName()));
    }
}
