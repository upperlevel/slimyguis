package xyz.upperlevel.spigot.gui.config.placeholders.managers.customs;

import org.bukkit.entity.Player;

public class PlayerDisplayNamePlaceholder implements CustomPlaceholder {
    @Override
    public String id() {
        return "player_display_name";
    }

    @Override
    public String get(Player player) {
        return player.getDisplayName();
    }
}
