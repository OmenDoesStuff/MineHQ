package net.frozenorb.mbungee.handlers;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.mbungee.MBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BungeeHandler {

    private Map<ProxiedPlayer, String> prevServer;
    @Getter @Setter private boolean whitelisted = true;
    @Getter private List<String> whitelists;

    public BungeeHandler(){
        prevServer = new HashMap<>();
        whitelists = MBungee.getInstance().getConfig().getStringList("whitelists");
    }

    public boolean isWhitelisted(String uuid){
        for(String uuids : whitelists){
            if(uuids.equals(uuid)){
                return true;
            }
        }
        return false;
    }

    public void setWhitelisted(String uuid, boolean bool){
        if(bool){
            if(!isWhitelisted(uuid)){
                whitelists.add(uuid);
            }
        } else {
            if(isWhitelisted(uuid)){
                whitelists.remove(uuid);
            }
        }
    }

    public String getPreviousServer(ProxiedPlayer pp){
        if(prevServer.containsKey(pp)){
            return prevServer.get(pp);
        }
        return null;
    }

    public void setPreviousServer(ProxiedPlayer pp, String server){
        if(server == null){
            prevServer.remove(pp);
            return;
        }
        prevServer.put(pp, server);
    }


}
