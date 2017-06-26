package xyz.upperlevel.spigot.gui.config.actions;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.config.Action;
import xyz.upperlevel.spigot.gui.config.ActionType;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.HashMap;
import java.util.Map;

public class BroadcastActionType extends ActionType<BroadcastActionType.BroadcastAction> {
    public static final String ID = "broadcast";

    public BroadcastActionType() {
        super(ID);
    }

    @Override
    public BroadcastAction load(Map<String, Object> config) {
        final String message = (String) config.get("message");
        if(message == null)
            throw new IllegalArgumentException("Cannot find field: \"message\"");
        String permission = (String) config.get("permission");
        return new BroadcastAction(PlaceHolderUtil.process(message), permission);
    }

    @Override
    public Map<String, Object> save(BroadcastAction action) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("message", action.getMessage());
        return res;
    }


    public class BroadcastAction extends Action<BroadcastAction> {
        @Getter
        private final PlaceholderValue<String> message;
        @Getter
        private final String permission;

        public BroadcastAction(PlaceholderValue<String> message, String permission) {
            super(BroadcastActionType.this);
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
    }
}
