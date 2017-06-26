package xyz.upperlevel.spigot.gui.config.placeholders.managers.customs;

import org.bukkit.entity.Player;

public class WorldSeedPlaceholder implements CustomPlaceholder {
    @Override
    public String id() {
        return "world_seed";
    }

    @Override
    public String get(Player player) {
        return Long.toString(player.getWorld().getSeed());
    }
}
