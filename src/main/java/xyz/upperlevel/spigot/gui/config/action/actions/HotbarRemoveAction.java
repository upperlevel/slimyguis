package xyz.upperlevel.spigot.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.Main;
import xyz.upperlevel.spigot.gui.config.ConfigHotbar;
import xyz.upperlevel.spigot.gui.config.action.Action;
import xyz.upperlevel.spigot.gui.config.action.BaseActionType;
import xyz.upperlevel.spigot.gui.config.action.Parser;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.Map;

public class HotbarRemoveAction extends Action<HotbarRemoveAction> {
    public static final HotbarTakeActionType TYPE = new HotbarTakeActionType();
    @Getter
    private final PlaceholderValue<String> id;

    public HotbarRemoveAction(PlaceholderValue<String> id) {
        super(TYPE);
        this.id = id;
    }

    @Override
    public void run(Player player) {
        final String pid = id.get(player);
        final ConfigHotbar hotbar = ConfigHotbar.get(pid);
        if(hotbar == null) {
            Main.logger().severe("Cannot find hotbar \"" + pid + "\"");
            return;
        }
        hotbar.remove(player);
    }


    public static class HotbarTakeActionType extends BaseActionType<HotbarRemoveAction> {

        public HotbarTakeActionType() {
            super("hotbarRemove");
            setParameters(
                    Parameter.of("id", Parser.strValue(), true)//TODO: better Placeholder support
            );
        }

        @Override
        @SuppressWarnings("unchecked")//:(
        public HotbarRemoveAction create(Map<String, Object> pars) {
            return new HotbarRemoveAction(
                    PlaceholderValue.strValue((String) pars.get("id"))
            );
        }

        @Override
        public Map<String, Object> read(HotbarRemoveAction action) {
            return ImmutableMap.of(
                    "id", action.id.toString()
            );
        }
    }
}