package xyz.upperlevel.spigot.gui.config.itemstack;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceholderValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static xyz.upperlevel.spigot.gui.config.ConfigUtils.parseColor;
import static xyz.upperlevel.spigot.gui.config.ConfigUtils.parseDye;

public class BannerCustomItem extends CustomItem {
    private DyeColor baseColor;
    private List<Pattern> patterns;

    public BannerCustomItem(Material material, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                            PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lore,
                            List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                            DyeColor baseColor, List<Pattern> patterns) {
        super(material, data, amount, displayName, lore, flags, enchantments);
        this.baseColor = baseColor;
        this.patterns = patterns;
    }

    @Override
    public void processMeta(Player player, ItemMeta m) {
        super.processMeta(player, m);
        BannerMeta meta = (BannerMeta) m;
        meta.setBaseColor(baseColor);
        meta.setPatterns(patterns);
    }

    @SuppressWarnings("unchecked")
    public static BannerCustomItem from(Material mat, PlaceholderValue<Short> data, PlaceholderValue<Integer> amount,
                                  PlaceholderValue<String> displayName, List<PlaceholderValue<String>> lores,
                                  List<ItemFlag> flags, Map<Enchantment, PlaceholderValue<Integer>> enchantments,
                                  Map<String, Object> config) {
        DyeColor baseColor = parseDye((String) config.get("color"));
        Collection<Map<String, Object>> rawPatterns = (Collection<Map<String, Object>>) config.get("patterns");
        List<Pattern> patterns = new ArrayList<>();
        for(Map<String, Object> p : rawPatterns) {
            DyeColor color = parseDye((String) p.get("color"));
            PatternType type = PatternType.getByIdentifier((String) p.get("pattern"));
            if(type == null) {
                Bukkit.getLogger().severe("Cannot find pattern identifier \"" + p.get("pattern") + "\"");
                type = PatternType.BASE;
            }
            patterns.add(new Pattern(color, type));
        }
        return new BannerCustomItem(
                mat, data, amount, displayName, lores, flags, enchantments,
                baseColor, patterns
        );
    }
}
