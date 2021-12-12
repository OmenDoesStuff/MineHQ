package net.frozenorb.basic.listeners;

import net.frozenorb.basic.Basic;
import net.frozenorb.basic.BasicPlayer;
import net.frozenorb.bridge.utils.MojangUtils;
import net.frozenorb.qlib.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BasicListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(p);
        if(basicPlayer == null) {
            basicPlayer = new BasicPlayer(p.getUniqueId());
            basicPlayer.save();

        }else {
            basicPlayer.loadSettings();
            if(!Messages.isHubMode()) {
                p.getInventory().setArmorContents(basicPlayer.getLastArmor());
                p.getInventory().setContents(basicPlayer.getLastInv());
                p.teleport(basicPlayer.getLastLocation());
            }

        }


    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(p);
        if(basicPlayer == null) {
            p.sendMessage("profile null");
            return;
        }
        basicPlayer.setDeathArmor(p.getInventory().getArmorContents());
        basicPlayer.setDeathInv(p.getInventory().getContents());
        basicPlayer.save();
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            BlockState block = event.getClickedBlock().getState();
            if (block instanceof Skull) {
                Skull skull = (Skull) block;
                String owner = skull.getOwner();
                player.sendMessage("§eThis is the head of: " + owner);
            }
        }

    }

    @EventHandler
    public void onExit(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        Inventory i = e.getInventory();
        if(i == null || (!i.getTitle().contains("§1§7Inventory: ") && i.getSize() != 36)) return;
        String inv = ChatColor.stripColor(i.getTitle().replaceAll("§1§7Inventory: ", ""));
        ItemStack[] armor = new ItemStack[4];
        armor[0] = i.getItem(39);
        armor[1] = i.getItem(38);
        armor[2] = i.getItem(37);
        armor[3] = i.getItem(36);

        ItemStack[] items = new ItemStack[36];
        for(int it = 35; it >= 0; it--) {
            items[it] = i.getItem(it);
        }
        Bukkit.getScheduler().runTaskAsynchronously(Basic.getInstance(), () -> {
            BasicPlayer basicPlayer;
            try {
                basicPlayer = BasicPlayer.getBasicPlayer(MojangUtils.fetchUUID(inv));
            } catch (Exception ex) {
                p.sendMessage("§cWe failed to contact Mojang Servers - try again later?");
                return;
            }
            if(basicPlayer == null) return;
            basicPlayer.setLastArmor(armor);
            basicPlayer.setLastInv(items);
            p.sendMessage("§cSaving inventory for offline player.");
            basicPlayer.save();

        });




    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(e.getPlayer());
        basicPlayer.save();
        BasicPlayer.getBasicPlayers().remove(basicPlayer);
    }

}
