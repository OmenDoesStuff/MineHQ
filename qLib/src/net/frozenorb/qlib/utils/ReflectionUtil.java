package net.frozenorb.qlib.utils;


import net.minecraft.util.io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {

    public static Class<?> getClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + className);
    }

    public static void sendPacket(Player player, Object packet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, ClassNotFoundException {
        Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
        Object playerConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
        playerConnection.getClass().getMethod("sendPacket", getClass("Packet")).invoke(playerConnection, packet);
    }

    public void setField(Object instance, String field, Object value) {
        try {
            Field fieldObject = instance.getClass().getDeclaredField(field);
            fieldObject.setAccessible(true);
            fieldObject.set(instance, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getField(Object instance, String fieldName) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    public static Object getMethod(Object instance, String methodName) throws Exception {
        Method method = instance.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(instance);
    }

    public static Channel getChannel(Player player) throws Exception {
        Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
        Object playerConnection = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
        Object networkManager = getField(playerConnection, "networkManager");

        Channel channel = null;
        try {
            channel = (Channel) getField(networkManager, "m");
            if (channel == null) {
                channel = (Channel) getField(networkManager, "i");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return channel;
    }

    public static void registerCommand(JavaPlugin plugin, Command command) {
        Field bukkitCommandMap = null;
        try {
            bukkitCommandMap = plugin.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        CommandMap commandMap;
        try {
            commandMap = (org.bukkit.command.CommandMap) bukkitCommandMap.get(plugin.getServer());
            commandMap.register(plugin.getDescription().getName(), command);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}