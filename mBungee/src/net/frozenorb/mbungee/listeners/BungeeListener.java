package net.frozenorb.mbungee.listeners;

import net.frozenorb.bridge.bungee.Bridge;
import net.frozenorb.bridge.bungee.profiles.Profile;
import net.frozenorb.bridge.bungee.ranks.Rank;
import net.frozenorb.mbungee.MBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BungeeListener implements Listener {

    @EventHandler(priority = 100)
    public void onLeave(PlayerDisconnectEvent e){
        ProxiedPlayer pp = e.getPlayer();
        Profile pr = Bridge.getProfileManager().getProfileByUUID(pp.getUniqueId());
        Rank r = pr.getCurrentGrant().getRank();
        if(r.isStaff()){
            MBungee.getInstance().getBungeeHandler().setPreviousServer(pp, null);
            if(MBungee.getInstance().getStaff().contains(pp)) {
                MBungee.getInstance().getStaff().remove(pp);
            }
            for(ProxiedPlayer p : MBungee.getInstance().getStaff()){
                p.sendMessage("§9[Staff] " + r.getColor() + e.getPlayer().getName() + " §cleft §bthe network (from " + e.getPlayer().getServer().getInfo().getName() + ")");
            }
        }
    }

    @EventHandler
    public void onLogin(ServerConnectEvent e) {
        Profile profile;
        profile = Bridge.getProfileManager().getProfileByUUIDOrCreate(e.getPlayer().getUniqueId());
        if (profile == null) {
            e.getPlayer().disconnect("§cYour profile didn't load.");
        } else {
            ProxiedPlayer p = e.getPlayer();
            Profile pr = Bridge.getProfileManager().getProfileByUUIDOrCreate(p.getUniqueId());
            Rank r = pr.getCurrentGrant().getRank();
            if(r.isStaff()){
                if(!MBungee.getInstance().getStaff().contains(p)){
                    MBungee.getInstance().getStaff().add(p);
                }
                if(MBungee.getInstance().getBungeeHandler().getPreviousServer(p) != null){
                    return;
                }
                ServerInfo server = MBungee.getInstance().getProxy().getServerInfo("Restricted-Hub");
                e.setTarget(server);
                for(ProxiedPlayer p1 : MBungee.getInstance().getStaff()){
                    p1.sendMessage("§9[Staff] " + r.getColor() + p.getName() + " §ajoined §bthe network (" + server.getName() + ")");
                }
            }
        }
    }

    @EventHandler
    public void onPing(ProxyPingEvent e){
        if(MBungee.getInstance().getBungeeHandler().isWhitelisted()){
            ServerPing ping = e.getResponse();
            ping.setVersion(new ServerPing.Protocol("§4Whitelisted", 9999));
            e.setResponse(ping);
        }
    }

    @EventHandler
    public void onWhitelistJoin(LoginEvent e){
        if(!MBungee.getInstance().getBungeeHandler().isWhitelisted(e.getConnection().getUniqueId().toString())){
            e.setCancelled(true);
            e.setCancelReason("§cThe network is currently whitelisted.\n§cAdditional info may be found at protocol.rip");
        }
    }

    @EventHandler
    public void onJoinOther(ServerConnectedEvent e){
        if(MBungee.getInstance().getBungeeHandler().getPreviousServer(e.getPlayer()) != null){
            Rank r = Bridge.getProfileManager().getProfileByUUID(e.getPlayer().getUniqueId()).getCurrentGrant().getRank();
            String prev = MBungee.getInstance().getBungeeHandler().getPreviousServer(e.getPlayer());
            if(r.isStaff()) {
                for (ProxiedPlayer p1 : MBungee.getInstance().getStaff()) {
                    if (p1 != e.getPlayer()) {
                        if (p1.getServer().getInfo() == e.getServer().getInfo())
                            p1.sendMessage("§9[Staff] " + r.getColor() + e.getPlayer().getName() + " §bjoined your server (from " + prev + ")");
                        if (p1.getServer().getInfo() == MBungee.getInstance().getProxy().getServerInfo(prev)) {
                            p1.sendMessage("§9[Staff] " + r.getColor() + e.getPlayer().getName() + " §bleft your server (to " + e.getServer().getInfo().getName() + ")");
                        }
                    }
                }
            }
        }
        MBungee.getInstance().getBungeeHandler().setPreviousServer(e.getPlayer(), e.getServer().getInfo().getName());
    }

    @EventHandler
    public void onKick(ServerKickEvent e) {
        ProxiedPlayer p = e.getPlayer();
        Profile pr = Bridge.getProfileManager().getProfileByUUID(p.getUniqueId());
        if(pr == null) {
            p.disconnect("§cFailed to load your profile...");
            return;
        }
        int i = new Random().nextInt(2);
        ServerInfo connect = BungeeCord.getInstance().getServerInfo("Hub-" + (i + 1));
        Rank r = pr.getCurrentGrant().getRank();
        if(r.isStaff()) {
            connect = BungeeCord.getInstance().getServerInfo("Restricted-Hub");
        }
        if(e.getKickedFrom() == connect) {
            p.disconnect("§cKicked from §6" + e.getKickedFrom().getName() + "§c: §f" + e.getKickReason());
            return;
        }
        e.setCancelServer(connect);
        e.setCancelled(true);
        BungeeCord.getInstance().getScheduler().schedule(MBungee.getInstance(), () -> {
            p.sendMessage("§cKicked from §6" + e.getKickedFrom().getName() + "§c: §f" +
                    e.getKickReason());
        }, 2, TimeUnit.SECONDS);
    }

}