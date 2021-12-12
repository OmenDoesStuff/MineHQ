package net.frozenorb.qmodsuite.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
public class ModProfile {

    @Getter private static Set<ModProfile> modProfiles = new HashSet<>();

    private UUID uuid;
    private int reports = 0;
    private long staffCooldown = 0;

    public ModProfile(UUID uuid) {
        this.uuid = uuid;
        modProfiles.add(this);
    }

    public static ModProfile getModProfile(Player p) {
        return modProfiles.stream().filter(modProfile -> modProfile.getUuid().toString().equalsIgnoreCase(p.getUniqueId().toString())).findFirst().orElse(null);
    }

    public static ModProfile getModProfile(UUID p) {
        return modProfiles.stream().filter(modProfile -> modProfile.getUuid().toString().equalsIgnoreCase(p.toString())).findFirst().orElse(null);
    }

}
