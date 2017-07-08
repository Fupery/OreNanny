package me.Fupery.OreNanny.Utils;

import me.Fupery.OreNanny.OreNanny;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public enum Lang {
    NO_PERMISSION(true), NO_PLAYERS_FOUND(true), PLAYER_NOT_FOUND(true), INVALID_MATERIAL(true),
    BLOCKS_FOR(false), BLOCKS_BROKEN(false), XRAY_CHANCE(false), COMMAND_HELP(true);

    public static final String prefix = "§d[OreNanny] ";
    final boolean isErrorMessage;
    private String message;

    Lang(boolean isErrorMessage) {
        this.isErrorMessage = isErrorMessage;
        OreNanny plugin = JavaPlugin.getPlugin(OreNanny.class);
//        String language = plugin.getConfig().getString("language");
        String language = "english";
        FileConfiguration langFile =
                YamlConfiguration.loadConfiguration(plugin.getTextResourceFile("lang.yml"));

        if (!langFile.contains(language)) {
            language = "english";
        }
        ConfigurationSection lang = langFile.getConfigurationSection(language);

        if (lang.get(name()) != null) {
            message = lang.getString(name());

        } else {
            Bukkit.getLogger().warning(String.format("%sError loading %s from lang.yml", prefix, name()));
        }
    }

    public String message() {
        ChatColor colour = (isErrorMessage) ? ChatColor.RED : ChatColor.LIGHT_PURPLE;
        return prefix + colour + message;
    }

    public String rawMessage() {
        return message;
    }

    public enum Array {
        HELP;

        String[] messages;

        Array() {
            OreNanny plugin = JavaPlugin.getPlugin(OreNanny.class);
//            String language = plugin.getConfig().getString("language");
            String language = "english";
            FileConfiguration langFile =
                    YamlConfiguration.loadConfiguration(plugin.getTextResourceFile("lang.yml"));

            if (!langFile.contains(language)) {
                language = "english";
            }
            ConfigurationSection lang = langFile.getConfigurationSection(language);

            if (lang.get(name()) != null) {
                List<String> strings = lang.getStringList(name());
                messages = strings.toArray(new String[strings.size()]);

            } else {
                Bukkit.getLogger().warning(String.format("Error loading %s from lang.yml", name()));
            }
        }

        public String[] messages() {
            return messages;
        }
    }
}