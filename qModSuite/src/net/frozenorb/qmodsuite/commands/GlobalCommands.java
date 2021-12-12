package net.frozenorb.qmodsuite.commands;

import net.frozenorb.bridge.bukkit.Bridge;
import net.frozenorb.qlib.command.*;
import net.frozenorb.qmodsuite.utils.ModProfile;
import net.frozenorb.qmodsuite.utils.redis.RedisUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GlobalCommands {

    @Command(name = "sc")
    public void staffChatCommand(CommandArgs cmd){
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        if(!Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank().isStaff()){
            p.sendMessage(ChatColor.RED + "No permission.");
            return;
        }
        if(args.length < 1) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /sc <message...>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eSend a global staff message.")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        String reason = StringUtils.join(args, ' ');
        RedisUtils.buildRedisMessage("staffchat", Bukkit.getServerName(), p.getName(), reason);
    }

    @Command(name = "report")
    public void reportCommand(CommandArgs cmd){
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        if(args.length < 2) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /report <player> <reason...>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eReport a player for breaking the rules.")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if(t == null) {
            p.sendMessage("§cNo player with the name \"" + args[0] + "\" found.");
            return;
        }
        ModProfile modProfile = ModProfile.getModProfile(t);
        modProfile.setReports(modProfile.getReports() + 1);
        String reason = StringUtils.join(args, ' ', 1, args.length);
        RedisUtils.buildRedisMessage("report", Bukkit.getServerName(), t.getName(), "" + modProfile.getReports(), p.getName(), reason);
        p.sendMessage("§aWe have recieved your report.");
    }

    @Command(name = "helpop", aliases = {"request"}, inGameOnly = true)
    public void requestCommand(CommandArgs cmd){
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        if(args.length < 1) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel().toLowerCase() + " <reason...>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eRequest staff help")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        String reason = StringUtils.join(args, ' ');
        RedisUtils.buildRedisMessage("helpop", Bukkit.getServerName(), p.getName(), reason);
        p.sendMessage("§aWe have recieved your request.");

    }


}
