package xyz.upperlevel.spigot.gui.config.itemstack;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FireworkCustomItem extends CustomItem {
    private List<FireworkEffect> effects;
    private PlaceholderValue<Integer> power;

    public FireworkCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                              PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                              List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                              List<FireworkEffect> effects, PlaceholderValue<Integer> power) {
        super(material, data, amount, displayName, lore, flags, enchantments);
        this.effects = effects;
        this.power = power;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        FireworkMeta meta = (FireworkMeta) m;
        meta.clearEffects();
        meta.addEffects(effects);
        meta.setPower(power.get(player));
    }

    @SuppressWarnings("unchecked")
    public static FireworkCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                     PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                     List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                     Map<String, Object> config) {
        List<FireworkEffect> effects = ((Collection<Map<String, Object>>)config.get("effects"))
                .stream()
                .map(FireworkChargeCustomItem::parse)
                .collect(Collectors.toList());
        PlaceholderValue<Integer> power = PlaceHolderUtil.parseInt(config.get("power"));
        return new FireworkCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments,
                effects, power
        );
    }
}
