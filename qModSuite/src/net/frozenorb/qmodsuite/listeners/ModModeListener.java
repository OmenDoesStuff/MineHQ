package net.frozenorb.qmodsuite.listeners;

import net.frozenorb.bridge.bukkit.utils.events.ProfileJoinEvent;
import net.frozenorb.qlib.utils.ItemBuilder;
import net.frozenorb.qmodsuite.QModSuite;
import net.frozenorb.qmodsuite.handlers.ModModeHandler;
import net.frozenorb.qmodsuite.punish.Punishment;
import net.frozenorb.qmodsuite.punish.utils.PunishmentTypes;
import net.frozenorb.qmodsuite.utils.ModProfile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class ModModeListener implements Listener {

    @EventHandler
    public void onJoin(ProfileJoinEvent e){
        if(ModProfile.getModProfile(e.getJoinEvent().getPlayer()) == null) {
            new ModProfile(e.getJoinEvent().getPlayer().getUniqueId());
        }
        ModModeHandler mmh = QModSuite.getInstance().getModModeHandler();
//        if(Bridge.getProfileManager().getProfileByUUID(e.getJoinEvent().getPlayer().getUniqueId()).getCurrentGrant().getRank().isStaff()){
//            mmh.setModMode(e.getJoinEvent().getPlayer(), true);
//        }
        for(Player p : mmh.inModMode){
            mmh.setTags(Arrays.asList(e.getJoinEvent().getPlayer()), p, true, mmh.isVanished(p));
        }
        try {
            for(Player p : Bukkit.getOnlinePlayers()){
                for(Player vanished : mmh.vanished){
                    if(p != e.getJoinEvent().getPlayer()) p.hidePlayer(vanished);
                }
            }
        } catch (Exception ignored){}
    }

    @EventHandler
    public void onDamageTaken(EntityDamageEvent e){
        ModModeHandler mmh = QModSuite.getInstance().getModModeHandler();
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(mmh.isInModMode(p)){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e){
        ModModeHandler mmh = QModSuite.getInstance().getModModeHandler();
        if(e.getDamager() instanceof Player){
            Player p = (Player) e.getDamager();
            if(mmh.isVanished(p)){
                e.setCancelled(true);
                p.sendMessage("§c§lYou cannot do this while in mod mode.");
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        ModModeHandler mmh = QModSuite.getInstance().getModModeHandler();
        if(!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;
        if(e.getItem() != null && e.getItem().getItemMeta() != null && e.getItem().getItemMeta().getDisplayName() != null && mmh.isInModMode(e.getPlayer())){
            String name = e.getItem().getItemMeta().getDisplayName();
            if(name.toLowerCase().contains("online staff")){
                mmh.openOnlineStaff(e.getPlayer());
            }
            if(name.toLowerCase().contains("become visible")){
                mmh.setVanish(e.getPlayer(), false);
                e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), new ItemBuilder(Material.INK_SACK,1).displayName("§bBecome Invisible").data((short) 10).build());
            }
            if(name.toLowerCase().contains("become invisible")){
                mmh.setVanish(e.getPlayer(), true);
                e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), new ItemBuilder(Material.INK_SACK,1).displayName("§bBecome Visible").data((short) 8).build());

            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemClick(InventoryClickEvent e){
        if(e.getInventory() != null && e.getInventory().getName() != null && e.getInventory().getName().equals("Online staff")){
            if(e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getDisplayName() != null){
                if(e.getWhoClicked() instanceof Player){
                    String name = e.getCurrentItem().getItemMeta().getDisplayName();
                    Player p = (Player) e.getWhoClicked();
                    Player to = Bukkit.getPlayerExact(ChatColor.stripColor(name));
                    p.teleport(to);
                    p.closeInventory();
                    p.sendMessage("§6Teleporting you to §f" + name + "§6.");
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e){
        ItemStack is = e.getPlayer().getItemInHand();
        ModModeHandler mmh = QModSuite.getInstance().getModModeHandler();
        if(is != null && is.getItemMeta() != null && is.getItemMeta().getDisplayName() != null && mmh.isInModMode(e.getPlayer())){
            if(is.getItemMeta().getDisplayName().toLowerCase().contains("inspection book")){
                if(e.getRightClicked() != null && e.getRightClicked() instanceof Player){
                    Player clicked = (Player) e.getRightClicked();
                    e.getPlayer().sendMessage("§eOpening inventory of: §b" + clicked.getName());
                    e.getPlayer().openInventory(clicked.getInventory());
                }
            }
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        ModProfile.getModProfiles().remove(ModProfile.getModProfile(e.getPlayer()));
        ModModeHandler mmh = QModSuite.getInstance().getModModeHandler();
        mmh.setModMode(e.getPlayer(), false);
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e){
        ModModeHandler mmh = QModSuite.getInstance().getModModeHandler();
        for(Player p : mmh.inModMode){
            mmh.setModMode(p, false);
        }
    }


    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Player player = Bukkit.getServer().getPlayer(event.getUniqueId());

        if (player != null && player.isOnline()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cPlease wait a few seconds before reconnecting...");
            return;
        }
        UUID uuid = event.getUniqueId();
        String ip = event.getAddress().getHostAddress();
        QModSuite.getPunishBackend().getPunishments().forEach(punishment -> Bukkit.broadcastMessage(punishment.getUuid().toString()));

        if (QModSuite.getPunishBackend().isCurrentlyPunishedByTypes(uuid, PunishmentTypes.BAN)) {
            Set<Punishment> punish = QModSuite.getPunishBackend().getActivePunishmentsByTypes(uuid, PunishmentTypes.BAN);
            if (punish.size() > 0) {
                Punishment disallow = (Punishment) punish.toArray()[0];
                if (disallow != null) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, disallow.getKickMessage());
                    return;
                }
            }
        }

        if (QModSuite.getPunishBackend().isCurrentlyPunishedByTypes(uuid, PunishmentTypes.BLACKLIST)) {
            Set<Punishment> punish = QModSuite.getPunishBackend().getActivePunishmentsByTypes(uuid, PunishmentTypes.BLACKLIST);
            if (punish.size() > 0) {
                Punishment disallow = (Punishment) punish.toArray()[0];
                if (disallow != null) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, disallow.getKickMessage());
                    return;
                }
            }
        }

    }
}
