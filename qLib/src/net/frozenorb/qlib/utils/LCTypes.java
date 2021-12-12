package net.frozenorb.qlib.utils;

import lombok.Getter;

public class LCTypes {

    public static enum StaffModes {
        BUNNYHOP("bunnyhop"),
        XRAY("xray"),
        NAMETAGS("nametags"),
        NOCLIP("noclip");

        @Getter private String packetName;

        StaffModes(String packetName) {
            this.packetName = packetName;
        }

    }

    public static enum Emotes {

    }

}
