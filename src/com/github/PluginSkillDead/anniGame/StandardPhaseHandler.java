package com.github.PluginSkillDead.anniGame;

import com.github.PluginSkillDead.main.ConfigUtils;
import com.github.PluginSkillDead.phaseAPI.PhaseAPI;
import com.github.PluginSkillDead.utils.Loc;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

public class StandardPhaseHandler
  implements PhaseHandler
{
  private static final List<Loc> diamonds = new ArrayList();

  public static void loadDiamonds(ConfigurationSection diamondSec)
  {
    if (diamondSec != null)
    {
      for (String key : diamondSec.getKeys(false))
      {
        Location loc = ConfigUtils.getLocation(diamondSec.getConfigurationSection(key));
        if (loc != null)
          diamonds.add(new Loc(loc));
      }
    }
  }

  public static void saveDiamonds(ConfigurationSection diamondSec)
  {
    int counter = 1;
    for (Loc loc : diamonds)
    {
      ConfigUtils.saveLocation(loc, diamondSec.createSection(counter));
      counter++;
    }
  }

  public static void addDiamond(Loc loc)
  {
    diamonds.add(loc);
  }

  public static boolean removeDiamond(Loc loc)
  {
    for (int x = 0; x < diamonds.size(); x++)
    {
      Loc l = (Loc)diamonds.get(x);
      if (l.isEqual(loc))
      {
        diamonds.remove(x);
        return true;
      }
    }
    return false;
  }

  public void onPhaseChange(int oldPhase)
  {
    Game.Phase = oldPhase + 1;

    if (oldPhase < 4)
      PhaseAPI.beginPhase(oldPhase + 1);
    else {
      PhaseAPI.permanentPhase(oldPhase + 1);
    }
    if (Game.Phase == 2)
      Game.NexusInvincible = false;
    else if (Game.Phase == 3)
    {
      for (Loc loc : diamonds)
      {
        Location l = loc.toLocation();
        l.getWorld().getBlockAt(l).setType(Material.DIAMOND_ORE);
      }
    }
    else if (Game.Phase == 5) {
      Game.CurrentMultiplier = 2;
    }
    for (AnniPlayer p : AnniPlayer.getPlayers().values())
    {
      p.sendMessage(ChatColor.DARK_PURPLE + "Phase " + ChatColor.AQUA + Game.Phase + ChatColor.DARK_PURPLE + " has begun!");
    }
  }
}