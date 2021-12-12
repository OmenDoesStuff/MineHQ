package net.frozenorb.qmodsuite.utils.redis;

import net.frozenorb.bridge.bukkit.Bridge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

public class ModSubscriber extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        String[] args = message.split("¸");
        String type = args[0].toLowerCase();
        switch(type) {
            case "helpop" : {
                Bukkit.getOnlinePlayers().stream().filter(p -> Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank().isStaff()).forEach(p -> {
                    p.sendMessage("§9[Request] §7[" + args[1] + "] §b" + args[2] + " §7requested assistance");
                    p.sendMessage("    §9Reason: §7" + args[3]);
                });
                return;
            }
            case "report" : {
                Bukkit.getOnlinePlayers().stream().filter(p -> Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank().isStaff()).forEach(p -> {
                    p.sendMessage("§9[Report] §7[" + args[1] + "] §b" + args[2] + " §7(" + args[3] +  ") reported by §b" + args[4]);
                    p.sendMessage("    §9Reason: §7" + args[5]);
                });
                return;
            }
            case "staffchat": {
                Bukkit.getOnlinePlayers().stream().filter(p -> Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank().isStaff()).forEach(p -> {
                    p.sendMessage("§7[" + args[1] + "] §5" + args[2] + "§d: " + args[3]);
                });
                return;
            }
        }

    }

    @Override
    public void onPMessage(final String s, final String s2, final String s3) {
    }

    @Override
    public void onSubscribe(final String s, final int i) {
    }

    @Override
    public void onUnsubscribe(final String s, final int i) {
    }

    @Override
    public void onPUnsubscribe(final String s, final int i) {
    }

    @Override
    public void onPSubscribe(final String s, final int i) {
    }

}
