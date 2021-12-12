package net.frozenorb.qlib.utils;

import net.minecraft.util.com.google.common.collect.HashMultimap;
import net.minecraft.util.com.google.common.collect.Multimap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.*;

public class SignHandler implements Listener {
    private Multimap<UUID, SignChange> signUpdateMap;
    private JavaPlugin plugin;

    public SignHandler(JavaPlugin plugin) {
        this.signUpdateMap = HashMultimap.create();
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerQuitEvent event) {
        this.cancelTasks(event.getPlayer(), null, false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.cancelTasks(event.getPlayer(), null, false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        this.cancelTasks(event.getPlayer(), null, false);
    }

    public boolean showLines(Player player, Sign sign, String[] newLines, long ticks, boolean forceChange) {
        String[] lines = sign.getLines();
        if (Arrays.equals(lines, newLines)) {
            return false;
        }
        Collection<SignChange> signChanges = this.getSignChanges(player);
        Iterator<SignChange> iterator = signChanges.iterator();
        while (iterator.hasNext()) {
            SignChange signChange = iterator.next();
            if (signChange.sign.equals(sign)) {
                if (!forceChange && Arrays.equals(signChange.newLines, newLines)) {
                    return false;
                }
                signChange.runnable.cancel();
                iterator.remove();
                break;
            }
        }
        Location location = sign.getLocation();
        player.sendSignChange(location, newLines);
        SignChange signChange;
        if (signChanges.add(signChange = new SignChange(sign, newLines))) {
            Block block = sign.getBlock();
            BlockState previous = block.getState();
            BukkitRunnable runnable = new BukkitRunnable() {
                public void run() {
                    if (SignHandler.this.signUpdateMap.remove(player.getUniqueId(), signChange) && previous.equals(block.getState())) {
                        player.sendSignChange(location, lines);
                    }
                }
            };
            runnable.runTaskLater(this.plugin, ticks);
            signChange.runnable = runnable;
        }
        return true;
    }

    public Collection<SignChange> getSignChanges(Player player) {
        return this.signUpdateMap.get(player.getUniqueId());
    }

    public void cancelTasks(@Nullable Sign sign) {
        Iterator<SignChange> iterator = this.signUpdateMap.values().iterator();
        while (iterator.hasNext()) {
            SignChange signChange = iterator.next();
            if (sign == null || signChange.sign.equals(sign)) {
                signChange.runnable.cancel();
                signChange.sign.update();
                iterator.remove();
            }
        }
    }

    public void cancelTasks(Player player, @Nullable Sign sign, boolean revertLines) {
        UUID uuid = player.getUniqueId();
        Iterator<Map.Entry<UUID, SignChange>> iterator = this.signUpdateMap.entries().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, SignChange> entry = iterator.next();
            if (entry.getKey().equals(uuid)) {
                SignChange signChange = entry.getValue();
                if (sign != null && !signChange.sign.equals(sign)) {
                    continue;
                }
                if (revertLines) {
                    player.sendSignChange(signChange.sign.getLocation(), signChange.sign.getLines());
                }
                signChange.runnable.cancel();
                iterator.remove();
            }
        }
    }

    private static class SignChange {
        public Sign sign;
        public String[] newLines;
        public BukkitRunnable runnable;

        public SignChange(Sign sign, String[] newLines) {
            this.sign = sign;
            this.newLines = newLines;
        }
    }
}
