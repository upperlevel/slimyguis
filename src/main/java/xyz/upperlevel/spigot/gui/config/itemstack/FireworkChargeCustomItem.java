package xyz.upperlevel.spigot.gui.config.itemstack;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.spigot.gui.config.ColorUtil;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FireworkChargeCustomItem extends CustomItem {
    private FireworkEffect effect;

    public FireworkChargeCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                    PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                                    List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                    FireworkEffect effect) {
        super(material, data, amount, displayName, lore, flags, enchantments);
        this.effect = effect;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        FireworkEffectMeta meta = (FireworkEffectMeta) m;
        meta.setEffect(effect);
    }

    public static FireworkChargeCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                              PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                              List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                              Map<String, Object> config) {
        FireworkEffect effect = parse((Map<String, Object>) config.get("effect"));
        return new FireworkChargeCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments,
                effect
        );
    }

    @SuppressWarnings("unchecked")
    public static FireworkEffect parse(Map<String, Object> config) {
        boolean flicker = (boolean) config.get("flicker");
        boolean trail = (boolean) config.get("trail");
        List<Color> colors = ((Collection<String>)config.get("colors"))
                .stream()
                .map(ColorUtil::parseColor)
                .collect(Collectors.toList());
        List<Color> fadeColors = ((Collection<String>)config.get("fadeColors"))
                .stream()
                .map(ColorUtil::parseColor)
                .collect(Collectors.toList());
        FireworkEffect.Type type = FireworkEffect.Type.valueOf(((String) config.get("type")).toUpperCase());
        return FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .withColor(colors)
                .withFade(fadeColors)
                .with(type)
                .build();
    }
}
