package xyz.upperlevel.spigot.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.config.action.Action;
import xyz.upperlevel.spigot.gui.config.action.BaseActionType;
import xyz.upperlevel.spigot.gui.config.action.Parser;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.Map;

public class BroadcastAction extends Action<BroadcastAction> {
    public static final BroadcastActionType TYPE = new BroadcastActionType();
    @Getter
    private final PlaceholderValue<String> message;
    @Getter
    private final String permission;

    public BroadcastAction(PlaceholderValue<String> message, String permission) {
        super(TYPE);
        this.message = message;
        this.permission = permission;
    }

    @Override
    public void run(Player player) {
        if(permission != null)
            Bukkit.broadcast(message.get(player), permission);
        else
            Bukkit.broadcastMessage(message.get(player));
    }


    public static class BroadcastActionType extends BaseActionType<BroadcastAction> {

        public BroadcastActionType() {
            super("broadcast");
            setParameters(
                    Parameter.of("message", Parser.strValue(), true),
                    Parameter.of("permission", Parser.strValue(), false)
            );
        }

        @Override
        public BroadcastAction create(Map<String, Object> pars) {
            return new BroadcastAction(
                    PlaceHolderUtil.process((String) pars.get("message")),
                    (String) pars.get("permission")
            );
        }

        @Override
        public Map<String, Object> read(BroadcastAction action) {
            return ImmutableMap.of(
                    "message", action.message.toString(),
                    "permission", action.permission
            );
        }
    }
}
