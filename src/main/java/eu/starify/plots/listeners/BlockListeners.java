package eu.starify.plots.listeners;

import eu.starify.plots.SPlots;
import eu.starify.plots.config.LanguageManager;
import eu.starify.plots.database.PlotManager;
import eu.starify.plots.plot.Plot;
import eu.starify.plots.utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListeners implements Listener {

    private final SPlots plugin;
    PlotManager plotManager;
    LanguageManager languageManager;

    public BlockListeners(SPlots plugin)
    {
        this.plugin = plugin;
        this.plotManager = new PlotManager(plugin);
        this.languageManager = SPlots.getInstance().getLanguageManager();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event)
    {
        Location location = event.getBlock().getLocation();
        if(!location.getWorld().getName().equalsIgnoreCase(SPlots.getInstance().getConfig_manager().getAllowed_world()))
        {
            return;
        }
        Player player = event.getPlayer();
        if(player.hasPermission("ownplots.admin"))
        {
            return;
        }
        Plot plot = plotManager.getPlotAt(location);
        if(plot != null && !plot.isMember(player) && !plot.getVisitorsSettings().isBlock_break())
        {
            event.setCancelled(true);
            player.sendMessage(ChatUtil.fixColorsWithPrefix(languageManager.getMessage("plot-protection-break").replace("%plot_owner%", plot.getOwner())));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Location location = event.getBlock().getLocation();
        if(!location.getWorld().getName().equalsIgnoreCase(SPlots.getInstance().getConfig_manager().getAllowed_world()))
        {
            return;
        }
        Player player = event.getPlayer();
        if(player.hasPermission("ownplots.admin"))
        {
            return;
        }
        Plot plot = plotManager.getPlotAt(location);
        if(plot != null && !plot.isMember(player) && !plot.getVisitorsSettings().isBlock_place())
        {
            event.setCancelled(true);
            player.sendMessage(ChatUtil.fixColorsWithPrefix(languageManager.getMessage("plot-protection-build").replace("%plot_owner%", plot.getOwner())));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event)
    {
        Location location = event.getBlock().getLocation();
        if(!location.getWorld().getName().equalsIgnoreCase(SPlots.getInstance().getConfig_manager().getAllowed_world()))
        {
            return;
        }

        Plot plot = plugin.getPlotManager().getPlotAt(location);
        if(plot != null && !plot.getPlotSettings().isFire_spread())
        {
            event.setCancelled(true);
        }
    }

}
