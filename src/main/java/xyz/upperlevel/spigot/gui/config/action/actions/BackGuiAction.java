package xyz.upperlevel.spigot.gui.config.action.actions;

import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.GuiManager;
import xyz.upperlevel.spigot.gui.config.action.Action;
import xyz.upperlevel.spigot.gui.config.action.BaseActionType;

import java.util.Map;

public class BackGuiAction extends Action<BackGuiAction> {
    public static final BackGuiActionType TYPE = new BackGuiActionType();

    public BackGuiAction() {
        super(TYPE);
    }

    @Override
    public void run(Player player) {
        GuiManager.back(player);
    }

    public static class BackGuiActionType extends BaseActionType<BackGuiAction> {

        public BackGuiActionType() {
            super("back-gui");
            setParameters();
        }

        @Override
        public BackGuiAction create(Map<String, Object> parameters) {
            return new BackGuiAction();
        }

        @Override
        public Map<String, Object> read(BackGuiAction action) {
            return null;
        }
    }
}

