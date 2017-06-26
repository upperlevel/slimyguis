package xyz.upperlevel.spigot.gui.config.actions;

import com.google.common.collect.ImmutableMap;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.Main;
import xyz.upperlevel.spigot.gui.config.Action;
import xyz.upperlevel.spigot.gui.config.ActionType;
import xyz.upperlevel.spigot.gui.config.economy.EconomyManager;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VaultTakeActionType extends ActionType<VaultTakeActionType.VaultTakeAction> {
    public static final String ID = "vaultTake";

    public VaultTakeActionType() {
        super(ID);
    }

    @Override
    public VaultTakeAction load(Map<String, Object> config) {
        final double value = ((Number) config.getOrDefault("value", -1)).intValue();
        if(value < 0)
            throw new IllegalArgumentException("Invalid value in vaultTake: " + config);
        List<Action> actions = ActionType.deserialize((Collection<Map<String, Object>>) config.getOrDefault("actions", Collections.emptyList()));
        List<Action> fail = ActionType.deserialize((Collection<Map<String, Object>>) config.getOrDefault("fail", Collections.emptyList()));
        return new VaultTakeAction(value, actions, fail);
    }

    @Override
    public Map<String, Object> save(VaultTakeAction action) {
        return ImmutableMap.of("value", action.value);
    }


    public class VaultTakeAction extends Action<VaultTakeAction> {
        private final double value;
        private List<Action> actions;
        private List<Action> fail;

        public VaultTakeAction(double value, List<Action> actions, List<Action> fail) {
            super(VaultTakeActionType.this);
            this.value = value;
            this.actions = actions;
            this.fail = fail;
        }

        @Override
        public void run(Player player) {
            final Economy economy = EconomyManager.getEconomy();
            if(economy == null) {
                Main.logger().severe("Cannot find vault's economy!");
                return;
            }
            if(economy.withdrawPlayer(player, value).transactionSuccess()) {
                for(Action a : actions)
                    a.run(player);
            } else
                for(Action a : fail)
                    a.run(player);
        }
    }
}