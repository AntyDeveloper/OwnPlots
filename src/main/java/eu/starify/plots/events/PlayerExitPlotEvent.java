package eu.starify.plots.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerExitPlotEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Player player;

    public PlayerExitPlotEvent(Player player)
    {
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }

    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLERS_LIST;
    }

}
