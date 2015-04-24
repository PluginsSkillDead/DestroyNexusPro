package com.github.PluginSkillDead.kits;

import com.github.PluginSkillDead.anniEvents.AnniEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public abstract class Kit
  implements Listener, Comparable<Kit>
{
  public static final Kit CivilianInstance = new CivilianKit();

  static {
    Plugin p = Bukkit.getPluginManager().getPlugin("Annihilation");

    Bukkit.getPluginManager().registerEvents(CivilianInstance, p);
    AnniEvent.registerListener(CivilianInstance);
    CivilianInstance.Initialize();
  }

  public abstract void Initialize();

  public abstract String getName();

  public abstract IconPackage getIconPackage();

  public abstract boolean canSelect(Player paramPlayer);

  public abstract void onPlayerSpawn(Player paramPlayer);

  public abstract void cleanup(Player paramPlayer);

  public int compareTo(Kit kit)
  {
    return getName().compareTo(kit.getName());
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (getName() == null ? 0 : getName().hashCode());
    return result;
  }

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Kit other = (Kit)obj;
    if (getName() == null)
    {
      if (other.getName() != null)
        return false;
    }
    else if (!getName().equals(other.getName()))
      return false;
    return true;
  }
}