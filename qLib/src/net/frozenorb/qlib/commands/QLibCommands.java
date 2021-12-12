package net.frozenorb.qlib.commands;

import net.frozenorb.pclientapi.PClientAPI;
import net.frozenorb.pclientapi.utils.EmoteType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.CommandArgs;
import net.frozenorb.qlib.utils.ByteBufWrapper;
import net.frozenorb.qlib.utils.LCTypes;
import net.frozenorb.qlib.utils.ReflectionUtil;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.buffer.Unpooled;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Collectors;

public class QLibCommands {

    @Command(name = "pcdebug", permission = "protocol.debug")
    public void onCommand(CommandArgs cmd) {
        Player p = cmd.getPlayer();
        String[] args = cmd.getArgs();
        if(args.length < 1) {
            p.sendMessage("§cUsage: /pcdebug <staffmod:emote:broadcast:timer:title:waypoint> <args> <args>");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "staffmod": {
                LCTypes.StaffModes staffModes = null;
                try {
                    staffModes = LCTypes.StaffModes.valueOf(args[1].toUpperCase());
                } catch(Exception e) {
                    p.sendMessage("§cInvalid staff mod, available types: NoClip, XRay, BunnyHop, Nametags");
                    return;
                }
                boolean b = Boolean.parseBoolean(args[2]);
                sendStaffModePacket(p, staffModes, b);
                p.sendMessage("§aYou have " + (b ? "enabled" : "disabled") + " " + staffModes.getPacketName());
                return;
            }
            case "title": {
                if(args.length != 3) {
                    p.sendMessage("§cUsage: /pcdebug title <player> <message>");
                    return;
                }
                Player t = Bukkit.getPlayer(args[1]);
                PClientAPI.getInstance().sendTitle(t, "yes", ChatColor.translateAlternateColorCodes('&', args[2]), 300L, 100L, 40L);
                return;
            }
            case "emote": {
                if(args.length != 2) {
                    p.sendMessage("§cUsage: /pcdebug emote <emote>");
                    return;
                }
                EmoteType emote;
                try {
                    emote = EmoteType.valueOf(args[1]);
                }catch(Exception e) {
                    p.sendMessage("§cUnknown emote the list are: " + StringUtils.join(EmoteType.values(), ", "));
                    return;
                }
                PClientAPI.getInstance().sendEmote(p, (List)p.getNearbyEntities(200.0, 200.0, 200.0).stream().filter(entity -> entity instanceof Player).map(entity -> entity).collect(Collectors.toList()), emote);
                return;
            }
            case "waypoint": {
                switch(args[1].toLowerCase()){
                    /*/
                    Waypoint add *forced + vis*
                    ServerRuke: SERVER_HANDLES_WAYPOINTS 0 0.0 true ""
                    ServerRule: MINIMAP_STATUS 0 0.0 false NEUTRAL
                    UpdateWorld
                    Waypoint add *forced +vis*
                     */
                    case "create": {
                        PClientAPI.getInstance().createWaypoint(p, args[2], p.getLocation(), -1, true, true);
                        p.sendMessage("kek");
                        return;
                    }

                }
                return;
            }
        }
    }

    public void sendStaffModePacket(Player p, LCTypes.StaffModes staffModes, boolean b) {
        ByteBufWrapper wrappedBuffer = new ByteBufWrapper(Unpooled.buffer(7));
        wrappedBuffer.writeVarInt(12);
        wrappedBuffer.writeString(staffModes.getPacketName());
        wrappedBuffer.buf().writeBoolean(b);

        try {

            Constructor constructor = ReflectionUtil.getClass("PacketPlayOutCustomPayload").getConstructor(String.class, ByteBuf.class);
            Constructor serializerConstructor = ReflectionUtil.getClass("PacketDataSerializer").getConstructor(ByteBuf.class);
            Object packet = constructor.newInstance("Lunar-Client", serializerConstructor.newInstance(Unpooled.wrappedBuffer(wrappedBuffer.buf().array())));
            ReflectionUtil.sendPacket(p, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command(name = "border")
    public void borderCommand(CommandArgs cmd) {
        cmd.getSender().sendMessage("§cNo permission.");
    }

    @Command(name = "chat", inGameOnly = true)
    public void chatCommand(CommandArgs cmd) {
        String[] args = cmd.getArgs();
        Player p = cmd.getPlayer();
        p.sendMessage("§7§m-----------------------------------");
        p.sendMessage("§e§lChat §f§lModes");
        p.sendMessage("§7§m-----------------------------------");
        p.sendMessage("§f§lCurrent Chat Mode: §a§lPublic");
        p.sendMessage(" §aPublic: §7/pc [message] §8(or prefix your message with §e§l!§8)");
        p.sendMessage("§7§m-----------------------------------");
    }



}
