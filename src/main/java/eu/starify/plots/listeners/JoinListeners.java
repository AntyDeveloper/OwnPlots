package eu.starify.plots.listeners;

import eu.starify.plots.SPlots;
import eu.starify.plots.challenge.Challenge;
import eu.starify.plots.database.PlotManager;
import eu.starify.plots.plot.PlotMember;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListeners implements Listener {

    private final SPlots plugin;

    public JoinListeners(SPlots plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        PlotManager plotManager = plugin.getPlotManager();
        PlotMember plotMember = plotManager.getPlotMember(player);
        plugin.getPlayerDataManager().addPlayerMember(player.getName(), plotMember);
        plugin.getPlayerDataManager().getChallenge_players().put(player.getName(), new Challenge(player));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();
        plugin.getPlayerDataManager().removePlayerMember(player.getName());
        if(plugin.getPlayerDataManager().getBorder_players().contains(player))
        {
            plugin.getPlayerDataManager().getBorder_players().remove(player);
        }
        if(plugin.getPlayerDataManager().getChallenge_players().containsKey(player.getName()))
        {
            plugin.getPlayerDataManager().getChallenge_players().remove(player.getName());
        }
    }

}
