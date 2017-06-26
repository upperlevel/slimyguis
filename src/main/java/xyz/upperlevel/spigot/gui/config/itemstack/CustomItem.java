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
import xyz.upperlevel.spigot.gui.Main;
import xyz.upperlevel.spigot.gui.config.InvalidGuiConfigurationException;
import xyz.upperlevel.spigot.gui.config.MessageUtil;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;
import xyz.upperlevel.spigot.gui.config.util.Config;

import java.util.*;
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
    public static CustomItem deserialize(Config config) {
        Material mat = config.getMaterialRequired("material");
        PlaceholderValue<Short> data = PlaceholderValue.shortValue(config.getString("data", "0"));//TODO: better api
        PlaceholderValue<Integer> amount = PlaceHolderUtil.parseInt(config.getString("amount", "1"));


        PlaceholderValue<String> displayName = config.getMessage("name");
        List<PlaceholderValue<String>> lores;
        if(config.has("lore")) {
            lores = ((Collection<String>)config.getCollection("lore"))
                    .stream()
                    .map(MessageUtil::process)
                    .collect(Collectors.toList());
        } else lores = Collections.emptyList();

        List<ItemFlag> flags;
        if(config.has("flags")) {
            flags = ((Collection<String>) config.getCollection("flags"))
                    .stream()
                    .map(ItemFlag::valueOf)
                    .collect(Collectors.toList());
        } else
            flags = Collections.emptyList();

        Map<Enchantment, PlaceholderValue<Integer>> enchantments;

        enchantments = new HashMap<>();
        if (config.has("enchantments")) {
            Collection<String> enchList = (Collection<String>) config.getCollection("enchantments");
            for(String e : enchList) {
                String[] parts = e.split(":");
                if(parts.length != 2)
                    throw new InvalidGuiConfigurationException("Invalid enchantment, correct version: <Enchantment>:<Level>");
                else {
                    Enchantment ench = Enchantment.getByName(parts[0].toUpperCase());
                    if(ench == null)
                        Main.logger().severe("Cannot find enchantment: " + parts[0]);
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
            case SKULL:
                mat = Material.SKULL_ITEM;
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
            case ENCHANTED_BOOK:
                return EnchantedBookCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case FIREWORK:
                return FireworkCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            case FIREWORK_CHARGE:
                return FireworkChargeCustomItem.from(
                        mat, data, amount, displayName, lores, flags, enchantments,
                        config
                );
            default:
                return new CustomItem(mat, data, amount, displayName, lores, flags, enchantments);
        }
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ");
        return '{' + joiner.toString() + '}';
    }

    protected void addStringDetails(StringJoiner joiner) {
        joiner.add("material: " + material);
        joiner.add("data: " + data);
        joiner.add("amount: " + amount);
        joiner.add("displayName: " + displayName);
        joiner.add("lore: " + lore);
        joiner.add("flags: " + flags);
        joiner.add("enchantments: " + enchantments);
    }


}
