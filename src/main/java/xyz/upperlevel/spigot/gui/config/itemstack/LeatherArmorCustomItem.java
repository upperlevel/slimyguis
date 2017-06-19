package xyz.upperlevel.spigot.gui.config.itemstack;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.List;
import java.util.Map;

public class LeatherArmorCustomItem extends CustomItem {
    private PlaceholderValue<Color> color;

    public LeatherArmorCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                  PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                                  List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                  PlaceholderValue<Color> color) {
        super(material, data, amount, displayName, lore, flags, enchantments);
        this.color = color;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        LeatherArmorMeta meta = (LeatherArmorMeta) m;
        meta.setColor(color.get(player));
    }

    public static LeatherArmorCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                       PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                       List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                       Map<String, Object> config) {
        PlaceholderValue<Color> color = PlaceholderValue.colorValue((String) config.get("color"));
        return new LeatherArmorCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments,
                color
        );
    }
}
