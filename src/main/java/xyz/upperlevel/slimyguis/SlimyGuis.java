package xyz.upperlevel.slimyguis;

import lombok.Getter;
import org.bstats.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.slimyguis.commands.SlimyGuisCmd;
import xyz.upperlevel.uppercore.economy.EconomyManager;
import xyz.upperlevel.uppercore.gui.GuiRegistry;
import xyz.upperlevel.uppercore.hotbar.HotbarRegistry;
import xyz.upperlevel.uppercore.placeholder.PlaceholderUtil;
import xyz.upperlevel.uppercore.script.ScriptRegistry;

import java.io.File;
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

        // placeholders
        PlaceholderUtil.tryHook();

        // economy
        EconomyManager.enable();

        // scripts
        scriptRegistry = new ScriptRegistry(this);
        scriptRegistry.loadFolder(new File(getDataFolder(), "slimy_scripts"));

        // guis
        guiRegistry = new GuiRegistry(this);
        guiRegistry.loadFolder(new File(getDataFolder(), "slimy_guis"));

        // hotbars
        hotbarRegistry = new HotbarRegistry(this);
        hotbarRegistry.loadFolder(new File(getDataFolder(), "slimy_hotbars"));

        // commands
        new SlimyGuisCmd().subscribe();
    }

    @Override
    public void onDisable() {
    }

    public static SlimyGuis get() {
        return instance;
    }
}
