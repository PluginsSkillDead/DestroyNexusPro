package com.github.PluginSkillDead.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Loc
{
  private double x;
  private double y;
  private double z;
  private double pitch;
  private double yaw;
  private String world;

  public Loc(Location loc)
  {
    this.x = loc.getX();
    this.y = loc.getY();
    this.z = loc.getZ();
    this.pitch = loc.getPitch();
    this.yaw = loc.getY();
    this.world = loc.getWorld().getName();
  }

  public Loc(double x, double y, double z, double pitch, double yaw, String world) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.pitch = pitch;
    this.yaw = yaw;
    this.world = world;
  }

  public Location toLocation()
  {
    return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, (float)this.yaw, (float)this.pitch);
  }

  public int getBlockX()
  {
    return (int)Math.floor(this.x);
  }

  public int getBlockY()
  {
    return (int)Math.floor(this.y);
  }

  public int getBlockZ()
  {
    return (int)Math.floor(this.z);
  }

  public String getWorldName()
  {
    return this.world;
  }

  public boolean isEqual(Loc loc)
  {
    return (loc.getBlockX() == getBlockX()) && (loc.getBlockY() == getBlockY()) && (loc.getBlockZ() == getBlockZ()) && (loc.world.equalsIgnoreCase(this.world));
  }

  public boolean isEqual(Location loc)
  {
    return (loc.getX() == this.x) && (loc.getY() == this.y) && (loc.getZ() == this.z) && (loc.getWorld().getName().equalsIgnoreCase(this.world));
  }

  public static Location getMiddle(Location loc)
  {
    Location k = loc.clone();
    k.setX(k.getBlockX() + 0.5D);

    k.setZ(k.getBlockZ() + 0.5D);
    return k;
  }

  public static String toMapKey(Location loc)
  {
    return loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " " + loc.getWorld().getName();
  }

  public static String toMapKey(Loc loc)
  {
    return toMapKey(loc.toLocation());
  }

  public static Loc fromMapKey(String key)
  {
    try
    {
      String[] args = key.split(" ");

      int x = Integer.parseInt(args[0]);
      int y = Integer.parseInt(args[1]);
      int z = Integer.parseInt(args[2]);
      return new Loc(x, y, z, 0.0D, 0.0D, args[3]);
    }
    catch (Exception e) {
    }
    return null;
  }
}