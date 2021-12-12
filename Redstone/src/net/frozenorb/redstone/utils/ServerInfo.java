package net.frozenorb.redstone.utils;

import net.frozenorb.redstone.property.ServerProperty;
import net.frozenorb.redstone.property.ServerStatus;
import net.frozenorb.redstone.tasks.ServerTask;
import org.bukkit.ChatColor;

public class ServerInfo {

    public static String getStatusFormatted(String s, boolean shouldColor) {
        if(!serverExists(s)) return null;
        ServerStatus st;
        try {
            st = ServerStatus.valueOf(getProperty(s, ServerProperty.STATUS).toUpperCase());
        }catch (IllegalArgumentException e) {
            return null;
        }
        switch (st) {
            case ONLINE:
                return (shouldColor ? ChatColor.GREEN : "") + "Online";
            case WHITELISTED:
                return (shouldColor ? ChatColor.WHITE : "") + "Whitelisted";
            case OFFLINE:
                return (shouldColor ? ChatColor.RED : "") + "Offline";
        }
        return null;
    }

    public static boolean serverExists(String server) {
        return ServerTask.getServerDataTable().column(ServerProperty.ONLINE).containsKey(server);
    }

    public static String getProperty(String server, ServerProperty data) {
        if(!serverExists(server)) {
            return null;
        }else {
            return "" + ServerTask.getServerDataTable().column(data).get(server);
        }
    }
}
