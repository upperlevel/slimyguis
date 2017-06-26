package xyz.upperlevel.spigot.gui.config.placeholders.managers.customs;

import org.bukkit.entity.Player;

public class PlayerFoodPlaceholder implements CustomPlaceholder {
    @Override
    public String id() {
        return "player_food";
    }

    @Override
    public String get(Player player) {
        return Float.toString(player.getFoodLevel());
    }
}
