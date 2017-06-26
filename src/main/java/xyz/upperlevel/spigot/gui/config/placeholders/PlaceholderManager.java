package xyz.upperlevel.spigot.gui.config.placeholders;

import org.bukkit.entity.Player;

public interface PlaceholderManager {
    boolean hasPlaceholders(String str);

    String apply(Player player, String str);
}
