package com.github.PluginSkillDead.main;

import com.github.PluginSkillDead.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigUtils
{
  public static void saveLocation(Location location, ConfigurationSection section)
  {
    if ((section != null) && (location != null))
    {
      section.set("X", Integer.valueOf(location.getBlockX()));
      section.set("Y", Integer.valueOf(location.getBlockY()));
      section.set("Z", Integer.valueOf(location.getBlockZ()));
      section.set("World", location.getWorld().getName());
    }
  }

  public static void saveLocation(Loc location, ConfigurationSection section)
  {
    saveLocation(location.toLocation(), section);
  }

  public static void savePreciseLocation(Location location, ConfigurationSection section)
  {
    if ((section != null) && (location != null))
    {
      section.set("X", Double.valueOf(location.getX()));
      section.set("Y", Double.valueOf(location.getY()));
      section.set("Z", Double.valueOf(location.getZ()));
      section.set("Pitch", Float.valueOf(location.getPitch()));
      section.set("Yaw", Float.valueOf(location.getYaw()));
      section.set("World", location.getWorld().getName());
    }
  }

  public static void savePreciseLocation(Loc location, ConfigurationSection section)
  {
    saveLocation(location.toLocation(), section);
  }

  public static Location getPreciseLocation(ConfigurationSection section)
  {
    if (section == null) {
      return null;
    }

    double x = section.getDouble("X");
    double y = section.getDouble("Y");
    double z = section.getDouble("Z");
    double pitch = section.getDouble("Pitch");
    double yaw = section.getDouble("Yaw");
    String world = section.getString("World");
    if (world == null)
      return null;
    return new Location(Bukkit.getWorld(world), x, y, z, (float)yaw, (float)pitch);
  }

  public static Location getLocation(ConfigurationSection section)
  {
    if (section == null) {
      return null;
    }

    int x = section.getInt("X");
    int y = section.getInt("Y");
    int z = section.getInt("Z");
    String world = section.getString("World");
    if (world == null)
      return null;
    return new Location(Bukkit.getWorld(world), x, y, z);
  }
}