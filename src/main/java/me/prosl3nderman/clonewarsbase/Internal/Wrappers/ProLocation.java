package me.prosl3nderman.clonewarsbase.Internal.Wrappers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class ProLocation {

    private final Vector coords;
    private final String worldName;

    public ProLocation(Location loc) {
        coords = loc.toVector();
        worldName = loc.getWorld().getName();
    }

    public Location getLocation() {
        return coords.toLocation(getWorld());
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }
}
