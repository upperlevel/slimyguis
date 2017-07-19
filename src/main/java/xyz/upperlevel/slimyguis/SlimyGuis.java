package xyz.upperlevel.slimyguis;

import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.uppercore.gui.GuiEventListener;
import xyz.upperlevel.uppercore.gui.GuiRegistry;
import xyz.upperlevel.uppercore.gui.GuiSystem;
import xyz.upperlevel.uppercore.gui.commands.GuiCommand;
import xyz.upperlevel.uppercore.gui.config.economy.EconomyManager;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarSystem;
import xyz.upperlevel.uppercore.gui.hotbar.HotbarRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.scoreboard.ScoreboardRegistry;
import xyz.upperlevel.uppercore.scoreboard.ScoreboardSystem;
import xyz.upperlevel.uppercore.script.ScriptRegistry;
import xyz.upperlevel.uppercore.script.ScriptSystem;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class SlimyGuis extends JavaPlugin implements Listener {

    public static final String SCRIPT_CONFIG = "script_engine.yml";

    private static SlimyGuis instance;

    private Metrics metrics;

    private GuiRegistry guiRegistry;
    private HotbarRegistry hotbarRegistry;
    private ScriptRegistry scriptRegistry;

    public SlimyGuis() {
        instance = this;
    }

    @Override
    public void onEnable() {
        metrics = new Metrics(this);

        getServer().getPluginManager().registerEvents(new GuiEventListener(), this);

        PlaceholderUtil.tryHook();
        EconomyManager.enable();

        /*
        { // script system
            File scriptsConfigFile = new File(getDataFolder(), SCRIPT_CONFIG);
            if (!scriptsConfigFile.exists())
                saveResource(SCRIPT_CONFIG, false);
            File scriptsFolder = new File(getDataFolder(), "scripts");
            scriptsFolder.mkdir();
            scriptRegistry.loadDefaultFolder(scriptsFolder);
        }
        */

        scriptRegistry = ScriptSystem.subscribe(this);
        scriptRegistry.loadDefaultFolder();

        guiRegistry = GuiSystem.subscribe(this);
        guiRegistry.loadDefaultFolder();

        hotbarRegistry = HotbarSystem.subscribe(this);
        hotbarRegistry.loadDefaultFolder();

        ScoreboardRegistry a = ScoreboardSystem.subscribe(this);
        a.loadDefaultFolder();

        new GuiCommand().subscribe();

        setupCustomDatas();
    }

    protected void setupCustomDatas() {
        metrics.addCustomChart(new Metrics.AdvancedPie("script_engines_used") {

            @Override
            public HashMap<String, Integer> getValues(HashMap<String, Integer> map) {
                Map<String, Long> counts = ScriptSystem.instance().get().values()
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
        HotbarSystem.clearAll();
        GuiSystem.closeAll();
    }

    public static SlimyGuis get() {
        return instance;
    }
}
