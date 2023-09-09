package eu.starify.plots;

import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import eu.starify.plots.commands.ChallengeCommand;
import eu.starify.plots.commands.PlotCommand;
import eu.starify.plots.commands.RankingCommand;
import eu.starify.plots.config.ConfigManager;
import eu.starify.plots.config.FileManager;
import eu.starify.plots.config.LanguageManager;
import eu.starify.plots.database.IPlotDatabase;
import eu.starify.plots.database.MySQL;
import eu.starify.plots.database.PlotManager;
import eu.starify.plots.database.SQLite;
import eu.starify.plots.listeners.*;
import eu.starify.plots.placeholders.OwnPlaceholder;
import eu.starify.plots.playerdata.PlayerDataManager;
import eu.starify.plots.tasks.PlotSaveTask;
import eu.starify.plots.utils.Metrics;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SPlots extends JavaPlugin {

    private static SPlots INSTANCE;
    private static Economy eco = null;

    private ConfigManager configManager;
    private FileManager fileManager;
    private LanguageManager languageManager;
    private PlayerDataManager playerDataManager;
    private PlotManager plotManager;
    private WorldBorderApi worldBorderApi;
    private IPlotDatabase database;

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider = getServer().getServicesManager().getRegistration(WorldBorderApi.class);

        if (worldBorderApiRegisteredServiceProvider == null) {
            getLogger().severe(String.format("[%s] - Disabled due to no WorldBorderAPI dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        worldBorderApi = worldBorderApiRegisteredServiceProvider.getProvider();

        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        configManager = new ConfigManager(this);
        if(getConfig().getBoolean("mysql.enabled")) {
            database = new MySQL(this);
        } else {
            database = new SQLite(this);
        }

        fileManager = new FileManager(this);
        languageManager = new LanguageManager(this);
        languageManager.loadMessages();

        playerDataManager = new PlayerDataManager();
        plotManager = new PlotManager(this);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            getLogger().info("Loading plots...");
            plotManager.loadPlots();
        }, 5L);


        getCommand("dzialka").setExecutor(new PlotCommand(this));
        getCommand("challenge").setExecutor(new ChallengeCommand(this));
        getCommand("ranking").setExecutor(new RankingCommand());

        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new BlockListeners(this), this);
        manager.registerEvents(new JoinListeners(this), this);
        manager.registerEvents(new PlayerListeners(this), this);
        manager.registerEvents(new InventoryListeners(), this);
        manager.registerEvents(new PlotListeners(), this);
        manager.registerEvents(new EntityListeners(this), this);
        manager.registerEvents(new ChatListeners(), this);
        manager.registerEvents(new FlyListener(), this);

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new PlotSaveTask(this), 18000L, 18000L);
        new OwnPlaceholder().register();
        new Metrics(this, 14347);

    }

    @Override
    public void onDisable() {
        if(playerDataManager != null)
            playerDataManager.onDisable();
    }

    public IPlotDatabase getDatabase() {
        return database;
    }

    public ConfigManager getConfig_manager() {
        return configManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public static SPlots getInstance() {
        return INSTANCE;
    }

    public PlotManager getPlotManager() {
        return plotManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public WorldBorderApi getWorldBorderApi() {
        return worldBorderApi;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }

    public Long getPlayerMoney(Player player) {
        return (long) eco.getBalance(player);
    }

    public void setPlayerMoney(Player player, int value) {
        eco.withdrawPlayer(player, eco.getBalance(player));
        eco.depositPlayer(player, value);
    }

    public void takePlayerMoney(Player player, int value) {
        eco.withdrawPlayer(player, value);
    }

    public void addPlayerMoney(Player player, int value) {
        eco.depositPlayer(player, value);
    }
}
