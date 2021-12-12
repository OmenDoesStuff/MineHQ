package net.frozenorb.qmodsuite.punish.utils;

import lombok.Getter;

public enum PunishmentTypes {

    KICK("Kick", "kicked", null, false, false, false, false),
    WARN("Warn", "warned", null, false, false, false, false),
    MUTE("Mute", "muted", "unmuted", false, false, true, true),
    BAN("Ban", "banned", "unbanned", true, false, false, true),
    IPBAN("IP Ban", "ipbanned", "unipbanned", true, true, false, true),
    BLACKLIST("Blacklist", "blacklisted", "unblacklisted", true, true, false, true);

    @Getter private String name;
    @Getter private String executeText;
    @Getter private String undoText;
    @Getter private boolean ban;
    @Getter private boolean ipBan;
    @Getter private boolean mute;
    @Getter private boolean pardon;

    PunishmentTypes(String name, String executeText, String undoText, boolean ban, boolean ipBan, boolean mute, boolean pardon) {
        this.name = name;
        this.executeText = executeText;
        this.undoText = undoText;
        this.ban = ban;
        this.ipBan = ipBan;
        this.mute = mute;
        this.pardon = pardon;
    }

}
