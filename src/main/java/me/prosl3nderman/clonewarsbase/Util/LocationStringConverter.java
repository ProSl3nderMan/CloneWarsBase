package me.prosl3nderman.clonewarsbase.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationStringConverter {

    public static String getStringFromLocation(Location loc, Boolean yawPitch) { //world;x;y;z;yaw;pitch
        if (yawPitch)
            return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() +";" +loc.getPitch();
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ();
    }

    public static Location getLocationFromString(String s, Boolean yawPitch) {
        String[] part = s.split(";");
        if (yawPitch)
            return new Location(Bukkit.getServer().getWorld(part[0]), intS(part[1]), intS(part[2]), intS(part[3]),floatS(part[4]),floatS(part[5]));
        return new Location(Bukkit.getServer().getWorld(part[0]), intS(part[1]), intS(part[2]), intS(part[3]));
    }

    private static Double intS(String s) {
        return Double.parseDouble(s);
    }

    private static Float floatS(String s) {
        return Float.parseFloat(s);
    }
}
