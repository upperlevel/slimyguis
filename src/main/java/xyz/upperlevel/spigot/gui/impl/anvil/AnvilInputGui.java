package xyz.upperlevel.spigot.gui.impl.anvil;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.SlimyGuis;

@RequiredArgsConstructor
@Accessors(fluent = true, chain = true)
public class AnvilInputGui implements Gui {

    @Getter
    @Accessors()
    private String id = null;


    @Getter
    @Setter
    private String message;

    @Setter
    @Getter
    private AnvilGUI.ClickHandler listener = (player, input) -> "Not implemented!";

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(false);
    }//Not working :/

    @Override
    public void show(Player player) {
        new AnvilGUI(
                SlimyGuis.getInstance(),
                player,
                message,
                this::onAnvilClick
        );
    }

    private String onAnvilClick(Player player, String input) {
        //We don't want the GUI to close:
        //Once we return null the AnvilGui will close the GUI and the GuiManager will listen that close event
        //all AFTER that the onClick is executed
        final String str = listener.onClick(player, input);
        return str == null ? "" : str;
    }

    @Override
    public void onOpen(Player player) {}

    @Override
    public void onClose(Player player) {}
}
