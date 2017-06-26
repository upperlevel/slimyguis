package xyz.upperlevel.spigot.gui.config.actions;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.GuiManager;
import xyz.upperlevel.spigot.gui.Main;
import xyz.upperlevel.spigot.gui.config.Action;
import xyz.upperlevel.spigot.gui.config.ActionType;
import xyz.upperlevel.spigot.gui.config.ConfigGuiManager;

import java.util.HashMap;
import java.util.Map;

public class OpenGuiActionType extends ActionType<OpenGuiActionType.OpenGuiAction>{
    public static final String ID = "openGui";

    public OpenGuiActionType() {
        super(ID);
    }

    @Override
    public OpenGuiAction load(Map<String, Object> config) {
        final String guiId = (String) config.get("id");
        if(guiId == null)
            throw new IllegalArgumentException("Cannot find field: \"id\" in " + config);
        String clearStack = (String) config.get("clearStack");
        return new OpenGuiAction(guiId, clearStack != null && Boolean.parseBoolean(clearStack));
    }

    @Override
    public Map<String, Object> save(OpenGuiAction action) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("id", action.getGuiId());
        return res;
    }


    public class OpenGuiAction extends Action<OpenGuiAction> {
        @Getter
        private final String guiId;

        @Getter
        private final boolean clearStack;

        public OpenGuiAction(String guiId, boolean clearStack) {
            super(OpenGuiActionType.this);
            this.guiId = guiId;
            this.clearStack = clearStack;
        }

        @Override
        public void run(Player player) {
            Gui gui =  ConfigGuiManager.get(guiId);
            if(gui == null) {
                Main.logger().severe("Cannot find gui \"" + ID + "\"");
                return;
            }

            GuiManager.open(player, gui, clearStack);
        }
    }
}
