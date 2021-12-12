package net.frozenorb.qsite;

import lombok.Getter;
import net.frozenorb.qsite.listeners.QSiteListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class QSite extends JavaPlugin {

    @Getter private static QSite instance;
    @Getter private static MongoManager mongoManager;

    @Override
    public void onEnable() {
        instance = this;
        mongoManager = new MongoManager();
        Bukkit.getPluginManager().registerEvents(new QSiteListener(), this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
