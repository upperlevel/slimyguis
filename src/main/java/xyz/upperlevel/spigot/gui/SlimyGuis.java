package xyz.upperlevel.spigot.gui;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.spigot.gui.config.ConfigHotbar;
import xyz.upperlevel.spigot.gui.config.economy.EconomyManager;
import xyz.upperlevel.spigot.gui.config.placeholders.PlaceHolderUtil;
import xyz.upperlevel.spigot.gui.hotbar.HotbarManager;
import xyz.upperlevel.spigot.gui.script.ScriptSystem;

import java.io.File;
import java.util.logging.Logger;

public class SlimyGuis extends JavaPlugin implements Listener {
    public static final String SCRIPT_CONFIG = "script_engine.yml";
    @Getter
    private static SlimyGuis instance;
    @Getter
    private static ScriptSystem scriptSystem;

    public SlimyGuis() {
        if(instance == null)
            instance = this;
    }

    @Override
    public void onEnable() {
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
    }

    @Override
    public void onDisable() {
        HotbarManager.clearAll();
        GuiManager.closeAll();
    }

    public static Logger logger() {
        return instance.getLogger();
    }
}
