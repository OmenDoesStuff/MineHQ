package net.frozenorb.qmodsuite;

import lombok.Getter;
import net.frozenorb.qlib.command.CommandFramework;
import net.frozenorb.qmodsuite.commands.GlobalCommands;
import net.frozenorb.qmodsuite.commands.ModSuiteCommands;
import net.frozenorb.qmodsuite.commands.PunishCommands;
import net.frozenorb.qmodsuite.handlers.ModModeHandler;
import net.frozenorb.qmodsuite.listeners.ModModeListener;
import net.frozenorb.qmodsuite.punish.PunishBackend;
import net.frozenorb.qmodsuite.utils.redis.ModSubscriber;
import net.frozenorb.redstone.Redstone;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

public class QModSuite extends JavaPlugin {

    @Getter private ModModeHandler modModeHandler;
    @Getter private static QModSuite instance;
    @Getter private static PunishBackend punishBackend;
    private CommandFramework commandFramework;
    private ModSubscriber modSubscriber;

    @Override
    public void onEnable() {
        instance = this;
        this.commandFramework = new CommandFramework(this);
        commandFramework.registerCommands(new ModSuiteCommands());
        commandFramework.registerCommands(new GlobalCommands());
        commandFramework.registerCommands(new PunishCommands());
        modModeHandler = new ModModeHandler();
        modSubscriber = new ModSubscriber();
        Bukkit.getPluginManager().registerEvents(new ModModeListener(), this);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            Jedis jedis = Redstone.getInstance().getJedisPool().getResource();
            jedis.subscribe(modSubscriber, "qmodsuite");
            Redstone.getInstance().getJedisPool().returnResource(jedis);
        });
        (punishBackend = new PunishBackend()).initializeBackend(callback -> {
            if (!callback) {
                System.out.println("[qModSuite] Failed to load Mongo Backend");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }else {
                return;
            }
        });
        punishBackend.loadFromDatabase();

    }

    @Override
    public void onDisable(){
        modSubscriber.unsubscribe();
        instance = null;
    }
}