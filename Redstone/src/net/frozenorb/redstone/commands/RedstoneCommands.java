package net.frozenorb.redstone.commands;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.CommandArgs;
import net.frozenorb.qlib.utils.Messages;
import net.frozenorb.redstone.Redstone;
import net.frozenorb.redstone.property.ServerProperty;
import net.frozenorb.redstone.utils.ServerInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class RedstoneCommands {

    @Command(name = "rsenv", description = "Prints Redstone enviroment information", isAsync = true)
    public void rsenvCommand(CommandArgs cmd) {
        CommandSender s = cmd.getSender();
        String[] args = cmd.getArgs();
        if (args.length != 1) {
            s.sendMessage("§c§nRedstone Environment Dump");
            s.sendMessage("");
            s.sendMessage("§cName: §f" + Bukkit.getServerName());
            s.sendMessage("§cGroup: §f" + Redstone.getInstance().getConfig().getString("servergroup"));
            if(s.hasPermission("redstone.extra")) {
               s.sendMessage("§cTPS: §f" + Messages.formatTPS(Double.parseDouble(ServerInfo.getProperty(Bukkit.getServerName(), ServerProperty.TPS)), false));
               s.sendMessage("§cOnline Players: §f" + ServerInfo.getProperty(Bukkit.getServerName(), ServerProperty.ONLINE) + "/" + ServerInfo.getProperty(Bukkit.getServerName(), ServerProperty.MAXIMUM));
               s.sendMessage("§cStatus: §f" + ServerInfo.getStatusFormatted(Bukkit.getServerName(), false));
            }
        }else {
            if(s.hasPermission("redstone.extra")) {
                if(!ServerInfo.serverExists(args[0])) {
                    s.sendMessage("§cThere is no such server with the name \"" + args[0] + "\" found.");
                    return;
                }
                s.sendMessage("§c§nRedstone Environment Dump");
                s.sendMessage("");
                s.sendMessage("§cName: §f" + args[0]);
                s.sendMessage("§cGroup: §f" + ServerInfo.getProperty(args[0], ServerProperty.GROUP));
                s.sendMessage("§cTPS: §f" + Messages.formatTPS(Double.parseDouble(Objects.requireNonNull(ServerInfo.getProperty(args[0], ServerProperty.TPS))), false));
                s.sendMessage("§cOnline Players: §f" + ServerInfo.getProperty(args[0], ServerProperty.ONLINE) + "/" + ServerInfo.getProperty(args[0], ServerProperty.MAXIMUM));
                s.sendMessage("§cStatus: §f" + ServerInfo.getStatusFormatted(args[0], false));
            }else {
                s.sendMessage("§c§nRedstone Environment Dump");
                s.sendMessage("");
                s.sendMessage("§cName: §f" + Bukkit.getServerName());
                s.sendMessage("§cGroup: §f" + Redstone.getInstance().getConfig().getString("servergroup"));
            }
        }
    }

    @Command(name = "rsreload", permission = "redstone.reload", description = "Reloads Redstone configuration keys")
    public void reloadCommand(CommandArgs cmd) {
        Redstone.getInstance().reloadConfig();;
        cmd.getSender().sendMessage(ChatColor.GREEN + "Reloaded the Redstone configuration keys!");
    }

}
