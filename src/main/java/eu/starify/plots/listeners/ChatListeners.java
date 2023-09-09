package eu.starify.plots.listeners;

import eu.starify.plots.database.PlotManager;
import eu.starify.plots.plot.Plot;
import eu.starify.plots.utils.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListeners implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        if(player.hasPermission("ownplots.admin"))
        {
            return;
        }
        Plot plot = PlotManager.getPlotByOwner(player.getName());
        if(plot != null)
        {
            event.setFormat(ChatUtil.fixColors("&6[&eLvl." + plot.getLevel() + "&6] ") + event.getFormat());
        }
        else
        {
            event.setFormat(ChatUtil.fixColors("&6[&eLvl.0&6] ") + event.getFormat());
        }
    }

}
