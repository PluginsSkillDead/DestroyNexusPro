package com.github.PluginSkillDead.utils;

import com.gmail.nuclearcat1337.anniGame.AnniPlayer;
import com.gmail.nuclearcat1337.anniGame.AnniTeam;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Furnace;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class SignHandler
{
  private static final Map<String, JoinSign> joinSignMap = new HashMap();
  private static final Map<String, ShopSign> brewingSigns = new HashMap();
  private static final Map<String, ShopSign> weaponSigns = new HashMap();
  private static final Map<String, ShopSign> enderFurnaces = new HashMap();

  public static void registerListeners(Plugin plugin)
  {
    joinSignMap.clear();
    brewingSigns.clear();
    weaponSigns.clear();
    enderFurnaces.clear();
    Bukkit.getPluginManager().registerEvents(new SignListeners(), plugin);
  }

  public static Map<String, JoinSign> getTeamSigns()
  {
    return Collections.unmodifiableMap(joinSignMap);
  }

  public static Map<String, ShopSign> getBrewingSigns()
  {
    return Collections.unmodifiableMap(brewingSigns);
  }

  public static Map<String, ShopSign> getWeaponSigns()
  {
    return Collections.unmodifiableMap(weaponSigns);
  }

  public static Map<String, ShopSign> getEnderFurnaces()
  {
    return Collections.unmodifiableMap(enderFurnaces);
  }

  private static void placeSignInWorld(Block block, String[] lore, BlockFace facingDirection, boolean post)
  {
    if ((block.getType() != Material.WALL_SIGN) || (block.getType() != Material.SIGN_POST)) {
      block.setType(post ? Material.SIGN_POST : Material.WALL_SIGN);
    }
    org.bukkit.block.Sign sign = (org.bukkit.block.Sign)block.getState();
    if (sign != null)
    {
      for (int x = 0; x < lore.length; x++)
        sign.setLine(x, lore[x]);
      org.bukkit.material.Sign matSign = new org.bukkit.material.Sign(block.getType());
      matSign.setFacingDirection(facingDirection);
      sign.setData(matSign);
      sign.update(true);
    }
  }

  public static void addBrewingSign(Block block, BlockFace face, boolean signPost)
  {
    String key = Loc.toMapKey(block.getLocation());
    if ((!brewingSigns.containsKey(key)) && (block != null) && (face != null))
    {
      ChatColor g = ChatColor.DARK_GRAY;
      String[] lore = { g + "[" + ChatColor.DARK_PURPLE + "Shop" + g + "]", ChatColor.BLACK + "Brewing" };
      placeSignInWorld(block, lore, face, signPost);
      brewingSigns.put(key, new ShopSign(face, signPost));
    }
  }

  public static void addWeaponSign(Block block, BlockFace face, boolean signPost)
  {
    String key = Loc.toMapKey(block.getLocation());
    if ((!weaponSigns.containsKey(key)) && (block != null) && (face != null))
    {
      ChatColor g = ChatColor.DARK_GRAY;
      String[] lore = { g + "[" + ChatColor.DARK_PURPLE + "Shop" + g + "]", ChatColor.BLACK + "Weapon" };
      placeSignInWorld(block, lore, face, signPost);
      weaponSigns.put(key, new ShopSign(face, signPost));
    }
  }

  public static void addTeamSign(AnniTeam team, Block block, BlockFace face, boolean signPost)
  {
    String key = Loc.toMapKey(block.getLocation());
    if ((!joinSignMap.containsKey(key)) && (block != null) && (face != null))
    {
      String[] lore = { ChatColor.DARK_PURPLE + "Right Click", ChatColor.DARK_PURPLE + "To Join:", team.Color + team.getName() + " Team" };
      placeSignInWorld(block, lore, face, signPost);
      joinSignMap.put(key, new JoinSign(team.getName(), face, signPost));
    }
  }

  public static void addEnderFurnace(Block block, BlockFace face)
  {
    String key = Loc.toMapKey(block.getLocation());
    if ((!enderFurnaces.containsKey(key)) && (block != null) && (face != null))
    {
      enderFurnaces.put(key, new ShopSign(face, false));
      try
      {
        if ((block.getType() != Material.FURNACE) && (block.getType() != Material.BURNING_FURNACE)) {
          block.setType(Material.BURNING_FURNACE);
        }
        Furnace f = new Furnace(Material.BURNING_FURNACE);
        f.setFacingDirection(face);
        BlockState s = block.getState();
        s.setData(f);
        s.update(true);
      }
      catch (Exception localException)
      {
      }
    }
  }

  public static boolean removeEnderFurnace(Location loc)
  {
    String key = Loc.toMapKey(loc);
    if (enderFurnaces.containsKey(key))
    {
      enderFurnaces.remove(key);
      loc.getWorld().getBlockAt(loc).setType(Material.AIR);
      return true;
    }
    return false;
  }

  public static boolean removeJoinSign(Location loc)
  {
    String key = Loc.toMapKey(loc);
    if (joinSignMap.containsKey(key))
    {
      joinSignMap.remove(key);
      loc.getWorld().getBlockAt(loc).setType(Material.AIR);
      return true;
    }
    return false;
  }

  public static boolean removeBrewingSign(Location loc)
  {
    String key = Loc.toMapKey(loc);
    if (brewingSigns.containsKey(key))
    {
      brewingSigns.remove(key);
      loc.getWorld().getBlockAt(loc).setType(Material.AIR);
      return true;
    }
    return false;
  }

  public static boolean removeWeaponSign(Location loc)
  {
    String key = Loc.toMapKey(loc);
    if (weaponSigns.containsKey(key))
    {
      weaponSigns.remove(key);
      loc.getWorld().getBlockAt(loc).setType(Material.AIR);
      return true;
    }
    return false;
  }

  public static class JoinSign
  {
    public final String TeamName;
    public final BlockFace Face;
    public final boolean signPost;

    public JoinSign(String Team, BlockFace face, boolean signPost)
    {
      this.TeamName = Team;
      this.Face = face;
      this.signPost = signPost;
    }
  }

  public static class ShopSign {
    public final BlockFace Face;
    public final boolean signPost;

    public ShopSign(BlockFace face, boolean signPost) {
      this.Face = face;
      this.signPost = signPost;
    }
  }

  private static class SignListeners
    implements Listener
  {
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void signClickCheck(PlayerInteractEvent event)
    {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
      {
        Block b = event.getClickedBlock();
        if (b != null)
        {
          if ((b.getType() == Material.WALL_SIGN) || (b.getType() == Material.SIGN_POST) || (b.getType() == Material.FURNACE) || (b.getType() == Material.BURNING_FURNACE))
          {
            Location loc = b.getLocation();
            Player p = event.getPlayer();
            String key = Loc.toMapKey(loc);
            if (SignHandler.joinSignMap.containsKey(key))
            {
              AnniTeam team = AnniTeam.getTeamByName(((SignHandler.JoinSign)SignHandler.joinSignMap.get(key)).TeamName);
              if (team != null)
              {
                p.performCommand("team " + team.getName());
              }

            }
            else if (SignHandler.brewingSigns.containsKey(key))
            {
              p.sendMessage("You clicked on a brewing shop sign.");
            }
            else if (SignHandler.weaponSigns.containsKey(key))
            {
              p.sendMessage("You clicked on a weapon shop sign.");
            }
            else if (SignHandler.enderFurnaces.containsKey(key))
            {
              event.setCancelled(true);
              if (!p.isSneaking())
              {
                AnniPlayer pl = AnniPlayer.getPlayer(p.getUniqueId());
                if (pl != null)
                {
                  pl.openFurnace();
                }
              }
            }
          }
        }
      }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void signBreakCheck(BlockBreakEvent event)
    {
      if (event.getBlock() != null)
      {
        if ((event.getBlock().getType() == Material.WALL_SIGN) || (event.getBlock().getType() == Material.SIGN_POST) || (event.getBlock().getType() == Material.FURNACE) || (event.getBlock().getType() == Material.BURNING_FURNACE))
        {
          String key = Loc.toMapKey(event.getBlock().getLocation());
          if ((SignHandler.joinSignMap.containsKey(key)) || (SignHandler.brewingSigns.containsKey(key)) || (SignHandler.weaponSigns.containsKey(key)) || (SignHandler.enderFurnaces.containsKey(key)))
            event.setCancelled(true);
        }
      }
    }
  }
}
