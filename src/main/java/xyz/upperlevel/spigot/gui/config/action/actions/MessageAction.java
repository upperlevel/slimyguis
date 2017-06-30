package xyz.upperlevel.spigot.gui.config.action.actions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.Nms;
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
    @Getter
    private final boolean raw;

    public MessageAction(PlaceholderValue<String> message, boolean raw) {
        super(TYPE);
        this.message = message;
        this.raw = raw;
    }

    @Override
    public void run(Player player) {
        if (!raw)
            player.sendMessage(message.get(player));
        else
            Nms.sendJson(player, message.get(player));
    }


    public static class MessageActionType extends BaseActionType<MessageAction> {

        public MessageActionType() {
            super("message");
            setParameters(
                    Parameter.of("message", Parser.strValue(), true),
                    Parameter.of("raw", Parser.boolValue(), false, false)
            );
        }

        @Override
        public MessageAction create(Map<String, Object> pars) {
            return new MessageAction(
                    PlaceHolderUtil.process((String) pars.get("message")),
                    (Boolean) pars.get("raw")
            );
        }

        @Override
        public Map<String, Object> read(MessageAction action) {
            return ImmutableMap.of(
                    "message", action.message.toString(),
                    "raw", action.raw
            );
        }
    }
}