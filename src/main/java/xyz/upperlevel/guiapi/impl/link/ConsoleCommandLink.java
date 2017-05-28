package xyz.upperlevel.guiapi.impl.link;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ConsoleCommandLink implements Link {
    private final String command;

    @Override
    public void run(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player.getName()));
    }
}
