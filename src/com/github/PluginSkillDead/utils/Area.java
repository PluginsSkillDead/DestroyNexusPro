package com.github.PluginSkillDead.utils;

import com.gmail.nuclearcat1337.anniGame.AnniPlayer;
import com.gmail.nuclearcat1337.kits.CustomItem;
import com.gmail.nuclearcat1337.kits.KitUtils;
import com.gmail.nuclearcat1337.main.ConfigUtils;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Area
{
  private static Map<String, Area> areas = new HashMap();
  public final Loc Corner1;
  public final Loc Corner2;
  public final String Name;
  private boolean allowPVP;

  public static void registerListeners(JavaPlugin p)
  {
    areas.clear();
    AreaListener l = new AreaListener(null);
    Bukkit.getPluginManager().registerEvents(l, p);
    p.getCommand("Area").setExecutor(l);
  }

  public static void loadAreas(ConfigurationSection areaSection)
  {
    if (areaSection != null)
    {
      for (String key : areaSection.getKeys(false))
      {
        ConfigurationSection area = areaSection.getConfigurationSection(key);
        String name = area.getString("Name");
        Location loc1 = ConfigUtils.getLocation(area.getConfigurationSection("Corner1Location"));
        Location loc2 = ConfigUtils.getLocation(area.getConfigurationSection("Corner2Location"));
        if ((name != null) && (loc1 != null) && (loc2 != null))
        {
          Area a = new Area(name, new Loc(loc1), new Loc(loc2));
          if (area.isBoolean("AllowPVP"))
            a.allowPVP = area.getBoolean("AllowPVP");
          areas.put(name.toLowerCase(), a);
        }
      }
    }
  }

  public static void saveAreas(ConfigurationSection areaSection)
  {
    int counter = 1;
    for (Area a : areas.values())
    {
      ConfigurationSection sec = areaSection.createSection(counter);
      sec.set("Name", a.Name);
      sec.set("AllowPVP", Boolean.valueOf(a.allowPVP));
      ConfigUtils.saveLocation(a.Corner1, sec.createSection("Corner1Location"));
      ConfigUtils.saveLocation(a.Corner2, sec.createSection("Corner2Location"));
      counter++;
    }
  }

  private static boolean fallsBetween(int one, int two, int num)
  {
    int max;
    int min;
    int max;
    if (one < two)
    {
      int min = one;
      max = two;
    }
    else
    {
      min = two;
      max = one;
    }

    return (num >= min) && (num <= max);
  }

  private static Area getArea(String name)
  {
    return (Area)areas.get(name.toLowerCase());
  }

  private static Area getArea(Loc loc)
  {
    for (Area a : areas.values())
    {
      if (a.Corner1.getWorldName().equals(loc.getWorldName()))
      {
        if ((fallsBetween(a.Corner1.getBlockX(), a.Corner2.getBlockX(), loc.getBlockX())) && 
          (fallsBetween(a.Corner1.getBlockY(), a.Corner2.getBlockY(), loc.getBlockY())) && 
          (fallsBetween(a.Corner1.getBlockZ(), a.Corner2.getBlockZ(), loc.getBlockZ())))
        {
          return a;
        }
      }
    }
    return null;
  }

  private Area(String name, Loc b1, Loc b2)
  {
    this.Name = name;
    this.Corner1 = b1;
    this.Corner2 = b2;

    this.allowPVP = true;
  }

  public boolean isPVPAllowed()
  {
    return this.allowPVP;
  }

  private static class AreaListener
    implements Listener, CommandExecutor
  {
    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void playerCheck(PlayerInteractEvent event)
    {
      if ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_BLOCK))
      {
        Player player = event.getPlayer();
        if (KitUtils.itemHasName(player.getItemInHand(), CustomItem.AREAWAND.getName()))
        {
          AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
          if (p != null)
          {
            event.setCancelled(true);
            if (event.getAction() == Action.LEFT_CLICK_BLOCK)
            {
              p.setData("A.Loc1", new Loc(event.getClickedBlock().getLocation()));
              player.sendMessage(ChatColor.LIGHT_PURPLE + "Corner " + ChatColor.GOLD + "1 " + ChatColor.LIGHT_PURPLE + "set.");
            }
            else
            {
              p.setData("A.Loc2", new Loc(event.getClickedBlock().getLocation()));
              player.sendMessage(ChatColor.LIGHT_PURPLE + "Corner " + ChatColor.GOLD + "2 " + ChatColor.LIGHT_PURPLE + "set.");
            }
          }
        }
      }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void checkBreaks(EntityDamageByEntityEvent e)
    {
      if ((e.getDamager().getType() == EntityType.PLAYER) && (e.getEntity().getType() == EntityType.PLAYER))
      {
        Area a = Area.getArea(new Loc(e.getDamager().getLocation()));
        if ((a != null) && (!a.allowPVP))
        {
          e.setCancelled(true);
          return;
        }
        a = Area.getArea(new Loc(e.getEntity().getLocation()));
        if ((a != null) && (!a.allowPVP))
        {
          e.setCancelled(true);
          return;
        }
      }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void checkBreaks(BlockBreakEvent e)
    {
      if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
      {
        Area a = Area.getArea(new Loc(e.getBlock().getLocation()));
        if (a != null)
        {
          e.setCancelled(true);
        }
      }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void checkBreaks(BlockPlaceEvent e)
    {
      if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
      {
        Area a = Area.getArea(new Loc(e.getBlock().getLocation()));
        if (a != null)
        {
          e.setCancelled(true);
        }
      }
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void checkBreaks(PlayerBucketEmptyEvent event)
    {
      if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
      {
        Area a = Area.getArea(new Loc(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation()));
        if (a != null)
        {
          event.setCancelled(true);
        }
      }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
      if ((sender instanceof Player))
      {
        Player player = (Player)sender;
        if (player.hasPermission("A.Area"))
        {
          AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
          if (p != null)
          {
            if (args.length > 0)
            {
              if ((args[0].equalsIgnoreCase("create")) && (args.length > 1))
              {
                Object obj = p.getData("A.Loc1");
                Object obj2 = p.getData("A.Loc2");
                if ((obj != null) && (obj2 != null) && ((obj instanceof Loc)) && ((obj2 instanceof Loc)))
                {
                  p.setData("A.Loc1", null);
                  p.setData("A.Loc2", null);
                  Area a = new Area(args[1], (Loc)obj, (Loc)obj2, null);
                  if (args.length > 2)
                  {
                    if (args[2].equalsIgnoreCase("allow"))
                      a.allowPVP = true;
                    else if (args[2].equalsIgnoreCase("disallow"))
                      a.allowPVP = false;
                  }
                  Area.areas.put(a.Name, a);
                  sender.sendMessage(ChatColor.LIGHT_PURPLE + "Area " + ChatColor.GOLD + a.Name + ChatColor.LIGHT_PURPLE + " added");
                } else {
                  sender.sendMessage(ChatColor.RED + "You don't have 2 corners set to create an area.");
                }
              } else if ((args[0].equalsIgnoreCase("delete")) && (args.length > 1))
              {
                Area a;
                Area a;
                if (args[1].equalsIgnoreCase("this"))
                  a = Area.getArea(new Loc(player.getLocation()));
                else
                  a = Area.getArea(args[1].toLowerCase());
                if (a != null)
                {
                  Area.areas.remove(a.Name.toLowerCase());
                  sender.sendMessage(ChatColor.LIGHT_PURPLE + "Area " + ChatColor.GOLD + a.Name + ChatColor.LIGHT_PURPLE + " removed");
                } else {
                  sender.sendMessage(ChatColor.RED + "Could not locate the area you wanted to delete.");
                }
              } else { sender.sendMessage(ChatColor.LIGHT_PURPLE + "/Area " + ChatColor.GOLD + "[create,delete] [name] [allow-PvP?]"); }
            }
            else sender.sendMessage(ChatColor.LIGHT_PURPLE + "/Area " + ChatColor.GOLD + "[create,delete] [name] [allow-PvP?]"); 
          }
        }
        else { sender.sendMessage(ChatColor.RED + "You do not have permission to use this command."); }
      } else {
        sender.sendMessage("You must be a player to select areas.");
      }return true;
    }
  }
}