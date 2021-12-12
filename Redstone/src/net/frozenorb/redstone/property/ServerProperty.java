package net.frozenorb.redstone.property;

import net.frozenorb.qlib.utils.Messages;

public enum ServerProperty {

    ONLINE, MAXIMUM, STATUS, MOTD, TPS, LASTUPDATED, GROUP;

    public String getJedisId() {
        return "server-data-" + Messages.getName() + ":" + this.name().toLowerCase();
    }
}