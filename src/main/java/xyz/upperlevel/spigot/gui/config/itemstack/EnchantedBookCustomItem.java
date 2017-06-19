package xyz.upperlevel.spigot.gui.config.itemstack;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantedBookCustomItem extends CustomItem {
    private Map<Enchantment, PlaceholderValue<Integer>> storedEnchantments;

    public EnchantedBookCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                   PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                                   List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                   Map<Enchantment, PlaceholderValue<Integer>> storedEnchantments) {
        super(material, data, amount, displayName, lore, flags, enchantments);
        this.storedEnchantments = storedEnchantments;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) m;
        for(Map.Entry<Enchantment, PlaceholderValue<Integer>> ench : storedEnchantments.entrySet())
            meta.addStoredEnchant(ench.getKey(), ench.getValue().get(player), true);
    }

    @SuppressWarnings("unchecked")
    public static EnchantedBookCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                              PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                              List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                              Map<String, Object> config) {
        Map<Enchantment, PlaceholderValue<Integer>> storedEnchantments;

        storedEnchantments = new HashMap<>();
        if (config.containsKey("storedEnchantments")) {
            Collection<String> enchList = (Collection<String>) config.get("storedEnchantments");
            for(String e : enchList) {
                String[] parts = e.split(":");
                if(parts.length != 2)
                    Bukkit.getLogger().severe("Invalid book enchantment, correct version: <Enchantment>:<Level>");
                else {
                    Enchantment ench = Enchantment.getByName(parts[0].toUpperCase());
                    if(ench == null)
                        Bukkit.getLogger().severe("Cannot find enchantment: " + parts[0]);
                    else
                        storedEnchantments.put(ench, PlaceholderValue.intValue(parts[1]));
                }
            }
        }
        return new EnchantedBookCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments,
                storedEnchantments
        );
    }
}
