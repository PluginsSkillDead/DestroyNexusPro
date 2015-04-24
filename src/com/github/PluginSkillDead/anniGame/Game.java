package com.github.PluginSkillDead.anniGame;

import com.gmail.nuclearcat1337.anniEvents.AnniEvent;
import com.gmail.nuclearcat1337.anniEvents.GameEndEvent;
import com.gmail.nuclearcat1337.anniEvents.GameStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Game
{
  public static String GameWorld;
  public static int Phase;
  public static boolean NexusInvincible;
  public static int CurrentMultiplier = 1;
  public static Location LobbyLocation;
  public static int PhaseTime = 150;

  private static boolean GameRunning = false;

  public static World getLobbyWorld()
  {
    try
    {
      return LobbyLocation.getWorld();
    }
    catch (Exception e) {
    }
    return null;
  }

  public static boolean isGameRunning()
  {
    return GameRunning;
  }

  public static void startGame()
  {
    if (!isGameRunning())
    {
      GameRunning = true;
      AnniEvent.callEvent(new GameStartEvent());
    }
  }

  public static void stopGame(AnniTeam team)
  {
    if (isGameRunning())
    {
      GameRunning = false;
      AnniEvent.callEvent(new GameEndEvent(team));
    }
  }

  public static boolean WorldCheck(Player player)
  {
    String lobbyworld = LobbyLocation == null ? null : LobbyLocation.getWorld().getName();
    String gameworld = GameWorld;
    if ((lobbyworld != null) && (player.getWorld().getName().equalsIgnoreCase(lobbyworld)))
      return true;
    if (player.getWorld().getName().equalsIgnoreCase(gameworld))
      return true;
    return false;
  }

  public static void broadcastMessage(String message)
  {
    for (Player player : Bukkit.getOnlinePlayers())
    {
      if (WorldCheck(player))
        player.sendMessage(message);
    }
  }
}