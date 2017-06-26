package xyz.upperlevel.spigot.gui.config.actions;

import com.google.common.collect.ImmutableMap;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import xyz.upperlevel.spigot.gui.Main;
import xyz.upperlevel.spigot.gui.config.Action;
import xyz.upperlevel.spigot.gui.config.ActionType;
import xyz.upperlevel.spigot.gui.config.economy.EconomyManager;

import java.util.Map;

public class VaultGiveActionType extends ActionType<VaultGiveActionType.VaultGiveAction> {
    public static final String ID = "vaultGive";

    public VaultGiveActionType() {
        super(ID);
    }

    @Override
    public VaultGiveAction load(Map<String, Object> config) {
        final double value = ((Number) config.getOrDefault("value", -1)).intValue();
        if(value < 0)
            throw new IllegalArgumentException("Invalid value in vaultGive: " + config);
        return new VaultGiveAction(value);
    }

    @Override
    public Map<String, Object> save(VaultGiveAction action) {
        return ImmutableMap.of("value", action.value);
    }


    public class VaultGiveAction extends Action<VaultGiveAction> {
        private final double value;

        public VaultGiveAction(double value) {
            super(VaultGiveActionType.this);
            this.value = value;
        }

        @Override
        public void run(Player player) {
            final Economy economy = EconomyManager.getEconomy();
            if(economy == null) {
                Main.logger().severe("Cannot find vault's economy!");
                return;
            }
            economy.depositPlayer(player, value);
        }
    }
}