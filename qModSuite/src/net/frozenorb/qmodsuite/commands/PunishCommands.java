package net.frozenorb.qmodsuite.commands;

import net.frozenorb.bridge.bukkit.Bridge;
import net.frozenorb.bridge.bukkit.utils.TimeUtil;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.CommandArgs;
import net.frozenorb.qmodsuite.QModSuite;
import net.frozenorb.qmodsuite.punish.Punishment;
import net.frozenorb.qmodsuite.punish.utils.PunishmentTypes;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PunishCommands {

    @Command(name = "ban", permission = "hermes.ban", description = "Banish a player from the server.")
    public void ban(CommandArgs args) {
        if (args.length() < 2) {
            args.getSender().sendMessage("§cUsage: /ban <player> [duration] <reason>");
            return;
        }
        String senderName = (args.getSender() instanceof Player) ? Bridge.getProfileManager().getProfileByUUIDOrCreate(args.getPlayer().getUniqueId()).getCurrentGrant().getRank().getColor() + args.getPlayer().getName() : "§4§lConsole";
        long length = TimeUtil.parseTime(args.getArgs(1));
        if (length == -1L) {
            length = Long.MAX_VALUE;
        }
        OfflinePlayer targetPlayer;
        if (Bukkit.getPlayer(args.getArgs(0)) != null) {
            targetPlayer = Bukkit.getPlayer(args.getArgs(0));
        }
        else {
            targetPlayer = Bukkit.getOfflinePlayer(args.getArgs(0));
        }
//        if (args.getSender() instanceof Player && Messages.isHigher(targetPlayer.getUUID(), args.getPlayer().getUniqueId())) {
//            args.getSender().sendMessage("§cThis player has a higher rank than you, so you may not punish them.");
//            return;
//        }
        StringBuilder reasonBuilder = new StringBuilder();
        boolean silent = false;
        for (int i = (length == Long.MAX_VALUE) ? 1 : 2; i < args.length(); ++i) {
            if (args.getArgs(i).equalsIgnoreCase("-s")) {
                silent = true;
            }
            else {
                reasonBuilder.append(reasonBuilder.toString().equals("") ? "" : " ").append(args.getArgs(i));
            }
        }
        String reason = reasonBuilder.toString();
        if (QModSuite.getPunishBackend().isCurrentlyPunishedByTypes(targetPlayer.getUniqueId(), PunishmentTypes.BAN)) {
            args.getSender().sendMessage("§cThis player is already banned.");
            return;
        }
        String ip = targetPlayer.isOnline() ? targetPlayer.getPlayer().getAddress().getHostString() : "None";
        Punishment punishment = new Punishment(UUID.randomUUID(), targetPlayer.getUniqueId(), PunishmentTypes.BAN, System.currentTimeMillis(), reason, length, ip);
        if (args.getSender() instanceof Player) {
            punishment.setExecutor(args.getPlayer().getUniqueId());
        }
        QModSuite.getPunishBackend().insertPunishment(punishment);
        punishment.broadcastPunishment(senderName, Bridge.getProfileManager().getProfileByUUIDOrCreate(targetPlayer.getUniqueId()).getCurrentGrant().getRank().getColor() + targetPlayer.getName(), reason, (length == Long.MAX_VALUE ? "Permanent" : TimeUtil.millisToRoundedTime(length)), silent);
//        Bukkit.broadcastMessage(args.getSender().getName() + " banned " + targetPlayer.getName() + " for " + reason + " for " + TimeUtil.millisToRoundedTime(length)+ " (Silent: " + silent + ")");
      //  Helios.getCommunication().sendPacket((HeliosPacket)new PacketPunishmentCreate(punishment, senderName, Messages.getFormattedName(targetPlayer.getUUID()), targetPlayer.getUUID(), silent));
        if (targetPlayer.isOnline()) {
            new BukkitRunnable() {
                public void run() {
                    targetPlayer.getPlayer().kickPlayer(punishment.getKickMessage());
                }
            }.runTask(QModSuite.getInstance());
        }
    }

    @Command(name = "unban", permission = "hermes.unban", description = "Remove a player's ban.")
    public void unban(CommandArgs args) {
        if (args.length() < 2) {
            args.getSender().sendMessage("§cUsage: /unban <player> <reason>");
            return;
        }
        OfflinePlayer targetPlayer;
        if (Bukkit.getPlayer(args.getArgs(0)) != null) {
            targetPlayer = Bukkit.getPlayer(args.getArgs(0));
        }
        else {
            targetPlayer = Bukkit.getOfflinePlayer(args.getArgs(0));
        }
        String senderName = (args.getSender() instanceof Player) ? Bridge.getProfileManager().getProfileByUUIDOrCreate(args.getPlayer().getUniqueId()).getCurrentGrant().getRank().getColor() + args.getPlayer().getName() : "§4§lConsole";
        StringBuilder reasonBuilder = new StringBuilder();
        boolean silent = false;
        for (int i = 1; i < args.length(); ++i) {
            if (args.getArgs(i).equalsIgnoreCase("-s")) {
                silent = true;
            }
            else {
                reasonBuilder.append(reasonBuilder.toString().equals("") ? "" : " ").append(args.getArgs(i));
            }
        }
        String reason = reasonBuilder.toString();
        if (!QModSuite.getPunishBackend().isCurrentlyPunishedByTypes(targetPlayer.getUniqueId(), PunishmentTypes.BAN)) {
            args.getSender().sendMessage("§cThis player is not currently banned.");
            return;
        }
        Punishment punishment = (Punishment)QModSuite.getPunishBackend().getActivePunishmentsByTypes(targetPlayer.getUniqueId(), PunishmentTypes.BAN).toArray()[0];
        punishment.setPardoned(true);
        punishment.setPardonedAt(System.currentTimeMillis());
        if (args.getSender() instanceof Player) {
            punishment.setPardonedBy(args.getPlayer().getUniqueId());
        }
        punishment.setPardonedReason(reason);
        QModSuite.getPunishBackend().updatePunishment(targetPlayer.getUniqueId(), punishment.getPunishID(), punishment);
        punishment.broadcastPunishment(senderName, Bridge.getProfileManager().getProfileByUUIDOrCreate(targetPlayer.getUniqueId()).getCurrentGrant().getRank().getColor() + targetPlayer.getName(), reason, null, silent);

//        Bukkit.broadcastMessage(args.getSender().getName() + " unbanned " + targetPlayer.getName() + " for " + reason + " (Silent: " + silent + ")");
      //  Helios.getCommunication().sendPacket((HeliosPacket)new PacketPunishmentRemove(punishment, senderName, Messages.getFormattedName(targetPlayer.getUUID()), targetPlayer.getUUID(), silent));
    }
    
}
