package net.frozenorb.basic;

import lombok.Getter;
import net.frozenorb.basic.commands.BasicCommands;
import net.frozenorb.basic.commands.InventoryCommands;
import net.frozenorb.basic.commands.MessageCommands;
import net.frozenorb.basic.commands.TeleportCommands;
import net.frozenorb.basic.listeners.BasicListener;
import net.frozenorb.qlib.command.CommandFramework;
import net.frozenorb.qlib.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Basic extends JavaPlugin {

    @Getter private static Basic instance;
    @Getter private CommandFramework commandFramework;
    @Getter private Config playerDataConfig;

    @Override
    public void onEnable() {
        instance = this;
        this.playerDataConfig = new Config(this, "playerdata");
        this.commandFramework = new CommandFramework(this);
        commandFramework.registerCommands(new BasicCommands());
        commandFramework.registerCommands(new MessageCommands());

        commandFramework.registerHelp();

        //Commands that shouldn't be in /help
        commandFramework.registerCommands(new TeleportCommands());
        commandFramework.registerCommands(new InventoryCommands());
        Bukkit.getPluginManager().registerEvents(new BasicListener(), this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

}
