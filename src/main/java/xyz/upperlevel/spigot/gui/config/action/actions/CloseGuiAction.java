package xyz.upperlevel.spigot.gui.config.action.actions;

import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.GuiManager;
import xyz.upperlevel.spigot.gui.config.action.Action;
import xyz.upperlevel.spigot.gui.config.action.BaseActionType;

import java.util.Map;

public class CloseGuiAction extends Action<CloseGuiAction> {
    public static final CloseGuiActionType TYPE = new CloseGuiActionType();

    public CloseGuiAction() {
        super(TYPE);
    }

    @Override
    public void run(Player player) {
        GuiManager.back(player);
    }

    public static class CloseGuiActionType extends BaseActionType<CloseGuiAction> {

        public CloseGuiActionType() {
            super("close-gui");
            setParameters();
        }

        @Override
        public CloseGuiAction create(Map<String, Object> parameters) {
            return new CloseGuiAction();
        }

        @Override
        public Map<String, Object> read(CloseGuiAction action) {
            return null;
        }
    }
}
