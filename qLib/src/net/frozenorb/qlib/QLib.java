package net.frozenorb.qlib;

import lombok.Getter;
import net.frozenorb.qlib.command.CommandFramework;
import net.frozenorb.qlib.commands.QLibCommands;
import net.frozenorb.qlib.listeners.QLibListener;
import net.frozenorb.qlib.utils.EntityHider;
import net.frozenorb.qlib.utils.Messages;
import net.frozenorb.qlib.utils.SignHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class QLib extends JavaPlugin {

    @Getter private static QLib instance;
    @Getter private static Messages messages;
    @Getter private static EntityHider entityHider;
    @Getter private static SignHandler signHandler;
    @Getter private CommandFramework commandFramework;

    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        messages = new Messages();
        entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);
        signHandler = new SignHandler(this);
        this.commandFramework = new CommandFramework(this);
        commandFramework.registerCommands(new QLibCommands());
        commandFramework.registerHelp();
        Bukkit.getPluginManager().registerEvents(new QLibListener(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "Lunar-Client");
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        instance = null;

    }
}
