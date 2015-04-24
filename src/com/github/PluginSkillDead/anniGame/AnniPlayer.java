package com.github.PluginSkillDead.anniGame;

import com.gmail.nuclearcat1337.anniEvents.AnniEvent;
import com.gmail.nuclearcat1337.anniEvents.AnnihilationEvent;
import com.gmail.nuclearcat1337.anniEvents.GameEndEvent;
import com.gmail.nuclearcat1337.anniEvents.GameStartEvent;
import com.gmail.nuclearcat1337.enderFurnace.AbstractFurnace;
import com.gmail.nuclearcat1337.enderFurnace.EnderFurnace;
import com.gmail.nuclearcat1337.kits.CustomItem;
import com.gmail.nuclearcat1337.kits.Kit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public final class AnniPlayer
{
  private static final Map<UUID, AnniPlayer> players = new HashMap();
  public final UUID ID;
  public final String Name;
  private Map<Object, Object> data;
  private AnniTeam team;
  private Kit kit;
  private AbstractFurnace enderfurnace;

  public static AnniPlayer getPlayer(UUID id)
  {
    return (AnniPlayer)players.get(id);
  }

  public static Map<UUID, AnniPlayer> getPlayers()
  {
    return Collections.unmodifiableMap(players);
  }

  public static AnniPlayer forcePlayerLoad(Player player)
  {
    AnniPlayer p = new AnniPlayer(player.getUniqueId(), player.getName());
    players.put(player.getUniqueId(), p);
    return p;
  }

  public static void RegisterListener(Plugin p)
  {
    players.clear();
    PlayerLoader l = new PlayerLoader();
    Bukkit.getPluginManager().registerEvents(l, p);
    AnniEvent.registerListener(l);
  }

  private AnniPlayer(UUID ID, String Name)
  {
    this.ID = ID;
    this.Name = Name;
    this.team = null;
    this.kit = Kit.CivilianInstance;
    this.data = null;
    this.enderfurnace = null;
  }

  private void setFurnace(AbstractFurnace furnace)
  {
    this.enderfurnace = furnace;
  }

  public AbstractFurnace getFurnace()
  {
    return this.enderfurnace;
  }

  public void openFurnace()
  {
    if (this.enderfurnace != null)
      this.enderfurnace.open();
  }

  public Object getData(Object key)
  {
    if (this.data == null)
      return null;
    return this.data.get(key);
  }

  public void setData(Object key, Object value)
  {
    if (this.data == null)
      this.data = new HashMap();
    this.data.put(key, value);
  }

  public void setTeam(AnniTeam t)
  {
    this.team = t;
    String playerName = this.Name;
    String name = playerName.length() > 14 ? playerName.substring(0, 14) : playerName;
    if (t == null)
    {
      Player p = getPlayer();
      if (p != null)
        p.setPlayerListName(ChatColor.WHITE + name);
      return;
    }
    try
    {
      Player p = getPlayer();
      if (p != null)
      {
        p.setPlayerListName(t.Color + name);
      }
    }
    catch (IllegalArgumentException e1)
    {
      Random rnd = new Random();
      name = (name.length() > 11 ? name.substring(0, 11) : name) + rnd.nextInt(9);
      try
      {
        Player p = getPlayer();
        if (p != null)
        {
          p.setPlayerListName(t.Color + name);
        }
      }
      catch (IllegalArgumentException e2)
      {
        Bukkit.getLogger().warning("[Annihilation] setPlayerListName error: " + e2.getMessage());
      }
    }
  }

  public AnniTeam getTeam()
  {
    return this.team;
  }

  public void setKit(Kit kit)
  {
    if (kit != null)
      this.kit = kit;
  }

  public void sendMessage(String message)
  {
    Player p = Bukkit.getPlayer(this.ID);
    if (p != null)
      p.sendMessage(message);
  }

  public Kit getKit()
  {
    return this.kit;
  }

  public Player getPlayer()
  {
    return Bukkit.getPlayer(this.ID);
  }

  public boolean equals(Object obj)
  {
    if ((obj instanceof AnniPlayer))
    {
      AnniPlayer p = (AnniPlayer)obj;
      return this.ID.equals(p.ID);
    }
    return false;
  }

  private static class FurnaceTick extends BukkitRunnable
  {
    public void run()
    {
      if (Game.isGameRunning())
      {
        for (AnniPlayer player : AnniPlayer.getPlayers().values())
        {
          AbstractFurnace f = player.getFurnace();
          if (f != null)
            f.tick();
        }
      }
      else cancel();
    }
  }

  private static class PlayerLoader
    implements Listener
  {
    public PlayerLoader()
    {
      for (Player p : Bukkit.getOnlinePlayers())
      {
        if (Game.WorldCheck(p))
          loadPlayer(p);
      }
    }

    @AnnihilationEvent
    public void onGameStart(GameStartEvent event)
    {
      for (AnniPlayer player : AnniPlayer.getPlayers().values())
      {
        Player pl = player.getPlayer();
        player.setFurnace(new EnderFurnace(pl, 0));
        new AnniPlayer.FurnaceTick(null).runTaskTimer(Bukkit.getPluginManager().getPlugin("Annihilation"), 3L, 3L);
      }
    }

    @AnnihilationEvent
    public void onGameEnd(GameEndEvent event)
    {
      if (event.getWinningTeam() == null)
      {
        for (AnniPlayer player : AnniPlayer.getPlayers().values())
        {
          player.setTeam(null);
          player.setKit(Kit.CivilianInstance);
          player.setFurnace(null);
          Player pl = player.getPlayer();
          if (pl != null)
          {
            pl.getInventory().clear();
            pl.getInventory().setArmorContents(null);
            if (Game.LobbyLocation != null)
            {
              pl.teleport(Game.LobbyLocation);
              pl.getInventory().addItem(new ItemStack[] { CustomItem.KITMAP.toItemStack(1) });
            }
          }
        }
      }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void playerCheck(PlayerTeleportEvent event)
    {
      String lobby = Game.getLobbyWorld() == null ? null : Game.getLobbyWorld().getName();
      String world = Game.GameWorld;
      Player p = event.getPlayer();
      if ((!exists(p)) && (((lobby != null) && (p.getWorld().getName().equalsIgnoreCase(lobby))) || (world.equalsIgnoreCase(p.getWorld().getName()))))
      {
        loadPlayer(p);
      }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void playerCheck(PlayerJoinEvent event)
    {
      Player p = event.getPlayer();
      if (Game.WorldCheck(p))
      {
        if (!exists(p))
          loadPlayer(p);
      }
    }

    private boolean exists(Player p)
    {
      return AnniPlayer.players.containsKey(p.getUniqueId());
    }

    private void loadPlayer(Player p)
    {
      AnniPlayer.players.put(p.getUniqueId(), new AnniPlayer(p.getUniqueId(), p.getName(), null));
    }
  }
}