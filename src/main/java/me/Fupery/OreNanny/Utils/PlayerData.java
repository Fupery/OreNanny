package me.Fupery.OreNanny.Utils;

import me.Fupery.OreNanny.OreNanny;
import org.bukkit.Material;

import java.util.UUID;

public class PlayerData implements Comparable<PlayerData> {
    public final int[] data;
    public final UUID player;

    private PlayerData(UUID player, int[] data) {
        this.player = player;
        this.data = data;
    }

    public static PlayerData generate(UUID player, int[] data) {
        return (data != null) ? new PlayerData(player, data) : null;
    }

    private static int getMaterialWeighting(Material material) {
        switch (material) {
            case DIAMOND_ORE:
                return 10;
            case GOLD_ORE:
                return 4;
            case REDSTONE_ORE:
                return 3;
            case EMERALD_ORE:
                return 3;
            case LAPIS_ORE:
                return 3;
            case COAL_ORE:
                return 1;
            case IRON_ORE:
                return 2;
            default:
                return 0;
        }
    }

    @Override
    public int compareTo(PlayerData o) {
        PlayerData[] pair = new PlayerData[]{this, o};
        double[] score = new double[2];

        for (int i = 0; i < 2; i++) {
            score[i] = pair[i].getXRayScore();
        }
        if (score[0] == score[1]) {
            return 0;
        }
        return (score[0] > score[1]) ? 1 : -1;
    }

    public double getXRayScore() {
        double score = 0;
        for (int i = 0; i < data.length; i++) {
            score += data[i] * getMaterialWeighting(OreNanny.getDataFormat()[i]);
        }
        score *= 150;
        score /= data[0];
        return score;
    }

    public int getTotalBlocks() {
        int total = 0;
        for (int amount : data) {
            total += amount;
        }
        return total;
    }

    public String getDataString() {
        String dataString = "[";
        for (int i = 0; i < data.length; i++) {
            dataString += data[i];
            if (i < data.length - 1) {
                dataString += ", ";
            }
        }
        return dataString + ']';
    }
}
