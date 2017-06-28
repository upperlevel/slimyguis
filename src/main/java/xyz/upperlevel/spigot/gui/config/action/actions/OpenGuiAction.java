package xyz.upperlevel.spigot.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.Gui;
import xyz.upperlevel.spigot.gui.GuiManager;
import xyz.upperlevel.spigot.gui.SlimyGuis;
import xyz.upperlevel.spigot.gui.config.ConfigGuiManager;
import xyz.upperlevel.spigot.gui.config.action.Action;
import xyz.upperlevel.spigot.gui.config.action.BaseActionType;
import xyz.upperlevel.spigot.gui.config.action.Parser;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.Map;

public class OpenGuiAction extends Action<OpenGuiAction> {
    public static final OpenGuiActionType TYPE = new OpenGuiActionType();
    @Getter
    private final PlaceholderValue<String> guiId;

    @Getter
    private final boolean clearStack;

    public OpenGuiAction(PlaceholderValue<String> guiId, boolean clearStack) {
        super(TYPE);
        this.guiId = guiId;
        this.clearStack = clearStack;
    }

    @Override
    public void run(Player player) {
        Gui gui =  ConfigGuiManager.get(guiId.get(player));
        if(gui == null) {
            SlimyGuis.logger().severe("Cannot find gui \"" + guiId + "\"");
            return;
        }

        GuiManager.open(player, gui, clearStack);
    }


    public static class OpenGuiActionType extends BaseActionType<OpenGuiAction> {

        public OpenGuiActionType() {
            super("openGui");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true),
                    Parameter.of("clearStack", Parser.boolValue(), false, false)
            );
        }

        @Override
        public OpenGuiAction create(Map<String, Object> pars) {
            return new OpenGuiAction(
                    PlaceHolderUtil.process((String) pars.get("id")),
                    (Boolean) pars.get("clearStack")
            );
        }

        @Override
        public Map<String, Object> read(OpenGuiAction action) {
            return ImmutableMap.of(
                    "id", action.guiId.toString(),
                    "clearStack", action.clearStack
            );
        }
    }
}
