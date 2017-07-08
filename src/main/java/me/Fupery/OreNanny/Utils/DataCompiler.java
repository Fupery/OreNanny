package me.Fupery.OreNanny.Utils;

import me.Fupery.OreNanny.OreDataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.Fupery.OreNanny.OreNanny.getDataFormat;

public class DataCompiler {

    public ArrayList<String> getPlayerData(OreDataManager config, OfflinePlayer player) {
        PlayerData playerData = config.getPlayerData(player.getUniqueId());
        if (playerData == null) {
            return null;
        }
        ArrayList<String> strings = new ArrayList<>();

        double totalBlocksBroken = playerData.getTotalBlocks();

        strings.add(Lang.BLOCKS_FOR.message() + player.getName());
        strings.add(Lang.BLOCKS_BROKEN.rawMessage() + (int) totalBlocksBroken);

        for (int i = 0; i < playerData.data.length; i++) {
            double percent = (playerData.data[i] / totalBlocksBroken) * 100D;
            BigDecimal bd = new BigDecimal(percent);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            strings.add(String.format("   §5• §7%s: §r%s §8%s",
                    getDataFormat()[i].name(), playerData.data[i], "(" + bd.doubleValue() + "%)"));
        }
        BigDecimal bd = new BigDecimal(playerData.getXRayScore());
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        if (totalBlocksBroken > 1000) {
            strings.add(Lang.XRAY_CHANCE.rawMessage() + bd.doubleValue());
        }
        return strings;
    }

    public List<String> getTopPlayers(OreDataManager oreConfig) {
        ArrayList<PlayerData> players = oreConfig.getTrackedPlayers();

        if (players == null || players.size() == 0) {
            return Collections.singletonList(Lang.NO_PLAYERS_FOUND.message());
        }
        Collections.sort(players);

        List<String> strings = new ArrayList<>();
        int j = 1;
        for (int i = players.size() - 1; i >= 0 && j <= 10; i--) {
            int totalBlocks = players.get(i).getTotalBlocks();
            if (totalBlocks <= 1000) {
                continue;
            }
            BigDecimal bd = new BigDecimal(players.get(i).getXRayScore());
            bd = bd.setScale(2, RoundingMode.HALF_UP);

            strings.add(String.format("§d%s. §5%s: §a%s §7%s", j,
                    Bukkit.getOfflinePlayer(players.get(i).player).getName(),
                    bd.doubleValue(), players.get(i).getDataString()));
            j++;
        }
        if (strings.size() == 0) {
            return Collections.singletonList(Lang.NO_PLAYERS_FOUND.message());
        }
        return strings;
    }
}
