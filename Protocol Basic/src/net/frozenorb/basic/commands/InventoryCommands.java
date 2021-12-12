package net.frozenorb.basic.commands;

import net.frozenorb.basic.BasicPlayer;
import net.frozenorb.bridge.bukkit.Bridge;
import net.frozenorb.bridge.bukkit.ranks.Rank;
import net.frozenorb.bridge.utils.MojangUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class InventoryCommands {

    @Command(name = "invsee", permission = "basic.invsee", isAsync = true, inGameOnly = true)
    public void invseeCmd(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        if (args.length != 1) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " <player>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eView a players inventory")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            UUID offUUID;
            try {
                offUUID = MojangUtils.fetchUUID(cmd.getArgs(0));
            } catch (Exception e) {
                p.sendMessage("§cFailed to contact Mojang Servers - try again later?");
                return;
            }
            BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(offUUID);
            OfflinePlayer op = Bukkit.getOfflinePlayer(offUUID);
            if (op == null || !op.hasPlayedBefore() || basicPlayer == null) {
                p.sendMessage("§cNo player with the name \"" + cmd.getArgs()[0] + "\" found.");
                return;
            }
            Rank r = Bridge.getProfileManager().getProfileByUUIDOrCreate(offUUID).getCurrentGrant().getRank();
            Inventory i = Bukkit.createInventory(null, 45, "§1§7Inventory: " + r.getColor() + op.getName());
            i.setContents(basicPlayer.getLastInv());
            i.setItem(36, basicPlayer.getLastArmor()[3]);
            i.setItem(37, basicPlayer.getLastArmor()[2]);
            i.setItem(38, basicPlayer.getLastArmor()[1]);
            i.setItem(39, basicPlayer.getLastArmor()[0]);
            p.openInventory(i);
            return;
        }
    }

    @Command(name = "head", permission = "basic.head", inGameOnly = true)
    public void headCmd(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        if(args.length != 1) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " <player>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eGet a players head")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        ItemStack skull = new ItemStack(Material.SKULL_ITEM);
        SkullMeta meta = (SkullMeta)skull.getItemMeta();

        meta.setOwner(args[0]);
        meta.setDisplayName(args[0] + "'s Head");
        skull.setDurability((short)3);
        skull.setItemMeta(meta);

        p.getInventory().addItem(skull);
        p.sendMessage("§6You were given §f" + args[0] + "§6's head.");
    }

    @Command(name = "clear", aliases = {"ci"}, permission = "basic.clear")
    public void clearCmd(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        if(args.length != 1) {
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);
            p.updateInventory();
            p.sendMessage("§6Your inventory has been cleared.");
            return;
        }
        if(!p.hasPermission("basic.clear.others")) {
            p.sendMessage("§cNo permission.");
            return;
        }
        Player t = Bukkit.getPlayer(args[0]);
        if (t == null) {
            UUID offUUID;
            try {
                offUUID = MojangUtils.fetchUUID(cmd.getArgs(0));
            } catch (Exception e) {
                p.sendMessage("§cFailed to contact Mojang Servers - try again later?");
                return;
            }
            BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(offUUID);
            OfflinePlayer op = Bukkit.getOfflinePlayer(offUUID);
            if (op == null || !op.hasPlayedBefore() || basicPlayer == null) {
                p.sendMessage("§cNo player with the name \"" + cmd.getArgs()[0] + "\" found.");
                return;
            }
            Rank r = Bridge.getProfileManager().getProfileByUUIDOrCreate(offUUID).getCurrentGrant().getRank();
            basicPlayer.setLastInv(null);
            basicPlayer.setLastArmor(null);
            basicPlayer.save();
            p.sendMessage(Bridge.getProfileManager().getProfileByUUIDOrCreate(offUUID).getCurrentGrant().getRank().getColor() + Bukkit.getOfflinePlayer(offUUID).getName() + "§6's inventory has been cleared.");
            return;
        }
        t.getInventory().clear();
        t.getInventory().setArmorContents(new ItemStack[4]);
        t.updateInventory();
        p.sendMessage(Bridge.getProfileManager().getProfileByUUIDOrCreate(t.getUniqueId()).getCurrentGrant().getRank().getColor() + t.getName() + "§6's inventory has been cleared.");
    }

    @Command(name = "lastinv", permission = "basic.lastinv", isAsync = true, inGameOnly = true)
    public void lastInvCmd(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        if(args.length != 1) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " <player>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eGet a players previous inventory")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        UUID offUUID;
        try {
            offUUID = MojangUtils.fetchUUID(cmd.getArgs(0));
        } catch (Exception e) {
            p.sendMessage("§cFailed to contact Mojang Servers - try again later?");
            return;
        }
        BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(offUUID);
        OfflinePlayer op = Bukkit.getOfflinePlayer(offUUID);
        if (op == null || !op.hasPlayedBefore() || basicPlayer == null) {
            p.sendMessage("§cNo player with the name \"" + cmd.getArgs()[0] + "\" found.");
            return;
        }
        if(basicPlayer.getDeathInv() == null) {
            p.sendMessage("§cThere is no previous inventory for " + op.getName());
            return;
        }
        if(basicPlayer.getDeathArmor() != null) p.getInventory().setArmorContents(basicPlayer.getDeathArmor());
        p.getInventory().setContents(basicPlayer.getDeathInv());
        p.sendMessage("§aLoaded " + op.getName() + "'s last inventory.");
    }

    @Command(name = "cpto", permission = "basic.cpto", isAsync = true, inGameOnly = true)
    public void copyInvCmd(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        if(args.length != 1) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " <player>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eCopy your inventory to a player")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        UUID offUUID;
        try {
            offUUID = MojangUtils.fetchUUID(cmd.getArgs(0));
        } catch (Exception e) {
            p.sendMessage("§cFailed to contact Mojang Servers - try again later?");
            return;
        }
        BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(offUUID);
        OfflinePlayer op = Bukkit.getOfflinePlayer(offUUID);
        if (op == null || !op.hasPlayedBefore() || basicPlayer == null) {
            p.sendMessage("§cNo player with the name \"" + cmd.getArgs()[0] + "\" found.");
            return;
        }
        Rank r= Bridge.getProfileManager().getProfileByUUIDOrCreate(offUUID).getCurrentGrant().getRank();
        if(op.isOnline()) {
            Player on = Bukkit.getPlayer(offUUID);
            on.getInventory().setArmorContents(p.getInventory().getArmorContents());
            on.getInventory().setContents(p.getInventory().getContents());
        }else {
            basicPlayer.setLastArmor(p.getInventory().getArmorContents());
            basicPlayer.setLastInv(p.getInventory().getContents());
            basicPlayer.save();
        }
        p.sendMessage("§6Your inventory has been applied to " + r.getColor() + op.getName() + "§6.");
    }

    @Command(name = "cpfrom", aliases = {"cp", "cpinv"}, permission = "basic.cpfrom", isAsync = true, inGameOnly = true)
    public void copyFromCmd(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        if(args.length != 1) {
            ComponentBuilder cp = new ComponentBuilder("Usage: /" + cmd.getLabel() + " <player>").color(ChatColor.RED).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§eCopy a players inventory to you")));
            p.spigot().sendMessage(cp.create());
            return;
        }
        UUID offUUID;
        try {
            offUUID = MojangUtils.fetchUUID(cmd.getArgs(0));
        } catch (Exception e) {
            p.sendMessage("§cFailed to contact Mojang Servers - try again later?");
            return;
        }
        BasicPlayer basicPlayer = BasicPlayer.getBasicPlayer(offUUID);
        OfflinePlayer op = Bukkit.getOfflinePlayer(offUUID);
        if (op == null || !op.hasPlayedBefore() || basicPlayer == null) {
            p.sendMessage("§cNo player with the name \"" + cmd.getArgs()[0] + "\" found.");
            return;
        }
        Rank r= Bridge.getProfileManager().getProfileByUUIDOrCreate(offUUID).getCurrentGrant().getRank();
        ItemStack[] armor;
        ItemStack[] inv;
        if(op.isOnline()) {
            Player t = Bukkit.getPlayer(offUUID);
            armor = t.getInventory().getArmorContents();
            inv = t.getInventory().getContents();
        }else {
            armor = basicPlayer.getLastArmor();
            inv = basicPlayer.getLastInv();
        }
        p.getInventory().setArmorContents(armor);
        p.getInventory().setContents(inv);
        p.sendMessage(r.getColor() + op.getName() + "§6's " + "inventory has been applied to you.");
    }

}
