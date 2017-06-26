package xyz.upperlevel.spigot.gui.config.actions;

import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.GuiManager;
import xyz.upperlevel.spigot.gui.config.Action;
import xyz.upperlevel.spigot.gui.config.ActionType;

import java.util.HashMap;
import java.util.Map;

public class CloseGuiActionType extends ActionType<CloseGuiActionType.CloseGuiAction> {
    public static final String ID = "closeGui";

    public CloseGuiActionType() {
        super(ID);
    }

    @Override
    public CloseGuiAction load(Map<String, Object> config) {
        return new CloseGuiAction();
    }

    @Override
    public Map<String, Object> save(CloseGuiAction action) {
        return new HashMap<>();
    }


    public class CloseGuiAction extends Action<CloseGuiAction> {
        public CloseGuiAction() {
            super(CloseGuiActionType.this);
        }

        @Override
        public void run(Player player) {
            GuiManager.close(player);
        }
    }
}
