package xyz.upperlevel.spigot.gui.config.actions;


import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.config.Action;
import xyz.upperlevel.spigot.gui.config.ActionType;

import java.util.*;

public class RequireActionType extends ActionType<RequireActionType.RequireAction> {
    public static final String ID = "require";

    public RequireActionType() {
        super(ID);
    }

    @Override
    public RequireAction load(Map<String, Object> config) {
        String permission = (String) config.get("permission");
        List<Action> actions = ActionType.deserialize((Collection<Map<String, Object>>) config.getOrDefault("actions", Collections.emptyList()));
        List<Action> fail = ActionType.deserialize((Collection<Map<String, Object>>) config.getOrDefault("fail", Collections.emptyList()));
        return new RequireAction(permission, actions, fail);
    }

    @Override
    public Map<String, Object> save(RequireAction action) {
        return new HashMap<>();
    }

    public class RequireAction extends Action<RequireAction> {
        private String permission;
        private List<Action> actions;
        private List<Action> fail;

        public RequireAction(String permission, List<Action> actions, List<Action> fail) {
            super(RequireActionType.this);
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
    }
}
