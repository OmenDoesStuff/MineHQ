package net.frozenorb.mbungee.commands;

import net.frozenorb.bridge.bungee.Bridge;
import net.frozenorb.bridge.bungee.ranks.Rank;
import net.frozenorb.mbungee.MBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HubCommand extends Command {

    public HubCommand() {
        super("hub");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(commandSender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) commandSender;
            Rank r = Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank();
            if(r.isStaff() || r.getPriority() >= 45){
                if(args.length == 0){
                    p.sendMessage("§6Sending you to the restricted hub...");
                    p.sendMessage("§6Visit a specific hub with /hub <id>");
                    p.connect(MBungee.getInstance().getProxy().getServerInfo("Restricted-Hub"));
                    return;
                }
                try {
                    int hub = Integer.parseInt(args[0]);
                    p.connect(MBungee.getInstance().getProxy().getServerInfo("Hub-" + hub));
                    p.sendMessage("§6Sending you to Hub-" + hub + "...");
                } catch(Exception e){
                    p.sendMessage("§cSomething went wrong...");
                }
                return;
            }
            List<ServerInfo> hubs = new ArrayList<>();
            for(ServerInfo s : MBungee.getInstance().getProxy().getServers().values()){
                if(!s.getName().toLowerCase().contains("restricted") && s.getName().toLowerCase().contains("hub") && s != p.getServer().getInfo()) hubs.add(s);
            }
            Random rand = new Random();
            ServerInfo si = hubs.get(rand.nextInt(hubs.size()));
            p.sendMessage("§6Sending you to " + si.getName() + "...");
            p.connect(si);
        }
    }

}
