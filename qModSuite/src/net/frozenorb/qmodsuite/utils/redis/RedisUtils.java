package net.frozenorb.qmodsuite.utils.redis;

import net.frozenorb.redstone.Redstone;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.List;

public class RedisUtils {

    public static void buildRedisMessage(String... args) {
        buildRedisMessage(Arrays.asList(args));
    }


    public static void buildRedisMessage(List<String> args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(builder.toString().equals("")?"":"¸").append(arg);
        }
        Jedis jedis = Redstone.getInstance().getJedisPool().getResource();
        jedis.publish("qmodsuite", builder.toString());
        Redstone.getInstance().getJedisPool().returnResource(jedis);
    }

}
