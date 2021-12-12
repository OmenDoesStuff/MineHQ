package net.frozenorb.redstone.tasks;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.redstone.Redstone;
import net.frozenorb.redstone.property.ServerProperty;
import net.frozenorb.redstone.property.ServerStatus;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;

import java.util.stream.Stream;

public class ServerTask implements Runnable {

    @Getter @Setter private boolean shouldSync;
    @Getter private static Table serverDataTable = HashBasedTable.create();

    @Override
    public void run() {
        if(shouldSync) {
            long start = System.currentTimeMillis();
            save(ServerProperty.ONLINE, "" + Bukkit.getOnlinePlayers().size());
            save(ServerProperty.MAXIMUM, "" + Bukkit.getMaxPlayers());
            save(ServerProperty.STATUS, ServerStatus.getCurrentStatus().name());
            save(ServerProperty.MOTD, Bukkit.getMotd());
            save(ServerProperty.TPS, "" + Bukkit.spigot().getTPS()[0]);
            save(ServerProperty.LASTUPDATED, "" + (System.currentTimeMillis() - start));
            save(ServerProperty.GROUP, Redstone.getInstance().getConfig().getString("servergroup"));
            try {
                Jedis end = Redstone.getInstance().getJedisPool().getResource();

                try {
                    Stream.of(ServerProperty.values()).forEach((property) -> {
                        end.hgetAll(property.getJedisId()).forEach((server, value) -> {
                            getServerDataTable().put(server, property, value);
                        });
                    });
                } catch (Throwable ignored) {
                } finally {
                    if (end != null) {
                        end.close();
                    }

                }
            } catch (Throwable ignored) {}
        }
    }

    public void save(ServerProperty prop, String data) {
        if (Redstone.getInstance().getJedisPool() != null) {
            try {
                Jedis jedis = Redstone.getInstance().getJedisPool().getResource();
                Throwable throwable = null;

                try {
                    jedis.hset(prop.getJedisId(), Bukkit.getServerName(), data);
                } catch (Throwable throwable1) {
                    throwable = throwable1;
                    throw throwable1;
                } finally {
                    if (jedis != null) {
                        if (throwable != null) {
                            try {
                                jedis.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        } else {
                            jedis.close();
                        }
                    }

                }
            } catch (Throwable ignored) {

            }
        }
    }



}
