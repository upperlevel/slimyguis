package xyz.upperlevel.spigot.gui.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.upperlevel.spigot.gui.SlimyGuis;

@RequiredArgsConstructor
public class UpdaterTask extends BukkitRunnable {
    private final int updateTime;
    private final Runnable updater;

    public void start() {
        runTaskTimer(SlimyGuis.getInstance(), updateTime, updateTime);
    }

    public void stop() {
        cancel();
    }

    @Override
    public void run() {
        updater.run();
    }
}
