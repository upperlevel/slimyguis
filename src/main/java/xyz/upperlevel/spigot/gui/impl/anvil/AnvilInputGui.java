package xyz.upperlevel.spigot.gui.impl.anvil;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Accessors(fluent = true, chain = true)
public class AnvilInputGui implements Gui {
    @Getter
    @Setter
    private String message;

    @Setter
    @Getter
    private BiFunction<Player, String, String> listener = (player, input) -> "Not implemented!";


    @Override
    public void onClick(InventoryClickEvent event) {}//Not working :/

    @Override
    public void print(Player player) {
        new AnvilGUI(
                Main.getInstance(),
                player,
                message,
                this::onAnvilClick);
    }

    private String onAnvilClick(Player player, String input) {
        return listener.apply(player, input);
    }

    @Override
    public void onOpen(Player player) {}

    @Override
    public void onClose(Player player) {}
}
