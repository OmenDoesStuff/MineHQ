package net.frozenorb.qlib.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class CustomParser {

    public static String parseLocation(Location loc) {
        if(loc.getWorld() == null) {
            return null;
        }
        return loc.getWorld().getName() + "@" + loc.getX() + "@" + loc.getY() + "@" + loc.getZ() + "@" + loc.getYaw() + "@" + loc.getPitch();
    }

    public static Location unparseLocation(String parse) {
        Bukkit.broadcastMessage("Received parse: " + parse);
        String[] args = parse.split("@");
        if(Bukkit.getWorld(args[0]) == null) {
            return null;
        }
        return new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
    }

}
