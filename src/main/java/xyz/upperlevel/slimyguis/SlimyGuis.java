package xyz.upperlevel.slimyguis;

import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.gui.commands.GuiCommand;
import xyz.upperlevel.uppercore.gui.GuiEventListener;
import xyz.upperlevel.uppercore.gui.GuiManager;
import xyz.upperlevel.uppercore.gui.GuiRegistry;
import xyz.upperlevel.uppercore.gui.config.economy.EconomyManager;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarManager;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.script.ScriptSystem;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Getter
public class SlimyGuis extends JavaPlugin implements Listener {

    public static final String SCRIPT_CONFIG = "script_engine.yml";

    private static SlimyGuis instance;

    private Metrics metrics;
    private GuiRegistry guiRegistry;
    private HotbarRegistry hotbarRegistry;
    private ScriptSystem scriptSystem;

    public SlimyGuis() {
        instance = this;
    }

    @Override
    public void onEnable() {
        metrics = new Metrics(this);

        getServer().getPluginManager().registerEvents(new GuiEventListener(), this);

        PlaceholderUtil.tryHook();
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

        guiRegistry = new GuiRegistry(this);
        hotbarRegistry = new HotbarRegistry(this);

        File folder;
        folder = new File(getDataFolder(), "guis");
        logger().info("Attempting to load guis at \"" + folder.getPath() + "\"");
        guiRegistry.loadFolder(folder);

        folder = new File(getDataFolder(), "hotbars");
        logger().info("Attempting to load hotbars at \"" + folder.getPath() + "\"");
        hotbarRegistry.loadFolder(folder);

        new GuiCommand().subscribe();

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
