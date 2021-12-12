package net.frozenorb.basic.commands;

import net.frozenorb.basic.BasicPlayer;
import net.frozenorb.bridge.bukkit.Bridge;
import net.frozenorb.bridge.bukkit.ranks.Rank;
import net.frozenorb.bridge.utils.MojangUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.CommandArgs;
import net.frozenorb.qlib.utils.BukkitSerialization;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeleportCommands {

    @Command(name = "tp", aliases = {"teleport"}, inGameOnly = true, permission = "basic.tp.to", isAsync = true)
    public void teleportCommand(CommandArgs cmd){
        Player p = cmd.getPlayer();
        if(cmd.getArgs().length < 1) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " <player>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eTeleport to a player")));
            p.spigot().sendMessage(cp.create());
            return;
        }

        Player to = Bukkit.getPlayer(cmd.getArgs()[0]);
        if(to == null){
            UUID offUUID;
            try {
                offUUID = MojangUtils.fetchUUID(cmd.getArgs(0));
            } catch (Exception e) {
                p.sendMessage("§cFailed to contact Mojang Servers - try again later?");
                return;
            }
            BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(offUUID);
            OfflinePlayer op = Bukkit.getOfflinePlayer(offUUID);
            if(op == null || !op.hasPlayedBefore() || basicPlayer == null) {
                p.sendMessage("§cNo player with the name \"" + cmd.getArgs()[0] + "\" found.");
                return;
            }
            Rank r = Bridge.getProfileManager().getProfileByUUIDOrCreate(offUUID).getCurrentGrant().getRank();

            p.teleport(basicPlayer.getLastLocation());
            p.sendMessage("§6Teleporting you to offline location of " + r.getColor() + op.getName() + "§6.");
            return;
        }


        p.teleport(to);
        Rank r = Bridge.getProfileManager().getProfileByUUID(to.getUniqueId()).getCurrentGrant().getRank();
        p.sendMessage("§6Teleporting you to §f" + r.getColor() + to.getName() + "§6.");
    }

    @Command(name = "tphere", aliases = {"s"}, inGameOnly = true, permission = "basic.tp.here", isAsync = true)
    public void teleportHereCommand(CommandArgs cmd){
        Player p = cmd.getPlayer();
        if(cmd.getArgs().length < 1) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " <player>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eTeleport a player to you")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        Player tp = Bukkit.getPlayer(cmd.getArgs()[0]);
        if(tp == null){
            UUID offUUID;
            try {
                offUUID = MojangUtils.fetchUUID(cmd.getArgs(0));
            } catch (Exception e) {
                p.sendMessage("§cFailed to contact Mojang Servers - try again later?");
                return;
            }
            BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(offUUID);
            OfflinePlayer op = Bukkit.getOfflinePlayer(offUUID);
            if(op == null || !op.hasPlayedBefore() || basicPlayer == null) {
                p.sendMessage("§cNo player with the name \"" + cmd.getArgs()[0] + "\" found.");
                return;
            }
            Rank r = Bridge.getProfileManager().getProfileByUUIDOrCreate(offUUID).getCurrentGrant().getRank();
            basicPlayer.setLastLocation(p.getLocation());
            basicPlayer.save();
            p.sendMessage("§6Teleported offline player " + r.getColor() + op.getName() + " §6to you.");
            return;
        }
        tp.teleport(p);
        Rank tpr = Bridge.getProfileManager().getProfileByUUID(tp.getUniqueId()).getCurrentGrant().getRank();
        p.sendMessage("§6Teleporting §f" + tpr.getColor() + tp.getName() + "§6 to you.");
        Rank r = Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank();
        tp.sendMessage("§6Teleporting you to §f" + r.getColor() + p.getName() + "§6.");
    }

    @Command(name = "tppos", inGameOnly = true, permission = "basic.tp.pos")
    public void teleportPosCommand(CommandArgs cmd){
        Player p = cmd.getPlayer();
        if(cmd.getArgs().length < 3) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " [x] [y] [z]").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eTeleport to a location")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        try {
            double x = Double.parseDouble(cmd.getArgs()[0]);
            if(Math.floor(x) == x) x+= 0.5;
            double y = Double.parseDouble(cmd.getArgs()[1]);
            double z = Double.parseDouble(cmd.getArgs()[2]);
            if(Math.floor(z) == z) z+= 0.5;
            p.teleport(new Location(p.getWorld(), x, y, z));
            p.sendMessage("§6Teleporting you to §e[§f" + x + "§e, §f" + y + "§e, §f" + z + "§e]§6.");
        } catch (Exception e){
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " [x] [y] [z]").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eTeleport to a location")));
            p.spigot().sendMessage(cp.create());
        }
    }

}
