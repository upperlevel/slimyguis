package xyz.upperlevel.spigot.gui.config.actions;

import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.GuiManager;
import xyz.upperlevel.spigot.gui.config.Action;
import xyz.upperlevel.spigot.gui.config.ActionType;

import java.util.HashMap;
import java.util.Map;

public class BackGuiActionType extends ActionType<BackGuiActionType.BackGuiAction> {
    public static final String ID = "backGui";

    public BackGuiActionType() {
        super(ID);
    }

    @Override
    public BackGuiAction load(Map<String, Object> config) {
        return new BackGuiAction();
    }

    @Override
    public Map<String, Object> save(BackGuiAction action) {
       return new HashMap<>();
    }


    public class BackGuiAction extends Action<BackGuiAction> {

        public BackGuiAction() {
            super(BackGuiActionType.this);
        }

        @Override
        public void run(Player player) {
            GuiManager.back(player);
        }
    }
}

