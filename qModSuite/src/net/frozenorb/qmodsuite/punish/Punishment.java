package net.frozenorb.qmodsuite.punish;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.bridge.bukkit.Bridge;
import net.frozenorb.bridge.bukkit.utils.TimeUtil;
import net.frozenorb.qmodsuite.punish.utils.PunishmentTypes;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Setter @Getter
public class Punishment {

    @Getter private UUID punishID;
    @Getter private UUID uuid;
    @Getter private UUID executor;
    @Getter private UUID pardonedBy;
    @Getter private PunishmentTypes punishmentTypes;
    //@Getter private String punishedServer;
    @Getter private String reason;
    @Getter private String pardonedReason;
    //@Getter private String pardonedServer;
    @Getter private String IP;
    @Getter private long time;
    @Getter private long duration;
    @Getter private long pardonedAt;
    @Getter private boolean pardoned;

    public Punishment(UUID punishmentID, UUID uuid, PunishmentTypes type, long time, String reason, long duration, String playerIP) {
        this.punishID = punishmentID;
        this.uuid = uuid;
        this.punishmentTypes = type;
        this.time = time;
        this.reason = reason;
        this.duration = duration;
        this.IP = playerIP;
    }

    public Punishment(UUID punishmentID, UUID uuid, UUID executor, UUID pardonedBy, PunishmentTypes type, String reason, String pardonedReason, long time, long duration, long pardonedAt, boolean pardoned, String playerIP) {
        this.punishID = punishmentID;
        this.uuid = uuid;
        this.executor = executor;
        this.pardonedBy = pardonedBy;
        this.punishmentTypes = type;
        this.reason = reason;
        this.pardonedReason = pardonedReason;
        this.time = time;
        this.duration = duration;
        this.pardonedAt = pardonedAt;
        this.pardoned = pardoned;
        this.IP = playerIP;
    }

    public boolean isPermanent() {
        return this.punishmentTypes == PunishmentTypes.BLACKLIST || this.duration == Long.MAX_VALUE;
    }

    public boolean isActive() {
        return !this.pardoned && (this.isPermanent() || this.getRemainingTime() < 0L);
    }

    public long getRemainingTime() {
        return System.currentTimeMillis() - (this.time + this.duration);
    }

    public String getRemainingString() {
        if (this.pardoned) {
            return "Pardoned";
        }
        if (this.isPermanent()) {
            return "Permanent";
        }
        if (!this.isActive()) {
            return "Expired";
        }
        return TimeUtil.millisToRoundedTime(this.time + this.duration - System.currentTimeMillis());
    }

    public String getStatusString() {
        if (this.pardoned) {
            return "Pardoned";
        }
        if (this.isPermanent()) {
            return "Permanent";
        }
        if (!this.isActive()) {
            return "Expired";
        }
        return "Active";
    }

    public String getDurationString() {
        if (this.isPermanent()) {
            return "Permanent";
        }
        return TimeUtil.millisToRoundedTime(this.duration);
    }

    public String getKickMessage() {
        switch (this.punishmentTypes) {
            case BAN: {
                String message = "§cYour account has been suspended from the Protocol Network.\n§cAppeal at protocol.rip/appeal";
                if (!this.isPermanent()) {
                    message = "§cYour account has been suspended from the Protocol Network.\n§cExpires in " + this.getRemainingString();
                }
                return message;
            }
            case BLACKLIST: {
                return "§cYour account has been blacklisted from the Protocol Network.\n§cThis type of punishment cannot be appealed.";
            }
            case KICK: {
                return "§eKicked by a staff member: " + this.reason;
            }
            default: {
                return "§cERR ID: 0x01";
            }
        }
    }

    public void broadcastPunishment(String sender, String target, String reason, String duration, boolean silent) {
        String message = "§f" + target + " §awas " + (silent ? "§esilently §a" : "") + this.getDisplayString() + " by §f" + sender;
        Bukkit.getConsoleSender().sendMessage(message);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (silent && !Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank().isStaff()) {
                return;
            }

//            ComponentBuilder cp = new ComponentBuilder("You can purchase a rank at: ").color(ChatColor.YELLOW).append("store.protocol.rip").color(ChatColor.LIGHT_PURPLE).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://store.protocol.rip")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aClick to open the store!"))).append(".").reset().color(ChatColor.YELLOW);
//            p.spigot().sendMessage(cp.create());
            if(!Bridge.getProfileManager().getProfileByUUID(p.getUniqueId()).getCurrentGrant().getRank().isStaff()) {
                message = "§a" + ChatColor.stripColor(target) + " was " + this.getDisplayString() + " by " + ChatColor.stripColor(sender);
            }else {
                ComponentBuilder cp = new ComponentBuilder(target).append(" was " + this.getDisplayString() + " by ").color(ChatColor.GREEN).append(sender);
                p.spigot().sendMessage(cp.create());
                return;
            }
            p.sendMessage(message);
        }

//        if (this.type == PunishmentType.WARN && !this.pardoned) {
//            Player tp = Bukkit.getPlayer(this.uuid);
//            if (tp != null && tp.isOnline()) {
//                tp.sendMessage("§cYou have been warned by §r" + sender + " §cfor: §r" + this.reason + "§c.");
//            }
//        }
//        if (this.type == PunishmentType.MUTE) {
//            Player tp = Bukkit.getPlayer(this.uuid);
//            if (tp != null && tp.isOnline()) {
//                if (this.pardoned) {
//                    tp.sendMessage("§cYou have been " + this.getDisplayString() + ".");
//                }
//                else {
//                    tp.sendMessage("§cYou have been " + this.getDisplayString() + " by §r" + sender + " §cfor: §r" + this.reason + "§c.");
//                }
//            }
//        }
    }

    public String getDisplayString() {
        if (this.punishmentTypes != PunishmentTypes.BAN && this.punishmentTypes != PunishmentTypes.MUTE) {
            return this.pardoned ? this.punishmentTypes.getUndoText() : this.punishmentTypes.getExecuteText();
        }
        if (this.isPermanent()) {
            return this.pardoned ? this.punishmentTypes.getUndoText() : (this.punishmentTypes.getExecuteText());
        }
        return this.pardoned ? this.punishmentTypes.getUndoText() : ("temporarily " + this.punishmentTypes.getExecuteText());
    }

}
