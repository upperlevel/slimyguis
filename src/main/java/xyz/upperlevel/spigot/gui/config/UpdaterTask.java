package xyz.upperlevel.spigot.gui.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.upperlevel.spigot.gui.SlimyGuis;

@RequiredArgsConstructor
public class UpdaterTask extends BukkitRunnable {
    @Getter
    private final int interval;
    private final Runnable updater;

    public void start() {
        runTaskTimer(SlimyGuis.get(), interval, interval);
    }

    public void stop() {
        cancel();
    }

    @Override
    public void run() {
        updater.run();
    }
}
