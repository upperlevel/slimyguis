package xyz.upperlevel.spigot.gui.config.actions;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.config.Action;
import xyz.upperlevel.spigot.gui.config.ActionType;
import xyz.upperlevel.spigot.gui.config.MessageUtil;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.HashMap;
import java.util.Map;

public class MessageActionType extends ActionType<MessageActionType.MessageAction> {
    public static final String ID = "message";

    public MessageActionType() {
        super(ID);
    }

    @Override
    public MessageAction load(Map<String, Object> config) {
        final PlaceholderValue<String> message = MessageUtil.process((String) config.get("message"));
        if(message == null)
            throw new IllegalArgumentException("Cannot find field: \"message\"");
        return new MessageAction(message);
    }

    @Override
    public Map<String, Object> save(MessageAction action) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("message", action.getMessage());
        return res;
    }


    public class MessageAction extends Action<MessageAction> {
        @Getter
        private final PlaceholderValue<String> message;

        public MessageAction(PlaceholderValue<String> message) {
            super(MessageActionType.this);
            this.message = message;
        }

        @Override
        public void run(Player player) {
            player.sendMessage(message.get(player));
        }
    }
}