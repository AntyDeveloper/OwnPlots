package eu.starify.plots.listeners;

import eu.starify.plots.SPlots;
import eu.starify.plots.config.LanguageManager;
import eu.starify.plots.events.PlayerEnterPlotEvent;
import eu.starify.plots.events.PlayerExitPlotEvent;
import eu.starify.plots.plot.Plot;
import eu.starify.plots.utils.ChatUtil;
import eu.starify.plots.utils.RandomTP;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlotListeners implements Listener {

    private LanguageManager languageManager;

    public PlotListeners() {
        languageManager = SPlots.getInstance().getLanguageManager();
    }

    @EventHandler
    public void onPlayerEnterPlot(PlayerEnterPlotEvent event)
    {
        final Player player = event.getPlayer();
        final Plot plot = event.getPlot();

        if(!plot.isMember(player) && (plot.isClosed() || plot.isBanned(player.getName())))
        {
            if(player.hasPermission("ownplots.admin"))
            {
                return;
            }
            RandomTP.randomTeleport(player, 1);
            return;
        }

        player.sendTitle(ChatColor.YELLOW + "Teren dzialki", ChatColor.GRAY + plot.getOwner(), 10, 40, 10);
        player.sendMessage(ChatUtil.fixColorsWithPrefix(languageManager.getMessage("plot-enter-message").replace("%plot_owner%", plot.getOwner())));
    }

    @EventHandler
    public void onPlayerExitPlot(PlayerExitPlotEvent event)
    {
        final Player player = event.getPlayer();
        player.sendTitle(ChatColor.RED + "Opuszczasz", ChatColor.RED + "dzialke", 10, 40, 10);
        player.sendMessage(ChatUtil.fixColorsWithPrefix(languageManager.getMessage("plot-leave-message")));
    }

}
