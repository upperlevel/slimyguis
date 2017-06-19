package xyz.upperlevel.spigot.gui.config.itemstack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class CustomItem {
    private Material material;
    private PlaceholderValue<Short> data;
    private PlaceholderValue<Integer> amount;
    //Meta
    private PlaceholderValue<String> displayName;
    private List<PlaceholderValue<String>> lore;
    private List<ItemFlag> flags;
    private Map<Enchantment, PlaceholderValue<Integer>> enchantments;

    public ItemStack toItemStack(Player player) {
        ItemStack item = new ItemStack(material, amount.get(player), data.get(player));
        ItemMeta meta = item.getItemMeta();

        processMeta(player, meta);

        item.setItemMeta(meta);
        return item;
    }

    public void processMeta(Player player, ItemMeta meta) {
        meta.setDisplayName(displayName.get(player));
        meta.setLore(lore.stream().map(m -> m.get(player)).collect(Collectors.toList()));
        meta.addItemFlags(flags.toArray(new ItemFlag[0]));
        for(Map.Entry<Enchantment, PlaceholderValue<Integer>> ench : enchantments.entrySet())
            meta.addEnchant(ench.getKey(), ench.getValue().get(player), true);
    }

    @SuppressWarnings("unchecked")
    public static CustomItem deserialzie(Map<String, Object> config) {
        Material mat = (Material) config.get("material");
        PlaceholderValue<Short> data = PlaceHolderUtil.parseShort(config.getOrDefault("data", 0));
        if(data == null)
            Bukkit.getLogger().severe("Illegal value into CustomItem's \"data\" tag");
        PlaceholderValue<Integer> amount = PlaceHolderUtil.parseInt(config.getOrDefault("amount", 1));
        if(amount == null)
            Bukkit.getLogger().severe("Illegal value into CustomItem's \"amount\" tag");


        PlaceholderValue<String> displayName = PlaceholderValue.strValue((String) config.get("name"));
        List<PlaceholderValue<String>> lores = ((Collection<String>)config.get("lores"))
                .stream()
                .map(PlaceholderValue::strValue)
                .collect(Collectors.toList());

        List<ItemFlag> flags = ((Collection<String>)config.get("flags"))
                .stream()
                .map(ItemFlag::valueOf)
                .collect(Collectors.toList());

        Map<Enchantment, PlaceholderValue<Integer>> enchantments;

        enchantments = new HashMap<>();
        if (config.containsKey("enchantments")) {
            Collection<String> enchList = (Collection<String>) config.get("enchantments");
            for(String e : enchList) {
                String[] parts = e.split(":");
                if(parts.length != 2)
                    Bukkit.getLogger().severe("Invalid enchantment, correct version: <Enchantment>:<Level>");
                else {
                    Enchantment ench = Enchantment.getByName(parts[0].toUpperCase());
                    if(ench == null)
                        Bukkit.getLogger().severe("Cannot find enchantment: " + parts[0]);
                    else
                        enchantments.put(ench, PlaceholderValue.intValue(parts[1]));
                }
            }
        }


        switch (mat) {
            case BANNER:
                return BannerCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case SKULL_ITEM:
                return SkullCustomItem.from(
                    mat, data, amount, displayName, lores, flags, enchantments,
                    config
                );
            case LEATHER_BOOTS:
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
            case LEATHER_LEGGINGS:
                return LeatherArmorCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case MAP:
                return MapCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case POTION:
            case LINGERING_POTION:
            case SPLASH_POTION:
                return PotionCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case MONSTER_EGG:
                return SpawnEggCustomItem.from(
                    mat, data, amount, displayName, lores, flags, enchantments,
                    config
                );
            default:
                return new CustomItem(mat, data, amount, displayName, lores, flags, enchantments);
        }
    }



}
