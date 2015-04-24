package com.github.PluginSkillDead.anniGame;

import com.github.PluginSkillDead.kits.CustomItem;
import com.github.PluginSkillDead.kits.Kit;
import com.github.PluginSkillDead.utils.Loc;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public class GameListeners
  implements Listener
{
  public GameListeners(Plugin p)
  {
    Bukkit.getPluginManager().registerEvents(this, p);
  }

  @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
  public void deathHandler(PlayerDeathEvent event)
  {
    Player player = event.getEntity();
    AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
    if (p != null)
      p.getKit().cleanup(player);
  }

  @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
  public void AnniPlayersInit(PlayerCommandPreprocessEvent event)
  {
    String[] args = event.getMessage().split(" ");
    if (args[0].equalsIgnoreCase("/tp"))
    {
      Player player = event.getPlayer();
      if (player.hasPermission("A.anni"))
      {
        if (args.length > 1)
        {
          AnniTeam team = AnniTeam.getTeamByName(args[1]);
          if (team != null)
          {
            Location loc = team.getSpectatorLocation();
            if (loc != null)
            {
              event.setCancelled(true);
              player.teleport(loc);
            }
          }
          else if (args[1].equalsIgnoreCase("lobby"))
          {
            Location lobby = Game.LobbyLocation;
            if (lobby != null)
            {
              event.setCancelled(true);
              player.teleport(lobby);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
  public void AnniPlayersInit(AsyncPlayerChatEvent event)
  {
    AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
    if (p != null)
    {
      new SendChatMessage(p, event.getMessage()).runTask(Bukkit.getPluginManager().getPlugin("Annihilation"));
      event.setCancelled(true);
    }
  }

  @EventHandler(priority=EventPriority.LOWEST)
  public void DeathListener(PlayerDeathEvent event)
  {
    String message = "";
    Player player = event.getEntity();
    Player killer = player.getKiller();

    if (Game.isGameRunning())
    {
      AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
      if (p != null)
      {
        if (killer != null)
        {
          AnniPlayer k = AnniPlayer.getPlayer(killer.getUniqueId());
          if (k != null)
          {
            if ((p.getTeam() != null) && (k.getTeam() != null))
            {
              message = p.getTeam().Color + player.getName() + "(" + p.getKit().getName() + ")" + ChatColor.GRAY + " was killed by " + k.getTeam().Color + killer.getName() + "(" + k.getKit().getName() + ")";
              if (k.getTeam().isTeamDead())
                message = message + ChatColor.GRAY + " in remembrance of his team.";
              else if (k.getTeam().Nexus.getLocation().toLocation().distanceSquared(killer.getLocation()) <= 225.0D)
                message = message + ChatColor.GRAY + " in defense of his nexus.";
            }
          }
        }
        event.setDroppedExp(0);
        event.setDeathMessage(message);
      }
    }
  }

  @EventHandler(priority=EventPriority.HIGHEST)
  public void autoRespawn(PlayerDeathEvent event)
  {
    final Player player = event.getEntity();
    AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
    if (p != null)
    {
      new BukkitRunnable()
      {
        public void run()
        {
          try {
            Object nmsPlayer = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
            Object packet = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".PacketPlayInClientCommand").newInstance();
            Class enumClass = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EnumClientCommand");

            for (Object ob : enumClass.getEnumConstants())
            {
              if (ob.toString().equals("PERFORM_RESPAWN"))
              {
                packet = packet.getClass().getConstructor(new Class[] { enumClass }).newInstance(new Object[] { ob });
              }
            }

            Object con = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
            con.getClass().getMethod("a", new Class[] { packet.getClass() }).invoke(con, new Object[] { packet });
          }
          catch (Throwable t)
          {
            t.printStackTrace();
          }
        }
      }
      .runTaskLater(Bukkit.getPluginManager().getPlugin("Annihilation"), 2L);
    }
  }

  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
  public void teleportToLobbyThing(PlayerJoinEvent event)
  {
    if ((!Game.isGameRunning()) && (Game.LobbyLocation != null))
    {
      final Player pl = event.getPlayer();
      AnniPlayer p = AnniPlayer.getPlayer(pl.getUniqueId());
      if ((p != null) && (Game.GameWorld != null))
      {
        if (pl.getLocation().getWorld().getName().equalsIgnoreCase(Game.GameWorld))
        {
          pl.getInventory().clear();
          pl.getInventory().setArmorContents(null);
        }
      }
      new BukkitRunnable()
      {
        public void run()
        {
          pl.teleport(Game.LobbyLocation);
          pl.getInventory().addItem(new ItemStack[] { CustomItem.KITMAP.toItemStack(1) });
          pl.setGameMode(GameMode.ADVENTURE);
        }
      }
      .runTaskLater(Bukkit.getPluginManager().getPlugin("Annihilation"), 20L);
    }
  }

  @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
  public void respawnHandler(PlayerRespawnEvent event)
  {
    Player player = event.getPlayer();
    AnniPlayer p = AnniPlayer.getPlayer(player.getUniqueId());
    if (p != null)
    {
      if (Game.isGameRunning())
      {
        if ((p.getTeam() != null) && (!p.getTeam().isTeamDead()))
        {
          event.setRespawnLocation(p.getTeam().getRandomSpawn());
          p.getKit().onPlayerSpawn(player);
          return;
        }
      }
      if (Game.LobbyLocation != null)
        event.setRespawnLocation(Game.LobbyLocation);
    }
  }
}
