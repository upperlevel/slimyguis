package xyz.upperlevel.spigot.gui.config.placeholders.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderManager;

public class OfficialPlaceholderManager implements PlaceholderManager {
    @Override
    public boolean hasPlaceholders(String str) {
        return PlaceholderAPI.containsPlaceholders(str);
    }

    @Override
    public String apply(Player player, String str) {
        return PlaceholderAPI.setPlaceholders(player, str);
    }
}
