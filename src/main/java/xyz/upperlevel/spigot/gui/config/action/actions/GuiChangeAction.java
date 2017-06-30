package xyz.upperlevel.spigot.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.GuiManager;
import xyz.upperlevel.spigot.gui.SlimyGuis;
import xyz.upperlevel.spigot.gui.config.action.Action;
import xyz.upperlevel.spigot.gui.config.action.BaseActionType;
import xyz.upperlevel.spigot.gui.config.action.Parser;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.Map;

public class GuiChangeAction extends Action<GuiChangeAction> {
    public static final GuiChangeActionType TYPE = new GuiChangeActionType();
    @Getter
    private final PlaceholderValue<String> guiId;

    public GuiChangeAction(PlaceholderValue<String> guiId) {
        super(TYPE);
        this.guiId = guiId;
    }

    @Override
    public void run(Player player) {
        Gui gui =  GuiManager.get(guiId.get(player));
        if(gui == null) {
            SlimyGuis.logger().severe("Cannot find gui \"" + guiId + "\"");
            return;
        }

        GuiManager.change(player, gui);
    }


    public static class GuiChangeActionType extends BaseActionType<GuiChangeAction> {

        public GuiChangeActionType() {
            super("gui-change");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true)
            );
        }

        @Override
        public GuiChangeAction create(Map<String, Object> pars) {
            return new GuiChangeAction(
                    PlaceHolderUtil.process((String) pars.get("id"))
            );
        }

        @Override
        public Map<String, Object> read(GuiChangeAction action) {
            return ImmutableMap.of(
                    "id", action.guiId.toString()
            );
        }
    }
}