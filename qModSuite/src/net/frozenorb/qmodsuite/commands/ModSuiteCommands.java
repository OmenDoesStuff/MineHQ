package net.frozenorb.qmodsuite.commands;

import net.frozenorb.bridge.bukkit.Bridge;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.CommandArgs;
import net.frozenorb.qmodsuite.QModSuite;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ModSuiteCommands {

    @Command(name = "modmode", aliases = {"modsuite"}, inGameOnly = true)
    public void modModeCommand(CommandArgs cmd){
        Player p = cmd.getPlayer();
        boolean isstaff = Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank().isStaff();
        if(!isstaff){
            p.sendMessage(ChatColor.RED + "No permission.");
            return;
        }
        if(QModSuite.getInstance().getModModeHandler().switchModMode(p)){
            p.sendMessage(ChatColor.GOLD + "Mod Mode: " + ChatColor.GREEN + "Enabled");
        } else {
            p.sendMessage(ChatColor.GOLD + "Mod Mode: " + ChatColor.RED + "Disabled");
        }
    }

}
