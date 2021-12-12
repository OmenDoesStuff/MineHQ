package net.frozenorb.qsite.backend;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.qsite.QSite;


import java.util.HashSet;
import java.util.UUID;

@Getter @Setter
public class SiteUser {

    private String username, hash = "", salt = "", IGN = "";
    private UUID uuid;
    private boolean registered;

    public SiteUser(String username) {
        this.username = username;
        QSite.getMongoManager().loadProfile(this.username, profile -> {
            if (profile != null) {
                this.importSettings(profile);
            }
        });

    }

    private void importSettings(SiteUser profile) {
        this.IGN = profile.getIGN();
        this.uuid = profile.getUuid();
        this.registered = profile.isRegistered();
    }

    public void saveProfile() {
        QSite.getMongoManager().saveProfile(this, callback -> {
            if (callback) {
                System.out.println("[qSite] §aSuccessfully saved §f" + getIGN() + "§a.");
            } else {
                System.err.println("[qSite] §cFailed to save §f" + getIGN() + "§c.");
            }
        });
    }




}
