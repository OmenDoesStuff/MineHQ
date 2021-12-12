package net.frozenorb.basic.commands;

import net.frozenorb.basic.BasicPlayer;
import net.frozenorb.bridge.bukkit.Bridge;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MessageCommands {

    @Command(name = "message", aliases = {"msg","m","t","tell"}, inGameOnly = true)
    public void messageCommand(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(p);
        if(args.length < 2) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " <player> <message...>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eSend a player a private message")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if(t == null) {
            p.sendMessage("§cNo player with the name \"" + args[0] + "\" found.");
            return;
        }
        BasicPlayer targetPlayer = BasicPlayer.getBasicPlayer(t);
        if(!targetPlayer.isPrivateMessagesEnabled()) {
            p.sendMessage(t.getDisplayName() + " has messages turned off.");
            return;
        }
        if(!basicPlayer.isPrivateMessagesEnabled()) {
            p.sendMessage("§cYou have messages toggled off.");
            return;
        }
        if(targetPlayer.isIgnored(p.getUniqueId()) && !Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank().isStaff()) {
            p.sendMessage("§cThat player has messaging disabled.");
            return;
        }
        String message = StringUtils.join(args, ' ', 1, args.length);
        t.sendMessage("§7(From " + p.getDisplayName() + "§7) " + message);
        p.sendMessage("§7(To " + t.getDisplayName() + "§7) " + message);
        if(targetPlayer.isSoundsEnabled()) t.playSound(t.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
        basicPlayer.setMessagingPlayer(t.getUniqueId());
        targetPlayer.setMessagingPlayer(p.getUniqueId());

    }

    @Command(name = "reply", aliases = {"r"}, inGameOnly = true)
    public void replyCommand(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(p);
        if(basicPlayer.getMessagingPlayer() == null || BasicPlayer.getBasicPlayer(basicPlayer.getMessagingPlayer()) == null) {
            p.sendMessage("§cYou aren't in a conversation.");
            return;
        }
        BasicPlayer targetPlayer = BasicPlayer.getBasicPlayer(basicPlayer.getMessagingPlayer());
        if(targetPlayer == null || Bukkit.getPlayer(basicPlayer.getMessagingPlayer()) == null || !Bukkit.getPlayer(basicPlayer.getMessagingPlayer()).isOnline()) {
            p.sendMessage("§cThat player has logged out.");
            return;
        }
        Player t = Bukkit.getPlayer(basicPlayer.getMessagingPlayer());
        if(args.length < 1) {
            p.sendMessage("§6You are in a conversation with " + t.getDisplayName() + "§6.");
            return;
        }
        if(!basicPlayer.isPrivateMessagesEnabled()) {
            p.sendMessage("§cYou have messages toggled off.");
            return;
        }
        if(!targetPlayer.isPrivateMessagesEnabled()) {
            p.sendMessage(t.getDisplayName() + " has messages turned off.");
            return;
        }
        String message = StringUtils.join(args, ' ');
        t.sendMessage("§7(From " + p.getDisplayName() + "§7) " + message);
        if(targetPlayer.isSoundsEnabled()) t.playSound(t.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
        p.sendMessage("§7(To " + t.getDisplayName() + "§7) " + message);
        targetPlayer.setMessagingPlayer(p.getUniqueId());
    }

    @Command(name = "togglepm")
    public void togglePMCommand(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(p);
        boolean b = basicPlayer.isPrivateMessagesEnabled();
        if(b) {
            basicPlayer.setPrivateMessagesEnabled(false);
        }else {
            basicPlayer.setPrivateMessagesEnabled(true);
        }
        p.sendMessage((b ? ChatColor.RED : ChatColor.GREEN) + "Private messages have been " + (b ? "disabled" : "enabled"));
    }

    @Command(name = "sounds")
    public void soundsCommand(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(p);
        boolean b = basicPlayer.isSoundsEnabled();
        if(b) {
            basicPlayer.setSoundsEnabled(false);
        }else {
            basicPlayer.setSoundsEnabled(true);
        }
        p.sendMessage("§eMessaging sounds have been " + (b ? "disabled" : "enabled"));
    }

    @Command(name = "ignore")
    public void ignoreCommand(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(p);
        if(args.length < 1) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " <player>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eStart ignoring a player. You won't receive private messages from them or see their public chat messages")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        switch(args[0].toLowerCase()) {
            case "list": {
                if(basicPlayer.getIgnored().isEmpty()) {
                    p.sendMessage("§eYou aren't ignoring anyone.");
                    return;
                }
                p.sendMessage("§eYou are currently ignoring §c" + basicPlayer.getIgnored().size() + " §e" + (basicPlayer.getIgnored().size() == 1 ? "player" : "players") + "§e: §c" + StringUtils.join(basicPlayer.getIgnored(), "§e, §c"));
                return;
            }

            case "remove": {
                if(args.length != 2) {
                    ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " remove <player>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eStop ignoring a player")));
                    p.spigot().sendMessage(cp.create());
                    return;
                }
                Player t = Bukkit.getPlayer(args[1]);
                if(t == null) {
                    p.sendMessage("§cNo player with the name \"" + args[1] + "\" found.");
                    return;
                }
                if(!basicPlayer.isIgnored(t.getUniqueId())) {
                    p.sendMessage("§cYou aren't ignoring " + t.getName() + "§c.");
                    return;
                }
                basicPlayer.removeIgnored(t.getUniqueId());
                p.sendMessage("§eYou are no longer ignoring " + t.getName());
                return;
            }

            default: {
                Player t = Bukkit.getPlayer(args[0]);
                if(t == null) {
                    p.sendMessage("§cNo player with the name \"" + args[0] + "\" found.");
                    return;
                }
                if(basicPlayer.isIgnored(t.getUniqueId())) {
                    p.sendMessage("§cYou are already ignoring " + t.getDisplayName() + "§c.");
                    return;
                }
                basicPlayer.addIgnored(t.getUniqueId());
                p.sendMessage("§eNow ignoring " + t.getDisplayName());
                return;
            }
        }

    }

    @Command(name = "unignore", inGameOnly = true)
    public void unignoreCommand(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(p);
        String[] args = cmd.getArgs();
        if(args.length != 1) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " <player>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eStop ignoring a player")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if(t == null) {
            p.sendMessage("§cNo player with the name \"" + args[0] + "\" found.");
            return;
        }
        if(!basicPlayer.isIgnored(t.getUniqueId())) {
            p.sendMessage("§cYou aren't ignoring " + t.getName() + "§c.");
            return;
        }
        basicPlayer.removeIgnored(t.getUniqueId());
        p.sendMessage("§eYou are no longer ignoring " + t.getName());
        return;
    }

}
