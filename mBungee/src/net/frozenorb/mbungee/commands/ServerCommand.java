package net.frozenorb.mbungee.commands;

import net.frozenorb.bridge.bungee.Bridge;
import net.frozenorb.bridge.bungee.ranks.Rank;
import net.frozenorb.mbungee.MBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ServerCommand extends Command {

    public ServerCommand() {
        super("server");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(commandSender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) commandSender;
            Rank r = Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank();
            if(r.isStaff()){
                if(args.length == 0){
                    p.sendMessage("§6You are currently connected to §f" + p.getServer().getInfo().getName() + "§6.");
                    String servers = "§6Servers: §f";
                    boolean first = true;
                    for(String name : MBungee.getInstance().getProxy().getServers().keySet()){
                        if(first){
                            servers += name;
                        } else {
                            servers += "§7, §f" + name;
                        }
                        first = false;
                    }
                    p.sendMessage(servers);
                    p.sendMessage("§6Connect to a server with §e/server <name>");
                } else if (args.length == 1){
                    ServerInfo si = MBungee.getInstance().getProxy().getServerInfo(args[0]);
                    if(si != null){
                        p.sendMessage("§6Connecting you to §f" + si.getName() + "§6!");
                        p.connect(si);
                    } else {
                        p.sendMessage("§cNo server by the name §f" + args[0] + "§c found!");
                    }
                }
            } else {
                p.sendMessage("§cYou do not have permission to execute this command!");
            }
        }
    }

}
