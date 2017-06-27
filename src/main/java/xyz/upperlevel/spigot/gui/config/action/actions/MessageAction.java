package xyz.upperlevel.spigot.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.config.action.Action;
import xyz.upperlevel.spigot.gui.config.action.BaseActionType;
import xyz.upperlevel.spigot.gui.config.action.Parser;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.Map;

public class MessageAction extends Action<MessageAction> {
    public static final MessageActionType TYPE = new MessageActionType();
    @Getter
    private final PlaceholderValue<String> message;

    public MessageAction(PlaceholderValue<String> message) {
        super(TYPE);
        this.message = message;
    }

    @Override
    public void run(Player player) {
        player.sendMessage(message.get(player));
    }


    public static class MessageActionType extends BaseActionType<MessageAction> {

        public MessageActionType() {
            super("message");
            setParameters(
                    Parameter.of("message", Parser.strValue(), true)
            );
        }

        @Override
        public MessageAction create(Map<String, Object> pars) {
            return new MessageAction(
                    PlaceHolderUtil.process((String) pars.get("message"))
            );
        }

        @Override
        public Map<String, Object> read(MessageAction action) {
            return ImmutableMap.of(
                    "message", action.message.toString()
            );
        }
    }
}