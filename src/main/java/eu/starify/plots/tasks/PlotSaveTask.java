package eu.starify.plots.tasks;

import eu.starify.plots.SPlots;
import eu.starify.plots.plot.Plot;
import org.bukkit.scheduler.BukkitRunnable;

public class PlotSaveTask extends BukkitRunnable {

    private SPlots plugin;

    public PlotSaveTask(SPlots plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        plugin.getLogger().info("Saving plots into MySQL database...");
        for(Plot plot : plugin.getPlayerDataManager().getPlots().values())
        {
            plugin.getPlotManager().savePlotAsync(plot);
        }
        plugin.getLogger().info("Plots successfully saved!");
    }
}
