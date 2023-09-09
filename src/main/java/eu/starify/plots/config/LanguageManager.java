package eu.starify.plots.config;

import eu.starify.plots.SPlots;
import eu.starify.plots.utils.ChatUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private final Map<String, String> messages = new HashMap<>();
    private final SPlots plugin;
    private final FileManager fileManager;
    private String plugin_prefix;

    public LanguageManager(SPlots plugin)
    {
        this.plugin = plugin;
        this.fileManager = plugin.getFileManager();
    }

    public String getMessage(String message_name)
    {
        return messages.get(message_name).replace("%prefix%", plugin_prefix);
    }

    public void loadMessages()
    {
        String language = plugin.getConfig().getString("language");
        File file = fileManager.getFile(language);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for(String key : config.getKeys(false))
        {
            String text = ChatUtil.fixColors(config.getString(key));
            messages.put(key, text);
        }

        plugin_prefix = ChatUtil.applyColor(messages.get("plugin-prefix"));
    }

    public String getPlugin_prefix() {
        return plugin_prefix;
    }
}