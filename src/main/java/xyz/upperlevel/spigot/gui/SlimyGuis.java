package xyz.upperlevel.spigot.gui;

import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.spigot.gui.commands.impl.GuiCommand;
import xyz.upperlevel.spigot.gui.config.economy.EconomyManager;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.spigot.gui.hotbar.HotbarManager;
import xyz.upperlevel.spigot.gui.script.ScriptSystem;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SlimyGuis extends JavaPlugin implements Listener {
    public static final String SCRIPT_CONFIG = "script_engine.yml";

    private static SlimyGuis instance;

    private ScriptSystem scriptSystem;
    @Getter
    private Metrics metrics;

    public SlimyGuis() {
        if (instance == null)
            instance = this;
    }

    @Override
    public void onEnable() {
        metrics = new Metrics(this);

        getServer().getPluginManager().registerEvents(new GuiEventListener(), this);

        PlaceHolderUtil.tryHook();
        EconomyManager.enable();

        { // script system
            File scriptsConfigFile = new File(getDataFolder(), SCRIPT_CONFIG);
            if (!scriptsConfigFile.exists())
                saveResource(SCRIPT_CONFIG, false);
            scriptSystem = new ScriptSystem(new File(getDataFolder(), "engines"), scriptsConfigFile);
            File scriptsFolder = new File(getDataFolder(), "scripts");
            scriptsFolder.mkdir();
            scriptSystem.loadFolder(scriptsFolder);
        }

        File f = new File(getDataFolder(), "guis");
        logger().log(Level.INFO, "Attempting to load guis at \"" + f.getPath() + "\"");
        GuiManager.loadFolder(f);

        f = new File(getDataFolder(), "hotbars");
        logger().log(Level.INFO, "Attempting to load hotbars at \"" + f.getPath() + "\"...");
        HotbarManager.loadFolder(f);

        HotbarManager.initialize();

        new GuiCommand(null).registerBukkit();

        setupCustomDatas();
    }

    protected void setupCustomDatas() {
        metrics.addCustomChart(new Metrics.AdvancedPie("script_engines_used") {

            @Override
            public HashMap<String, Integer> getValues(HashMap<String, Integer> map) {
                Map<String, Long> counts = scriptSystem.get().values()
                        .stream()
                        .collect(
                                Collectors.groupingBy(s -> s.getEngine().getClass().getSimpleName()
                                                .replaceFirst("ScriptEngine", "")
                                                .toLowerCase(Locale.ENGLISH),
                                        Collectors.counting())
                        );
                for (Map.Entry<String, Long> e : counts.entrySet())
                    map.put(e.getKey(), Math.toIntExact(e.getValue()));
                return map;
            }
        });
    }

    @Override
    public void onDisable() {
        HotbarManager.clearAll();
        GuiManager.closeAll();
    }

    public static SlimyGuis get() {
        return instance;
    }

    public static ScriptSystem getScriptSystem() {
        return instance.scriptSystem;
    }

    public static Logger logger() {
        return instance.getLogger();
    }
}
