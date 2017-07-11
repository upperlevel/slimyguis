package xyz.upperlevel.spigot.gui.config;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.upperlevel.spigot.gui.SlimyGuis;


public class UpdaterTask extends BukkitRunnable {

    private int interval;
    private Runnable task;

    public UpdaterTask(Runnable task) {
        this.task = task;
    }

    public UpdaterTask(int interval, Runnable task) {
        this.interval = interval;
        this.task = task;
    }

    public void start() {
        runTaskTimer(SlimyGuis.get(), 0, interval);
    }

    public void stop() {
        cancel();
    }

    @Override
    public void run() {
        task.run();
    }
}
