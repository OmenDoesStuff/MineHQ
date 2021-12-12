package net.frozenorb.mbungee;

import com.google.common.io.ByteStreams;
import lombok.Getter;
import net.frozenorb.bridge.utils.MojangUtils;
import net.frozenorb.mbungee.commands.*;
import net.frozenorb.mbungee.handlers.BungeeHandler;
import net.frozenorb.mbungee.listeners.BungeeListener;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MBungee extends Plugin {
    /*
        Global:
        §9[Staff] §4D0an §cleft §bthe network (from Hub-1)
        §9[Staff] §4D0an §ajoined §bthe network (Hub-Restricted)

        Per server:
        §9[Staff] §4D0an §bjoined your server (from Restricted-Hub)
        §9[Staff] §4D0an §bleft your server (to Restricted-Hub)
    */

    @Getter private List<ProxiedPlayer> staff;
    @Getter private static MBungee instance;
    @Getter private BungeeHandler bungeeHandler;
    private File configFile;
    @Getter private Configuration config;

    @Override
    public void onEnable() {
        instance = this;
        setupConfig();
        staff = new ArrayList<>();
        bungeeHandler = new BungeeHandler();
        getProxy().getPluginManager().registerListener(this, new BungeeListener());
        getProxy().getPluginManager().registerCommand(this, new HubCommand());
        getProxy().getPluginManager().registerCommand(this, new ServerCommand());
        getProxy().getPluginManager().registerCommand(this, new WhitelistCommand());
    }

    @Override
    public void onDisable() {
        config.set("whitelists", bungeeHandler.getWhitelists());
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupConfig() {
        configFile = new File(getDataFolder(), "config.yml");
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }
    }
}
