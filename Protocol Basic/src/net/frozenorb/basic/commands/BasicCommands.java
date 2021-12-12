package net.frozenorb.basic.commands;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.CommandArgs;
import net.frozenorb.qlib.utils.Messages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;


public class BasicCommands {

    @Command(name = "buy", aliases = {"donate","store"}, description = "Be awesome!", inGameOnly = true)
    public void buyCommand(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        ComponentBuilder cp = new ComponentBuilder("You can purchase a rank at: ").color(ChatColor.YELLOW).append("store.protocol.rip").color(ChatColor.LIGHT_PURPLE).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://store.protocol.rip")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aClick to open the store!"))).append(".").reset().color(ChatColor.YELLOW);
        p.spigot().sendMessage(cp.create());
    }

    @Command(name = "hologram", aliases = {"holo"})
    public void holoCommand(CommandArgs cmd) {
        cmd.getSender().sendMessage("§cNo permission.");
    }

    @Command(name = "rules", aliases = {"rule"}, inGameOnly = true)
    public void rulesCommand(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        ComponentBuilder cp = new ComponentBuilder("§b§nClick here").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Click here to go to the rules page."))).event(new ClickEvent((ClickEvent.Action.OPEN_URL), "https://protocol.rip/rules"));
        cp.append("§e to view the rules.", ComponentBuilder.FormatRetention.NONE);
        p.spigot().sendMessage(cp.create());
        return;
    }

    @Command(name = "ping", inGameOnly = true)
    public void pingCommand(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        Player t;
        if(args.length == 0) {
            t = p;
        }else {
            t = Bukkit.getPlayer(args[0]);
            if(t == null || !p.canSee(t)) {
                p.sendMessage("§cNo player with the name \"" + args[0] + "\" found.");
                return;
            }
        }
        p.sendMessage(t.getDisplayName() + "§e's Ping: §c" + ((CraftPlayer)t).getHandle().ping);
        if(Messages.isHubMode()) {
            p.sendMessage("§c§oPlease check your ping on a game server for a more accurate result");
        }
    }

}
