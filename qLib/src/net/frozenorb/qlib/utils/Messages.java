package net.frozenorb.qlib.utils;

import lombok.Getter;
import net.frozenorb.qlib.QLib;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import java.text.DecimalFormat;

public class Messages {

    @Getter private static String prefix, name, displayName, website, store, twitter, discord, teamSpeak;
    @Getter private static String redisIP, redisPassword, mongoIP, mongoUser, mongoPassword;
    @Getter private static int redisPort, mongoPort;
    @Getter private static boolean redisAuth, mongoAuth, hubMode;
    @Getter private static ChatColor primary, secondary;

    public Messages() {
        prefix = ChatColor.translateAlternateColorCodes('&', QLib.getInstance().getConfig().getString("server.prefix"));
        name = QLib.getInstance().getConfig().getString("server.name");
        displayName = ChatColor.translateAlternateColorCodes('&', QLib.getInstance().getConfig().getString("server.displayname"));
        website = QLib.getInstance().getConfig().getString("server.website");
        store = QLib.getInstance().getConfig().getString("server.store");
        twitter = QLib.getInstance().getConfig().getString("server.twitter");
        discord = QLib.getInstance().getConfig().getString("server.discord");
        teamSpeak = QLib.getInstance().getConfig().getString("server.teamspeak");
        primary = ChatColor.valueOf(QLib.getInstance().getConfig().getString("server.primary"));
        secondary = ChatColor.valueOf(QLib.getInstance().getConfig().getString("server.secondary"));

        redisIP = QLib.getInstance().getConfig().getString("redis.ip");
        redisPort = QLib.getInstance().getConfig().getInt("redis.port");
        redisAuth = QLib.getInstance().getConfig().getBoolean("redis.auth.enabled");
        redisPassword = QLib.getInstance().getConfig().getString("redis.auth.password");

        mongoIP = QLib.getInstance().getConfig().getString("mongo.ip");
        mongoPort = QLib.getInstance().getConfig().getInt("mongo.port");
        mongoAuth = QLib.getInstance().getConfig().getBoolean("mongo.auth.enabled");
        mongoUser = QLib.getInstance().getConfig().getString("mongo.auth.user");
        mongoPassword = QLib.getInstance().getConfig().getString("mongo.auth.password");

        hubMode = QLib.getInstance().getConfig().getBoolean("hubmode");
    }

    public static String formatTPS(double tps, boolean shouldColor) {
        DecimalFormat format = new DecimalFormat("##.##");
        ChatColor colour;
        if (tps >= 18.0D) {
            colour = ChatColor.GREEN;
        } else {
            if (tps >= 15.0D) {
                colour = ChatColor.YELLOW;
            } else {
                colour = ChatColor.RED;
            }
        }
        return (shouldColor ? colour : "") + format.format(tps);
    }

    public static void sendLog(Plugin plugin, String type, String message) {
        sendLog(plugin, ChatColor.AQUA, type, message);
    }

    public static void sendLog(Plugin plugin, ChatColor color, String type, String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[" + color + plugin.getName() + "&8] &7(&f" + type + "&7) &f" + message));
    }


}
