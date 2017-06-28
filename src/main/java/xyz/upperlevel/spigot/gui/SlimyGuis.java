package xyz.upperlevel.spigot.gui;

import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.spigot.gui.config.ConfigHotbar;
import xyz.upperlevel.spigot.gui.config.economy.EconomyManager;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.spigot.gui.hotbar.HotbarManager;
import xyz.upperlevel.spigot.gui.script.Script;
import xyz.upperlevel.spigot.gui.script.ScriptSystem;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SlimyGuis extends JavaPlugin implements Listener {
    public static final String SCRIPT_CONFIG = "script_engine.yml";
    @Getter
    private static SlimyGuis instance;

    private ScriptSystem scriptSystem;
    @Getter
    private Metrics metrics;

    public SlimyGuis() {
        if(instance == null)
            instance = this;
    }

    @Override
    public void onEnable() {
        metrics = new Metrics(this);

        getServer().getPluginManager().registerEvents(new GuiEventListener(), this);

        PlaceHolderUtil.tryHook();
        EconomyManager.enable();

        {//Script system
            File scriptsConfigFile = new File(getDataFolder(), SCRIPT_CONFIG);
            if(!scriptsConfigFile.exists())
                saveResource(SCRIPT_CONFIG, false);
            scriptSystem = new ScriptSystem(new File(getDataFolder(), "engines"), scriptsConfigFile);
            File scriptsFolder = new File(getDataFolder(), "scripts");
            scriptsFolder.mkdir();
            scriptSystem.loadFolder(scriptsFolder);
        }

        GuiManager.loadFolder(new File(getDataFolder(), "guis"));
        ConfigHotbar.onLoad(new File(getDataFolder(), "hotbars"));

        setupCustomDatas();
    }

    protected void setupCustomDatas() {
        metrics.addCustomChart(new Metrics.AdvancedPie("script_engines_used") {

            @Override
            public HashMap<String, Integer> getValues(HashMap<String, Integer> map) {
                Map<String, Long> counts = scriptSystem.get()
                        .stream()
                        .collect(Collectors.groupingBy(s -> s.getEngine().getClass().getSimpleName(), Collectors.counting()));
                for(Map.Entry<String, Long> e : counts.entrySet())
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

    public static ScriptSystem getScriptSystem() {
        return instance.scriptSystem;
    }

    public static Logger logger() {
        return instance.getLogger();
    }
}
