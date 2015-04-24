package com.github.PluginSkillDead.main;

import com.github.PluginSkillDead.anniGame.AnniPlayer;
import com.github.PluginSkillDead.anniGame.AnniTeam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class ScoreboardAPI
{
  private static final Scoreboard scoreBoard = Bukkit.getScoreboardManager().getNewScoreboard();
  private static Objective gamePhase;

  static
  {
    for (AnniTeam team : AnniTeam.Teams)
    {
      Team t = scoreBoard.registerNewTeam(team.getName());
      t.setAllowFriendlyFire(false);
      t.setCanSeeFriendlyInvisibles(true);
      t.setPrefix(team.Color.toString());
    }
  }

  public static void registerListener(Plugin p)
  {
    Bukkit.getPluginManager().registerEvents(new BoardListeners(), p);
  }

  public static void showBoard(String name)
  {
    gamePhase = scoreBoard.getObjective("CAT");
    if (gamePhase == null)
      gamePhase = scoreBoard.registerNewObjective("CAT", "MEOW MEOW");
    gamePhase.setDisplayName(name);
    gamePhase.setDisplaySlot(DisplaySlot.SIDEBAR);
    for (AnniTeam team : AnniTeam.Teams)
    {
      Score score = gamePhase.getScore(team.Color + team.getName() + " Nexus");
      score.setScore(team.getHealth());
    }
  }

  public static void resetScoreboard()
  {
    for (AnniTeam team : AnniTeam.Teams)
    {
      Team t = scoreBoard.getTeam(team.getName());
      Set pls = t.getPlayers();
      ArrayList x = new ArrayList();
      for (OfflinePlayer p : pls)
        x.add(p);
      for (OfflinePlayer p : x)
        t.removePlayer(p);
      scoreBoard.resetScores(team.Color + team.getName() + " Nexus");
    }
  }

  public static void setScore(AnniTeam team, int score)
  {
    if (gamePhase != null)
      gamePhase.getScore(team.Color + team.getName() + " Nexus").setScore(score);
  }

  public static void removeTeam(AnniTeam team)
  {
    scoreBoard.resetScores(team.Color + team.getName() + " Nexus");
  }

  public static void setScoreboard(Player player)
  {
    player.setScoreboard(scoreBoard);
  }

  public static void addPlayer(AnniTeam team, Player p)
  {
    scoreBoard.getTeam(team.getName()).addPlayer(p);
  }

  public static void removePlayer(AnniTeam team, Player p)
  {
    scoreBoard.getTeam(team.getName()).removePlayer(p);
  }

  public static Integer[] teamCounts()
  {
    Integer[] x = new Integer[4];
    int i = 0;
    for (Team t : scoreBoard.getTeams())
    {
      x[i] = Integer.valueOf(t.getSize());
      i++;
    }
    Arrays.sort(x);
    return x;
  }

  public static Set<OfflinePlayer> getPlayers(AnniTeam t)
  {
    return scoreBoard.getTeam(t.getName()).getPlayers();
  }

  public static int getPlayerCount(AnniTeam team)
  {
    return scoreBoard.getTeam(team.getName()).getSize();
  }

  private static class BoardListeners
    implements Listener
  {
    public BoardListeners()
    {
      for (AnniPlayer p : AnniPlayer.getPlayers().values())
      {
        Player pl = p.getPlayer();
        if (pl != null)
          ScoreboardAPI.setScoreboard(pl);
      }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void playerCheck(PlayerJoinEvent event)
    {
      AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
      if (p != null)
        ScoreboardAPI.setScoreboard(event.getPlayer());
      else
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void teleportCheck(PlayerTeleportEvent event)
    {
      AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
      if (p != null)
        ScoreboardAPI.setScoreboard(event.getPlayer());
      else
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
  }
}