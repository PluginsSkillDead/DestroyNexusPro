package com.github.PluginSkillDead.anniGame;

import com.gmail.nuclearcat1337.main.ScoreboardAPI;
import com.gmail.nuclearcat1337.utils.Loc;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Nexus
  implements Listener
{
  public final AnniTeam Team;
  private Loc Location;

  public Nexus(AnniTeam team)
  {
    this.Team = team;
    this.Location = null;
  }

  public void setLocation(Loc loc)
  {
    this.Location = loc;
  }

  public Loc getLocation()
  {
    return this.Location;
  }

  private void gameOverCheck()
  {
    int total = AnniTeam.Teams.size();
    int destroyed = 0;
    AnniTeam winner = null;
    for (AnniTeam team : AnniTeam.Teams)
    {
      if (team.isTeamDead())
        destroyed++;
      else winner = team;
    }

    if (destroyed == total - 1)
    {
      Game.stopGame(winner);
    }
  }

  @EventHandler(priority=EventPriority.HIGHEST)
  public void NexusCheck(BlockBreakEvent event)
  {
    if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
    {
      if ((this.Location != null) && (!this.Team.isTeamDead()))
      {
        Location loc = event.getBlock().getLocation();
        if (this.Location.isEqual(loc))
        {
          event.setCancelled(true);
          if (!Game.NexusInvincible)
          {
            AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
            if ((p != null) && (p.getTeam() != null) && (!p.getTeam().equals(this.Team)))
            {
              loc.getWorld().playSound(loc, Sound.ANVIL_LAND, 0.7F, (float)Math.random());
              this.Team.setHealth(this.Team.getHealth() - 1 * Game.CurrentMultiplier);
              if (this.Team.isTeamDead())
              {
                ScoreboardAPI.removeTeam(this.Team);
                for (Player player : Bukkit.getOnlinePlayers())
                {
                  player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 0.9F, 0.8F);
                }
                World w = loc.getWorld();
                w.getBlockAt(loc).setType(Material.BEDROCK);
                gameOverCheck();
              }
            }
          }
        }
      }
    }
  }
}