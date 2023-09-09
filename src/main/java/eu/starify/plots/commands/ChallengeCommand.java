package eu.starify.plots.commands;

import eu.starify.plots.SPlots;
import eu.starify.plots.gui.ChallengeGUI;
import eu.starify.plots.utils.ChatUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChallengeCommand implements CommandExecutor {

    private final SPlots plugin;

    public ChallengeCommand(SPlots plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if(!(sender instanceof Player))
        {
            sender.sendMessage("Nie mozesz tego uzywac");
            return true;
        }

        final Player player = (Player) sender;

        if(SPlots.getInstance().getPlotManager().hasPlot(player) == false) {
            player.sendMessage(ChatUtil.fixColorsWithPrefix("&cnie nalezysz do zadnej dzialki, aby z tego korzystac!"));
            return true;
        }

        player.openInventory(ChallengeGUI.getChallengesInventory(player));
        player.playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1.0f ,1.0f);

        return true;
    }
}
