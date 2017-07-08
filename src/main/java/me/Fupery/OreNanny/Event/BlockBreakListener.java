package me.Fupery.OreNanny.Event;

import me.Fupery.OreNanny.OreNanny;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;
import java.util.List;

public class BlockBreakListener implements Listener {

    private int maximumHeight;
    private List<Material> trackedOres;

    public BlockBreakListener(int maximumHeight) {
        this.maximumHeight = maximumHeight;
        this.trackedOres = Arrays.asList(OreNanny.getDataFormat());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        Material mat = event.getBlock().getType();
        final Material blockType = (mat != Material.GLOWING_REDSTONE_ORE) ? mat : Material.REDSTONE_ORE;
        final double blockHeight = event.getBlock().getLocation().getY();

        if (event.isCancelled()) {
            return;
        }
        OreNanny.runAsync(new Runnable() {
            @Override
            public void run() {
                if (player.getGameMode() == GameMode.CREATIVE || player.hasPermission("orenanny.bypass")
                        || !trackedOres.contains(blockType) || blockHeight > maximumHeight) {
                    return;
                }
                OreNanny.getDataManager().incrementCount(player.getUniqueId(), blockType);
            }
        });
    }
}
