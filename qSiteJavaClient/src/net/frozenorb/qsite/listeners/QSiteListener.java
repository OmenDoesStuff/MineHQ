package net.frozenorb.qsite.listeners;

import net.frozenorb.qsite.QSite;
import net.frozenorb.qsite.backend.SiteUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class QSiteListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        SiteUser user = QSite.getMongoManager().loadProfileByUUID(p.getUniqueId());
        if(user == null) {
            return;
        }
        if(!p.getName().equals(user.getIGN())) {
            user.setIGN(p.getName()); user.saveProfile();
            user.saveProfile();
        }
    }


}
