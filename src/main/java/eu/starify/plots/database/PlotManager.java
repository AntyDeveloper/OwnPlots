package eu.starify.plots.database;

import eu.starify.plots.SPlots;
import eu.starify.plots.challenge.Challenge;
import eu.starify.plots.playerdata.PlayerDataManager;
import eu.starify.plots.plot.Plot;
import eu.starify.plots.plot.PlotMember;
import eu.starify.plots.plot.PlotSettings;
import eu.starify.plots.plot.VisitorsSettings;
import eu.starify.plots.utils.ChatUtil;
import eu.starify.plots.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlotManager {

    private final SPlots plugin;
    private final String plots_table, table_plots_settings;

    public PlotManager(SPlots plugin)
    {
        this.plugin = plugin;
        this.plots_table = plugin.getDatabase().getPlots_table();
        this.table_plots_settings = plugin.getDatabase().getTable_plots_settings();
    }

    public void savePlotAsync(final Plot plot)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String level_data = plot.getIron_blocks() + ";" + plot.getGold_blocks() + ";" +
                            plot.getDiamond_blocks() + ";" + plot.getEmerald_blocks() + ";" +
                            plot.getNetherite_blocks() + ";" + plot.getBeacons() + ";";

                    int level = plot.getLevel();

                    String upgrade_data = plot.isSpeed_upgrade() + ";" + plot.isJump_upgrade() + ";" +
                            plot.isMob_drop_upgrade() + ";" + plot.isMob_exp_upgrade() + ";" +
                            plot.getSize() + ";";

                    String banned_players = "";
                    for(String banned : plot.getBanned_players())
                    {
                        banned_players = banned_players + banned + ";";
                    }



                    PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement(
                            "UPDATE `" + plots_table + "` SET `closed`=?, `banned-players`=?, `level-data`=?, `upgrade-data`=?, `level`=? WHERE `plot-name`=?");
                    statement.setBoolean(1, plot.isClosed());
                    statement.setString(2, banned_players);
                    statement.setString(3, level_data);
                    statement.setString(4, upgrade_data);
                    statement.setInt(5, level);
                    statement.setString(6, plot.getPlot_name());
                    statement.executeUpdate();
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void savePlotSync(final Plot plot)
    {
        try
        {
            String level_data = plot.getIron_blocks() + ";" + plot.getGold_blocks() + ";" +
                    plot.getDiamond_blocks() + ";" + plot.getEmerald_blocks() + ";" +
                    plot.getNetherite_blocks() + ";" + plot.getBeacons() + ";";

            String upgrade_data = plot.isSpeed_upgrade() + ";" + plot.isJump_upgrade() + ";" +
                    plot.isMob_drop_upgrade() + ";" + plot.isMob_exp_upgrade() + ";" +
                    plot.getSize() + ";";

            String banned_players = "";
            for(String banned : plot.getBanned_players())
            {
                banned_players = banned_players + banned + ";";
            }

            int level = plot.getLevel();

            PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement(
                    "UPDATE `" + plots_table + "` SET `closed`=?, `banned-players`=?, `level-data`=?, `upgrade-data`=?, `level`=? WHERE `plot-name`=?");
            statement.setBoolean(1, plot.isClosed());
            statement.setString(2, banned_players);
            statement.setString(3, level_data);
            statement.setString(4, upgrade_data);
            statement.setInt(5, level);
            statement.setString(6, plot.getPlot_name());
            statement.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<ItemStack> getTopPlots()
    {
        ArrayList<ItemStack> topPlots = new ArrayList<>();
        int counter = 0;

        try {
            PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement(
                    "SELECT * FROM `" + plots_table + "` ORDER BY `level` DESC LIMIT 9");
            ResultSet result = statement.executeQuery();
            while(result.next())
            {
                String[] members = result.getString("members").split(";");

                List<String> lore = new ArrayList<>();

                lore.add("");
                lore.add(ChatUtil.fixColors("&c* &9&lPoziom działki &7" + result.getInt("level")));
                lore.add("");
                lore.add(ChatUtil.fixColors("&c* &9&lMiejsce &7#" + (counter + 1)));
                lore.add(ChatUtil.fixColors("&c* &9&lCzlonkow &7" + members.length));
                lore.add("");
                lore.add(ChatUtil.fixColors("&c* &9&lCzlonkowie:"));

                for(int i = 0; i < members.length && i <= 8; i++)
                {
                    lore.add(ChatUtil.fixColors("  &f- &7" + members[i]));
                    if(i >= 8) {
                        lore.add("&7[&f...&7]");
                    }
                }

                lore.add("");
                lore.add(ChatUtil.fixColors("&7(( &fLewy przycisk &7&oaby teleportowac sie do warpa &7))"));

                ItemStack one = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta oneMeta = (SkullMeta) one.getItemMeta();
                oneMeta.setOwner(result.getString("owner"));
                oneMeta.setDisplayName(ChatUtil.fixColors("&7#" + (counter + 1) + " Działka: &9&n" + result.getString("owner"))); // " &7{&f*&7}"
                oneMeta.setLore(lore);
                one.setItemMeta(oneMeta);

                topPlots.add(one);

                counter++;
            }

            for(int i = counter; i <= 8; i++)
            {
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
                itemMeta.setOwner("AspDev");
                itemMeta.setDisplayName(ChatUtil.fixColors("&7#" + (i + 1) + " Działka: &4&l&nBRAK &7{&f*&7}"));
                itemMeta.setLore(ChatUtil.fixColors(Arrays.asList(
                        "",
                        "&cBrak danych..."
                )));
                item.setItemMeta(itemMeta);
                topPlots.add(item);
            }

        } catch(SQLException exception) {
            exception.printStackTrace();
        }

        return topPlots;
    }


    public void loadPlots()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    plugin.getPlayerDataManager().getPlots().clear();
                    PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement("SELECT * FROM `" + plots_table + "`");
                    ResultSet row = statement.executeQuery();
                    while(row.next())
                    {
                        String plot_name = row.getString("plot-name");
                        String owner = row.getString("owner");
                        Location location = LocationUtil.getLocationFromString(row.getString("location"));
                        boolean closed = row.getBoolean("closed");
                        String level_data = row.getString("level-data");
                        String upgrade_data = row.getString("upgrade-data");

                        String[] members_array = row.getString("members").split(";");
                        ArrayList<String> members = new ArrayList<>();
                        for(String s : members_array)
                        {
                            members.add(s);
                        }

                        String[] banned_array = row.getString("banned-players").split(";");
                        ArrayList<String> banned_players = new ArrayList<>();
                        for(String s : banned_array)
                        {
                            banned_players.add(s);
                        }

                        Plot plot = new Plot(owner, location, members, plot_name, closed, banned_players, level_data, upgrade_data);
                        plugin.getPlayerDataManager().getPlots().put(plot_name, plot);
                    }
                    plugin.getLogger().info("Plots loaded successfully!");
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void createPlot(final Player owner, final String plot_name, final String location)
    {
        Location loc = LocationUtil.getLocationFromString(location);
        loc.add(0,-1,0).getBlock().setType(Material.BEDROCK);
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String upgrade_data = "false;false;false;false;16;"; // speed ; jump ; mob-drop ; mob-exp ; size ;
                    String level_data = "0;0;0;0;0;0;";

                    String query = "INSERT INTO `" + table_plots_settings + "` (`id`,`plot-name`,`plot-settings`,`visitors-settings`) " +
                            "VALUES (NULL,?,?,?);";
                    PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement(query);
                    statement.setString(1, plot_name);
                    statement.setString(2, PlotSettings.getDefaultString());
                    statement.setString(3, VisitorsSettings.getDefaultString());
                    statement.executeUpdate();

                    query = "INSERT INTO `" + plots_table + "` (id, owner, `plot-name`, location, members, `closed`, `banned-players`, `level-data`, `upgrade-data`, `level`) " +
                            "VALUES (NULL,?,?,?,?,?,?,?,?,?);";
                    statement = plugin.getDatabase().getConnection().prepareStatement(query);
                    statement.setString(1, owner.getName());
                    statement.setString(2, plot_name);
                    statement.setString(3, location);
                    statement.setString(4, owner.getName() + ";");
                    statement.setBoolean(5, false);
                    statement.setString(6, "");
                    statement.setString(7, level_data);
                    statement.setString(8, upgrade_data);
                    statement.setInt(9, 0);
                    statement.executeUpdate();

                    ArrayList<String> members = new ArrayList<>();
                    members.add(owner.getName());
                    Plot plot = new Plot(owner.getName(), LocationUtil.getLocationFromString(location), members, plot_name, false, new ArrayList<String>(), level_data, upgrade_data);
                    plugin.getPlayerDataManager().getPlots().put(plot_name, plot);

                    PlotMember member = plugin.getPlayerDataManager().getPlotMember(owner.getName());
                    member.setOwn_plot(plot_name);
                    plugin.getPlayerDataManager().addPlayerMember(owner.getName(), member);

                    plugin.getPlayerDataManager().getChallenge_players().put(owner.getName(), new Challenge(owner));
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public PlotMember getPlotMember(Player player)
    {
        try
        {
            PreparedStatement statement = plugin.getDatabase().getConnection()
                    .prepareStatement("SELECT * FROM `" + plots_table + "` WHERE `members` LIKE '%" + player.getName() + "%'");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next())
            {
                String[] members = resultSet.getString("members").split(";");
                for(int i = 0; i < members.length; i++)
                {
                    if(members[i].equalsIgnoreCase(player.getName()))
                    {
                        String own_plot = resultSet.getString("plot-name");
                        return new PlotMember(player, own_plot);
                    }
                }
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return new PlotMember(player, null);
    }

    public void deletePlot(final Plot plot)
    {
        Location loc = plot.getLocation();
        loc.add(0,-1,0).getBlock().setType(Material.AIR);
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement("DELETE FROM `" + plots_table + "` WHERE `plot-name`=?");
                    statement.setString(1, plot.getPlot_name());
                    statement.executeUpdate();

                    statement = plugin.getDatabase().getConnection().prepareStatement("DELETE FROM `" + table_plots_settings + "` WHERE `plot-name`=?");
                    statement.setString(1, plot.getPlot_name());
                    statement.executeUpdate();

                    if(plugin.getPlayerDataManager().getPlotMember(plot.getOwner()) != null)
                    {
                        plugin.getPlayerDataManager().getPlotMember(plot.getOwner()).setOwn_plot(null);
                    }
                    plugin.getPlayerDataManager().getPlots().remove(plot.getPlot_name());

                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void addMember(final Plot plot, final String new_member)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String members = "";
                    for(String s : plot.getMembers())
                    {
                        members = members + s + ";";
                    }

                    members = members + new_member + ";";

                    PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement("UPDATE `" + plots_table + "` SET `members`=? WHERE `plot-name`=?");
                    statement.setString(1, members);
                    statement.setString(2, plot.getPlot_name());
                    statement.executeUpdate();
                    plot.addMember(new_member);
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void kickMember(final Plot plot, final String kicked_member)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String members = "";
                    for(String s : plot.getMembers())
                    {
                        if(!s.equalsIgnoreCase(kicked_member))
                        {
                            members = members + s + ";";
                        }
                    }

                    PreparedStatement statement = plugin.getDatabase().getConnection().prepareStatement("UPDATE `" + plots_table + "` SET `members`=? WHERE `plot-name`=?");
                    statement.setString(1, members);
                    statement.setString(2, plot.getPlot_name());
                    statement.executeUpdate();
                    plot.removeMember(kicked_member);
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public boolean hasPlot(Player player)
    {
        try {
            if(plugin.getPlayerDataManager().getPlotMember(player.getName()).getOwn_plot() != null) {
                return true;
            }
        } catch (NullPointerException exception) {
            return false;
        }

        return false;
    }

    @Nullable
    public Plot getPlotAt(Location loc)
    {
        for(Plot plots : SPlots.getInstance().getPlayerDataManager().getPlots().values())
        {
            if(plots.getPlotAt(loc) != null)
            {
                return plots;
            }
        }


        return null;
    }

    @Nullable
    public Plot getPlotAt(Location loc, int custom_size)
    {
        for(Plot plots : SPlots.getInstance().getPlayerDataManager().getPlots().values())
        {
            if(plots.getPlotAt(loc, custom_size) != null)
            {
                return plots;
            }
        }


        return null;
    }

    public static Plot getPlotByOwner(String owner)
    {
        PlayerDataManager playerDataManager = SPlots.getInstance().getPlayerDataManager();
        return playerDataManager.getPlots().get(playerDataManager.getPlotMember(owner).getOwn_plot());
    }


    @Nullable
    public static Plot getPlotByOfflineOwner(String owner)
    {
        PlayerDataManager playerDataManager = SPlots.getInstance().getPlayerDataManager();
        for(Plot plot : playerDataManager.getPlots().values())
        {
            if(plot.getOwner().equalsIgnoreCase(owner))
            {
                return plot;
            }
            else
            {
                for(String s : plot.getMembers())
                {
                    if(s.equalsIgnoreCase(owner))
                    {
                        return plot;
                    }
                }
            }
        }

        return null;
    }

    public Plot getPlotByName(String name)
    {
        return plugin.getPlayerDataManager().getPlots().get(name);
    }

}
