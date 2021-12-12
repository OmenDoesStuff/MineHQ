package net.frozenorb.qlib.listeners;

import net.frozenorb.pclientapi.PClientAPI;
import net.frozenorb.pclientapi.utils.WaypointColor;
import net.frozenorb.qlib.QLib;
import net.frozenorb.qlib.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class QLibListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if(e.getMessage().startsWith("!")) {
            e.setCancelled(true);
            p.chat(e.getMessage().replaceFirst("!", ""));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(QLib.getInstance(), () -> PClientAPI.getInstance().updateServer(p, "Protocol | " + Bukkit.getServerName()), 10);

        if(Messages.isHubMode()) {
            Bukkit.getScheduler().runTaskLater(QLib.getInstance(), () -> PClientAPI.getInstance().toggleWaypoints(p, true), 10);

            return;
        }
        if(Bukkit.getPluginManager().getPlugin("HCTeams") != null) {
            Bukkit.getScheduler().runTaskLater(QLib.getInstance(), () -> {
                PClientAPI.getInstance().toggleWaypoints(p, true);
                PClientAPI.getInstance().toggleMinimap(p, true);
                PClientAPI.getInstance().createWaypoint(p, "Spawn", p.getWorld().getSpawnLocation(), WaypointColor.WHITE, true, true);
                PClientAPI.getInstance().updateWorld(p, p.getWorld().getUID().toString());
            }, 10);


            return;
        }

    }


//    @EventHandler
//    public void waypointKek(PlayerJoinEvent e) {
//        Player p = e.getPlayer();
//        Bukkit.getScheduler().runTaskLater(QLib.getInstance(), () -> {
//            if(Messages.isHubMode()) {
//                LunarClientAPI.getInstance().toggleWaypoints(p, true);
//                return;
//            }
//            ByteBufWrapper wrappedBuffer = new ByteBufWrapper(Unpooled.buffer());
//            wrappedBuffer.writeVarInt(23);
//            wrappedBuffer.writeString("Spawn");
//            wrappedBuffer.writeString(p.getLocation().getWorld().getUID().toString());
//            wrappedBuffer.writeInt(-1);
//            wrappedBuffer.writeInt(p.getLocation().getBlockX());
//            wrappedBuffer.writeInt(p.getLocation().getBlockY());
//            wrappedBuffer.writeInt(p.getLocation().getBlockZ());
//            wrappedBuffer.writeBoolean(true);
//            wrappedBuffer.writeBoolean(true);
//
//            try {
//                Constructor constructor = website.vaperion.lunarclientapi.utils.ReflectionUtil.getClass("PacketPlayOutCustomPayload").getConstructor(String.class, ByteBuf.class);
//                Constructor serializerConstructor = website.vaperion.lunarclientapi.utils.ReflectionUtil.getClass("PacketDataSerializer").getConstructor(ByteBuf.class);
//                Object packet = constructor.newInstance("Lunar-Client", serializerConstructor.newInstance(Unpooled.wrappedBuffer(wrappedBuffer.buf().array())));
//                website.vaperion.lunarclientapi.utils.ReflectionUtil.sendPacket(p, packet);
//            } catch (Exception var11) {
//                var11.printStackTrace();
//                p.sendMessage("§cFailed to set a waypoint. Please contact a developer.");
//            }
//
//            LunarClientAPI.getInstance().toggleWaypoints(p, true);
//
//
////        LunarClientAPI.getInstance().createWaypoint(p, "Spawn", new Location(Bukkit.getWorld("world"), 0, 100, 0), -1, true, true);
////        LunarClientAPI.getInstance().toggleWaypoints(p, true);
////
//            wrappedBuffer = new ByteBufWrapper(Unpooled.buffer());
//            wrappedBuffer.writeVarInt(10);
//            wrappedBuffer.writeString(ServerRule.MINIMAP_STATUS.getRule());
//            wrappedBuffer.writeBoolean(true);
//            wrappedBuffer.writeInt(0);
//            wrappedBuffer.buf().writeFloat(0.0F);
//            wrappedBuffer.writeString("NEUTRAL");
//
//            try {
//                Constructor constructor = website.vaperion.lunarclientapi.utils.ReflectionUtil.getClass("PacketPlayOutCustomPayload").getConstructor(String.class, ByteBuf.class);
//                Constructor serializerConstructor = website.vaperion.lunarclientapi.utils.ReflectionUtil.getClass("PacketDataSerializer").getConstructor(ByteBuf.class);
//                Object packet = constructor.newInstance("Lunar-Client", serializerConstructor.newInstance(Unpooled.wrappedBuffer(wrappedBuffer.buf().array())));
//                website.vaperion.lunarclientapi.utils.ReflectionUtil.sendPacket(p, packet);
//            } catch (Exception var7) {
//                var7.printStackTrace();
//                p.sendMessage("§cFailed to set waypoint mode. Please contact a developer.");
//            }
////
//            wrappedBuffer = new website.vaperion.lunarclientapi.utils.ByteBufWrapper(Unpooled.buffer());
//            wrappedBuffer.writeVarInt(15);
//            wrappedBuffer.writeString(p.getLocation().getWorld().getUID().toString());
//
//            try {
//                Constructor constructor = website.vaperion.lunarclientapi.utils.ReflectionUtil.getClass("PacketPlayOutCustomPayload").getConstructor(String.class, ByteBuf.class);
//                Constructor serializerConstructor = website.vaperion.lunarclientapi.utils.ReflectionUtil.getClass("PacketDataSerializer").getConstructor(ByteBuf.class);
//                Object packet = constructor.newInstance("Lunar-Client", serializerConstructor.newInstance(Unpooled.wrappedBuffer(wrappedBuffer.buf().array())));
//                website.vaperion.lunarclientapi.utils.ReflectionUtil.sendPacket(p, packet);
//            } catch (Exception var7) {
//                var7.printStackTrace();
//                p.sendMessage("§cFailed to update world. Please contact a developer.");
//            }
//            wrappedBuffer = new ByteBufWrapper(Unpooled.buffer());
//            wrappedBuffer.writeVarInt(23);
//            wrappedBuffer.writeString("Spawn");
//            wrappedBuffer.writeString(p.getLocation().getWorld().getUID().toString());
//            wrappedBuffer.writeInt(-1);
//            wrappedBuffer.writeInt(p.getLocation().getBlockX());
//            wrappedBuffer.writeInt(p.getLocation().getBlockY());
//            wrappedBuffer.writeInt(p.getLocation().getBlockZ());
//            wrappedBuffer.writeBoolean(true);
//            wrappedBuffer.writeBoolean(true);
//
//            try {
//                Constructor constructor = website.vaperion.lunarclientapi.utils.ReflectionUtil.getClass("PacketPlayOutCustomPayload").getConstructor(String.class, ByteBuf.class);
//                Constructor serializerConstructor = website.vaperion.lunarclientapi.utils.ReflectionUtil.getClass("PacketDataSerializer").getConstructor(ByteBuf.class);
//                Object packet = constructor.newInstance("Lunar-Client", serializerConstructor.newInstance(Unpooled.wrappedBuffer(wrappedBuffer.buf().array())));
//                website.vaperion.lunarclientapi.utils.ReflectionUtil.sendPacket(p, packet);
//            } catch (Exception var11) {
//                var11.printStackTrace();
//                p.sendMessage("§cFailed to set a waypoint. Please contact a developer.");
//            }
//
//            p.sendMessage("kek");
//        }, 20);
//
//    }

                       @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if(e.getMessage().equalsIgnoreCase("/pc")) {
            e.setCancelled(true);
            p.sendMessage("§eYou are already talking in §cPublic §echat.");
            return;
        }
        if(e.getMessage().startsWith("/pc ")) {
            e.setCancelled(true);
            p.chat(e.getMessage().substring(4));

        }
    }
}
