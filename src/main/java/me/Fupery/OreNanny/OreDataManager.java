package me.Fupery.OreNanny;

import me.Fupery.OreNanny.Utils.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class OreDataManager extends YamlConfiguration {

    private final File file;
    private boolean saveScheduled;

    private OreDataManager(File file) {
        super();
        this.file = file;
        saveScheduled = false;
    }

    public static int getMaterialSlot(Material material) {
        return getMaterialSlot(material, OreNanny.getDataFormat());
    }

    public static int getMaterialSlot(Material material, Material[] dataFormat) {
        for (int i = 0; i < dataFormat.length; i++) {
            if (dataFormat[i] == material) {
                return i;
            }
        }
        return -1;
    }

    public static OreDataManager loadConfiguration(File file) {
        Validate.notNull(file, "File cannot be null");
        OreDataManager config = new OreDataManager(file);
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load configuration from stream", e);
        }
        Material[] oldFormat = config.getDataFormat();
        if (!Arrays.equals(oldFormat, OreNanny.getDataFormat())) {
            config.convertFormat(oldFormat);
        }
        return config;
    }

    private void convertFormat(Material[] oldFormat) {
        for (String uuid : getOreDataSection().getKeys(false)) {
            UUID player = UUID.fromString(uuid);
            int[] rawData = getOreData(player);
            if (rawData == null) {
                continue;
            }
            int[] data = new int[OreNanny.getDataFormat().length];

            for (int i = 0; i < OreNanny.getDataFormat().length; i++) {
                int slot = -1;
                if (i < rawData.length) {
                    slot = getMaterialSlot(OreNanny.getDataFormat()[i], oldFormat);
                }
                data[i] = (slot != -1) ? rawData[slot] : 0;
            }
            setOreData(player, data);
        }
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Material[] getDataFormat() {
        List<Integer> format = getIntegerList("format");

        if (format == null || format.size() == 0) {
            format = new ArrayList<>();
            for (Material mat : OreNanny.getDataFormat()) {
                format.add(mat.ordinal());
                set("format", format);
            }
        }
        Material[] matValues = Material.values();
        Material[] dataFormat = new Material[format.size()];

        for (int i = 0; i < format.size(); i++) {
            dataFormat[i] = matValues[format.get(i)];
        }
        return dataFormat;
    }

    public PlayerData getPlayerData(UUID player) {
        return PlayerData.generate(player, getOreData(player));
    }

    private int[] getOreData(UUID player) {
        String rawString = getOreDataSection().getString(player.toString());
        if (rawString == null) {
            return null;
        }
        String[] dataString = rawString.split(":");
        int[] data = new int[dataString.length];
        for (int i = 0; i < dataString.length; i++) {
            data[i] = Integer.parseInt(dataString[i]);
        }
        return data;
    }

    public void incrementCount(UUID player, Material oreType) {
        int[] data = getOreData(player);
        if (data == null) {
            data = new int[OreNanny.getDataFormat().length];
        }
        data[getMaterialSlot(oreType)]++;
        setOreData(player, data);
        save();
    }

    public void clearPlayerData(UUID player) {
        setOreData(player, null);
    }

    public ArrayList<PlayerData> getTrackedPlayers() {
        Set<String> keys = getOreDataSection().getKeys(false);
        ArrayList<PlayerData> players = new ArrayList<>();

        for (String key : keys) {
            UUID uuid = UUID.fromString(key);
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (!player.isBanned() && player.hasPlayedBefore()
                    && (player.isOnline() && !player.getPlayer().hasPermission("orenanny.bypass"))) {
                players.add(PlayerData.generate(uuid, getOreData(uuid)));
            }
        }
        return players;
    }

    private void setOreData(UUID player, int[] oreData) {
        String dataString = "";
        for (int i = 0; i < oreData.length; i++) {
            dataString += oreData[i];
            if (i < oreData.length - 1) {
                dataString += ':';
            }
        }
        getOreDataSection().set(player.toString(), dataString);
    }

    private ConfigurationSection getOreDataSection() {
        ConfigurationSection dataSection = getConfigurationSection("oreData");
        if (dataSection == null) {
            dataSection = createSection("oreData");
        }
        return dataSection;
    }

    private void save() {
        if (!saveScheduled) {
            saveScheduled = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(OreNanny.plugin(), new Runnable() {
                @Override
                public void run() {
                    try {
                        save(file);
                        saveScheduled = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 6000);
        }
    }
}
