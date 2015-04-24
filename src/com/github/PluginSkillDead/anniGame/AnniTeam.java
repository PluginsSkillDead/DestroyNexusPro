package com.github.PluginSkillDead.anniGame;

import com.gmail.nuclearcat1337.main.ScoreboardAPI;
import com.gmail.nuclearcat1337.utils.Loc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;

public class AnniTeam
{
  public static final AnniTeam Red = new AnniTeam(ChatColor.RED);
  public static final AnniTeam Blue = new AnniTeam(ChatColor.BLUE);
  public static final AnniTeam Green = new AnniTeam(ChatColor.GREEN);
  public static final AnniTeam Yellow = new AnniTeam(ChatColor.YELLOW);

  public static final List<AnniTeam> Teams = Collections.unmodifiableList(x);
  public final ChatColor Color;
  public final Nexus Nexus;
  private final String name;
  private int Health = 75;
  private LinkedList<Loc> spawns;
  private Random rand;
  private Loc spectatorLocation;

  static
  {
    List x = new ArrayList();
    x.add(Red);
    x.add(Blue);
    x.add(Green);
    x.add(Yellow);
  }

  public static AnniTeam getTeamByName(String name)
  {
    name = name.toLowerCase();
    if (name.equals("red"))
      return Red;
    if (name.equals("blue"))
      return Blue;
    if (name.equals("green"))
      return Green;
    if (name.equals("yellow")) {
      return Yellow;
    }
    return null;
  }

  public static AnniTeam getTeamByColor(ChatColor color)
  {
    if (color.equals(ChatColor.RED))
      return Red;
    if (color.equals(ChatColor.BLUE))
      return Blue;
    if (color.equals(ChatColor.GREEN))
      return Green;
    if (color.equals(ChatColor.YELLOW)) {
      return Yellow;
    }
    return null;
  }

  private AnniTeam(ChatColor color)
  {
    this.rand = new Random(System.currentTimeMillis());
    this.Color = color;
    this.spawns = new LinkedList();
    this.Nexus = new Nexus(this);
    Bukkit.getPluginManager().registerEvents(this.Nexus, Bukkit.getPluginManager().getPlugin("Annihilation"));
    this.name = (this.Color.name().substring(0, 1) + this.Color.name().substring(1).toLowerCase());

    this.Health = 25;
  }

  public Location getSpectatorLocation()
  {
    if (this.spectatorLocation != null)
      return this.spectatorLocation.toLocation();
    return null;
  }

  public void setSpectatorLocation(Loc loc)
  {
    this.spectatorLocation = loc;
  }

  public void setSpectatorLocation(Location loc)
  {
    setSpectatorLocation(new Loc(loc));
  }

  public int addSpawn(Location loc)
  {
    int val = this.spawns.size();
    this.spawns.addLast(new Loc(loc));
    return val + 1;
  }

  public boolean removeSpawn(int index)
  {
    if (this.spawns.size() >= index + 1)
    {
      this.spawns.remove(index);
      return true;
    }
    return false;
  }

  public int getHealth()
  {
    return this.Health;
  }

  public void setHealth(int Health)
  {
    if (Health < 0) {
      Health = 0;
    }
    if (this.Health > 0)
    {
      this.Health = Health;
      ScoreboardAPI.setScore(this, this.Health);
    }
  }

  public boolean isTeamDead()
  {
    return this.Health <= 0;
  }

  public Location getRandomSpawn()
  {
    if (!this.spawns.isEmpty()) {
      return ((Loc)this.spawns.get(this.rand.nextInt(this.spawns.size()))).toLocation();
    }

    Bukkit.getLogger().warning("NO SPAWNS SET FOR TEAM " + getName().toUpperCase() + ". SENDING TO LOBBY IS POSSIBLE.");
    if (Game.LobbyLocation != null)
      return Game.LobbyLocation;
    return null;
  }

  public List<Loc> getSpawnList()
  {
    return Collections.unmodifiableList(this.spawns);
  }

  public String getName()
  {
    return this.name;
  }

  public boolean equals(Object obj)
  {
    if ((obj instanceof AnniTeam))
      return ((AnniTeam)obj).Color == this.Color;
    return false;
  }

  public String toString()
  {
    return getName();
  }
}