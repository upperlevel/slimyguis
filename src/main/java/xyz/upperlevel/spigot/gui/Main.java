package xyz.upperlevel.spigot.gui;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.upperlevel.spigot.gui.config.ConfigGuiManager;
import xyz.upperlevel.spigot.gui.config.ConfigHotbar;

import java.io.File;

public class Main extends JavaPlugin implements Listener {
    @Getter
    private static Main instance;

    public Main() {
        if(instance == null)
            instance = this;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new GuiEventListener(), this);

        ConfigGuiManager.onLoad(new File(getDataFolder(), "guis"));
        ConfigHotbar.onLoad(new File(getDataFolder(), "hotbars"));
    }
}
