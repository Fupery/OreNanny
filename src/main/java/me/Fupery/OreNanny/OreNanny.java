package me.Fupery.OreNanny;

import me.Fupery.OreNanny.Event.BlockBreakListener;
import me.Fupery.OreNanny.Event.CommandHandler;
import me.Fupery.OreNanny.Utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class OreNanny extends JavaPlugin {

    private static Material[] dataFormat;
    private OreDataManager dataManager;
    private File dataFile;

    public static OreNanny plugin() {
        return (OreNanny) Bukkit.getPluginManager().getPlugin("OreNanny");
    }

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin(), runnable);
    }

    public static Material[] getDataFormat() {
        return dataFormat;
    }

    public static OreDataManager getDataManager() {
        return plugin().dataManager;
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
            saveDefaultConfig();
        }
        if (!(new File(getDataFolder(), "config.yml").exists())) {
            saveDefaultConfig();
        }
        dataFile = new File(getDataFolder(), "oreData.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataFormat = loadConfigFormat();
        int maxHeight = getConfig().getInt("max-height");
        dataManager = OreDataManager.loadConfiguration(dataFile);
        getCommand("orenanny").setExecutor(new CommandHandler());
        Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakListener(maxHeight), this);
    }

    private Material[] loadConfigFormat() {
        ConfigurationSection materials = getConfig().getConfigurationSection("track-ores");
        ArrayList<Material> formattingOptions = new ArrayList<>();
        formattingOptions.add(Material.STONE);

        for (String string : materials.getKeys(false)) {
            if (materials.getBoolean(string)) {
                try {
                    formattingOptions.add(Material.valueOf(string));
                } catch (Exception e) {
                    getLogger().warning(String.format(Lang.INVALID_MATERIAL.message(), string));
                }
            }
        }
        return formattingOptions.toArray(new Material[formattingOptions.size()]);
    }

    @Override
    public void onDisable() {
        try {
            dataManager.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Reader getTextResourceFile(String fileName) {
        return getTextResource(fileName);
    }
}
