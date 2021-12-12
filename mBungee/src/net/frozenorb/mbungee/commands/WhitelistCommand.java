package net.frozenorb.mbungee.commands;

import net.frozenorb.bridge.bungee.Bridge;
import net.frozenorb.bridge.bungee.ranks.Rank;
import net.frozenorb.bridge.utils.MojangUtils;
import net.frozenorb.mbungee.MBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class WhitelistCommand extends Command {

    public WhitelistCommand() {
        super("whitelist");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(commandSender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) commandSender;
            Rank r = Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank();
            if(r.getPriority() < 90){
                p.sendMessage("§cNo permission.");
                return;
            }
            if(args.length < 1) {
                p.sendMessage("§cUsage: /whitelist <on:off:add/remove name>");
                p.sendMessage("§cExample: /whitelist add D0an");
                return;
            }
            if(args[0].equalsIgnoreCase("on")){
                p.sendMessage("§aThe server is §cnow §awhitelisted.");
                MBungee.getInstance().getBungeeHandler().setWhitelisted(true);
                return;
            }
            if(args[0].equalsIgnoreCase("off")){
                p.sendMessage("§aThe server is §fno longer §awhitelisted.");
                MBungee.getInstance().getBungeeHandler().setWhitelisted(false);
                return;
            }
            if(args[0].equalsIgnoreCase("list")){
                p.sendMessage("");
                p.sendMessage("§aPeople whitelisted:");
                String error = null;
                for(String uuid : MBungee.getInstance().getBungeeHandler().getWhitelists()){
                    try {
                        p.sendMessage(" §a- §f" + MojangUtils.fetchName(UUID.fromString(uuid)));
                    } catch (Exception e) {
                        error = "§cSomething went wrong while loading the whitelist.";
                    }
                }
                if(error != null) {
                    p.sendMessage("");
                    p.sendMessage(error);
                }
                p.sendMessage("");
                return;
            }
            try {
                if (args.length >= 2) {
                    if (args[0].equalsIgnoreCase("add")) {
                        String uuid = args[1];
                        MBungee.getInstance().getBungeeHandler().setWhitelisted(
                                MojangUtils.fetchUUID(uuid).toString(), true);
                        p.sendMessage("§aYou succesfully added §f" + uuid + " §ato the whitelist!");
                        return;
                    }
                    if (args[0].equalsIgnoreCase("remove")) {
                        String uuid = args[1];
                        MBungee.getInstance().getBungeeHandler().setWhitelisted(MojangUtils.fetchUUID(uuid).toString(), false);
                        p.sendMessage("§cYou succesfully removed §f" + uuid + " §cfrom the whitelist!");
                        return;
                    }
                }
            }
            catch (Exception ignore){}

            p.sendMessage("§cUsage: /whitelist <on:off:list:add/remove name>");
            p.sendMessage("§cExample: /whitelist add D0an");

        }
    }

}