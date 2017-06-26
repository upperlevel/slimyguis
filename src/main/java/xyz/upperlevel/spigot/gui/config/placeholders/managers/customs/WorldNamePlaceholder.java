package xyz.upperlevel.spigot.gui.config.placeholders.managers.customs;

import org.bukkit.entity.Player;

public class WorldNamePlaceholder implements CustomPlaceholder {
    @Override
    public String id() {
        return "world_name";
    }

    @Override
    public String get(Player player) {
        return player.getWorld().getName();
    }
}
