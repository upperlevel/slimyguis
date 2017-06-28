package xyz.upperlevel.spigot.gui.config.action.actions;


import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.config.action.Action;
import xyz.upperlevel.spigot.gui.config.action.BaseActionType;
import xyz.upperlevel.spigot.gui.config.action.Parser;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RequireAction extends Action<RequireAction> {
    public static final RequireActionType TYPE = new RequireActionType();
    @Getter
    private final String permission;
    @Getter
    private final List<Action> actions;
    @Getter
    private final List<Action> fail;

    public RequireAction(String permission, List<Action> actions, List<Action> fail) {
        super(TYPE);
        this.permission = permission;
        this.actions = actions;
        this.fail = fail;
    }

    @Override
    public void run(Player player) {
        if(permission != null && player.hasPermission(permission))
            for(Action a : actions)
                a.run(player);
        else
            for(Action a : fail)
                a.run(player);
    }


    public static class RequireActionType extends BaseActionType<RequireAction> {

        public RequireActionType() {
            super("require");
            setParameters(
                    Parameter.of("permission", Parser.strValue(), false),
                    Parameter.of("actions", Parser.actionsValue(), true),
                    Parameter.of("fail", Parser.actionsValue(), Collections.emptyList(),false)
            );
        }

        @Override
        @SuppressWarnings("unchecked")//Come oooon
        public RequireAction create(Map<String, Object> pars) {
            return new RequireAction(
                    (String) pars.get("permission"),
                    (List<Action>)pars.get("actions"),
                    (List<Action>)pars.get("fail")
            );
        }

        @Override
        public Map<String, Object> read(RequireAction action) {
            return ImmutableMap.of(
                    "permission", action.permission,
                    "action", action.actions,
                    "fail", action.fail
            );
        }
    }
}

