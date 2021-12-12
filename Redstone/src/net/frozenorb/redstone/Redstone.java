package net.frozenorb.redstone;

import lombok.Getter;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.CommandFramework;
import net.frozenorb.qlib.utils.Messages;
import net.frozenorb.redstone.commands.RedstoneCommands;
import net.frozenorb.redstone.property.ServerProperty;
import net.frozenorb.redstone.property.ServerStatus;
import net.frozenorb.redstone.tasks.ServerTask;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class Redstone extends JavaPlugin {

    @Getter private static Redstone instance;
    @Getter private JedisPool jedisPool;
    private boolean redisConnected = false;
    @Getter private ServerTask serverTask;
    @Getter private BukkitTask serverBukkitTask;
    @Getter private CommandFramework commandFramework;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        initRedis();

        if(redisConnected) {
            this.commandFramework = new CommandFramework(this);
            serverTask = new ServerTask();
            serverBukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, serverTask, 0, 10);
            serverTask.setShouldSync(true);
            commandFramework.registerCommands(new RedstoneCommands());
            commandFramework.registerHelp();
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Redstone] Server Data is " + (redisConnected ? "currently being logged as Redis is connected!" : "not being logged as Redis is not connected."));

    }

    @Override
    public void onDisable() {
        serverTask.setShouldSync(false);
        serverTask.save(ServerProperty.STATUS, ServerStatus.OFFLINE.name());
        serverTask.save(ServerProperty.ONLINE, "0");
        Bukkit.getScheduler().cancelTasks(this);
        jedisPool.destroy();
        instance = null;
    }

    private void initRedis() {
        // Initialize Redis
        System.out.println(Messages.isRedisAuth() + " " + Messages.getRedisIP() + ":" + Messages.getRedisPort() + " " + Messages.getRedisPassword());
        if(!Messages.isRedisAuth()) {
            jedisPool = new JedisPool(new JedisPoolConfig(), Messages.getRedisIP(), Messages.getRedisPort(), 0);
        }else {
            jedisPool = new JedisPool(new JedisPoolConfig(), Messages.getRedisIP(), Messages.getRedisPort(), 0, Messages.getRedisPassword());
        }
        try {
            Jedis jedis = getJedisPool().getResource();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Redstone] Connected to Redis!");
            redisConnected = true;
            getJedisPool().returnResource(jedis);
        }
        catch (JedisConnectionException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Redstone] Failed to connect to Redis!");
            redisConnected = false;
        }
    }
}
