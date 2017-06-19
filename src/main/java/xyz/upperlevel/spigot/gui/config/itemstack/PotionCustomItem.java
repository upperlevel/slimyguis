package xyz.upperlevel.spigot.gui.config.itemstack;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PotionCustomItem extends CustomItem {
    private PotionType type;
    private PlaceholderValue<Color> customColor;
    private List<PotionEffect> customEffects;

    public PotionCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                            PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                            List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                            PotionType type, PlaceholderValue<Color> customColor, List<PotionEffect> customEffects) {
        super(material, data, amount, displayName, lore, flags, enchantments);
        this.type = type;
        this.customColor = customColor;
        this.customEffects = customEffects;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        PotionMeta meta = (PotionMeta) m;
        meta.setBasePotionData(new PotionData(type));
        meta.setColor(customColor.get(player));
        meta.clearCustomEffects();
        for(PotionEffect e : customEffects)
            meta.addCustomEffect(e, true);
    }

    public static PotionCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                     PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                     List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                     Map<String, Object> config) {
        PotionType type = PotionType.valueOf((String) config.get("type"));
        String rawColor = (String) config.get("color");
        PlaceholderValue<Color> customColor = rawColor == null ? null : PlaceholderValue.colorValue(rawColor);
        List<PotionEffect> customEffects = new ArrayList<>();
        Collection<Map<String, Object>> rawEffects = (Collection<Map<String, Object>>) config.get("effects");
        for(Map<String, Object> e : rawEffects)
            customEffects.add(new PotionEffect(e));
        return new PotionCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments,
                type, customColor, customEffects
        );
    }
}
