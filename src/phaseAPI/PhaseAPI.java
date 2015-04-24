package phaseAPI;


import com.gmail.nuclearcat1337.anniGame.AnniPlayer;
import com.gmail.nuclearcat1337.anniGame.Game;
import com.gmail.nuclearcat1337.anniGame.PhaseHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class PhaseAPI
  implements Listener
{
  private static HashMap<UUID, FakeDragon> players = new HashMap();
  private static Integer timer;
  private static PhaseHandler handler;
  private static String message;
  private static float currentHealth;
  private static boolean permanentPhase;
  private static Plugin plugin;
  
  public PhaseAPI(Plugin plugin)
  {
    plugin = plugin;
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }
  
  public static void Disable()
  {
    for (Player player : ) {
      quit(player);
    }
    players.clear();
    
    cancelTimer();
  }
  
  public static void Reset()
  {
    
    for (Player player : Bukkit.getOnlinePlayers()) {
      quit(player);
    }
    players.clear();
  }
  
  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
  public void PlayerLoggout(PlayerQuitEvent event)
  {
    AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
    if (p != null) {
      quit(event.getPlayer());
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
  public void onPlayerKick(PlayerKickEvent event)
  {
    AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
    if (p != null) {
      quit(event.getPlayer());
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
  public void onPlayerTeleport(PlayerTeleportEvent event)
  {
    AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
    if (p != null) {
      handleTeleport(event.getPlayer(), event.getTo().clone());
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
  public void onPlayerTeleport(PlayerRespawnEvent event)
  {
    AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
    if (p != null) {
      handleTeleport(event.getPlayer(), event.getRespawnLocation().clone());
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    AnniPlayer p = AnniPlayer.getPlayer(event.getPlayer().getUniqueId());
    if (p != null) {
      if ((!hasBar(event.getPlayer())) && (permanentPhase)) {
        sendPermDragon(event.getPlayer());
      }
    }
  }
  
  private static void handleTeleport(Player player, final Location loc)
  {
    if (!hasBar(player)) {
      return;
    }
    Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
    {
      public void run()
      {
        if (!PhaseAPI.hasBar(PhaseAPI.this)) {
          return;
        }
        FakeDragon oldDragon = PhaseAPI.getDragon(PhaseAPI.this, "");
        
        float health = oldDragon.health;
        String message = oldDragon.name;
        
        Util.sendPacket(PhaseAPI.this, PhaseAPI.getDragon(PhaseAPI.this, "")
          .getDestroyPacket());
        
        PhaseAPI.players.remove(PhaseAPI.this.getUniqueId());
        
        FakeDragon dragon = PhaseAPI.addDragon(PhaseAPI.this, loc, message);
        dragon.health = health;
        
        PhaseAPI.sendDragon(dragon, PhaseAPI.this);
      }
    }, 2L);
  }
  
  private static void quit(Player player)
  {
    removeBar(player);
  }
  
  public static void permanentPhase(int phase)
  {
    permanentPhase = true;
    message = "Phase " + phase;
    cancelTimer();
    for (AnniPlayer player : AnniPlayer.getPlayers().values())
    {
      Player p = player.getPlayer();
      if (p != null) {
        sendPermDragon(p);
      }
    }
  }
  
  public static void setPhaseHandler(PhaseHandler handler)
  {
    handler = handler;
  }
  
  private static void sendPermDragon(Player player)
  {
    FakeDragon dragon = getDragon(player, message);
    
    dragon.name = cleanMessage(message);
    dragon.health = dragon.getMaxHealth();
    
    sendDragon(dragon, player);
  }
  
  public static void beginPhase(final int phase)
  {
    message = "Phase " + phase;
    permanentPhase = false;
    
    Validate.isTrue(Game.PhaseTime > 0, "Seconds must be above 1 but was: ", 
      Game.PhaseTime);
    
    players.clear();
    
    cancelTimer();
    for (AnniPlayer player : AnniPlayer.getPlayers().values())
    {
      Player p = player.getPlayer();
      if (p != null)
      {
        FakeDragon dragon = getDragon(p, message);
        
        dragon.name = cleanMessage(message);
        dragon.health = dragon.getMaxHealth();
        
        sendDragon(dragon, p);
      }
    }
    currentHealth = 200.0F;
    
    float dragonHealthMinus = 200.0F / Game.PhaseTime;
    
    timer = 
    

































      Integer.valueOf(Bukkit.getScheduler().runTaskTimer(plugin, new Runnable()
      {
        public void run()
        {
          PhaseAPI.currentHealth -= this.val$dragonHealthMinus;
          if (PhaseAPI.currentHealth <= 1.0F)
          {
            for (Player p : Bukkit.getOnlinePlayers()) {
              PhaseAPI.removeBar(p);
            }
            PhaseAPI.access$6();
            if (PhaseAPI.handler != null) {
              PhaseAPI.handler.onPhaseChange(phase);
            }
          }
          else
          {
            for (AnniPlayer player : AnniPlayer.getPlayers().values())
            {
              Player p = player.getPlayer();
              if (p != null)
              {
                FakeDragon drag;
                FakeDragon drag;
                if (PhaseAPI.hasBar(p)) {
                  drag = PhaseAPI.getDragon(p, "");
                } else {
                  drag = PhaseAPI.getDragon(p, PhaseAPI.message);
                }
                drag.setName(PhaseAPI.cleanMessage(PhaseAPI.message));
                
                drag.health = PhaseAPI.currentHealth;
                PhaseAPI.sendDragon(drag, p);
              }
              else
              {
                PhaseAPI.removeBar(p);
              }
            }
          }
        }
      }, 20L, 20L).getTaskId());
  }
  
  private static void cancelTimer()
  {
    if (timer != null)
    {
      Bukkit.getScheduler().cancelTask(timer.intValue());
      timer = null;
    }
  }
  
  public static boolean hasBar(Player player)
  {
    return players.get(player.getUniqueId()) != null;
  }
  
  public static void removeBar(Player player)
  {
    if (!hasBar(player)) {
      return;
    }
    Util.sendPacket(player, getDragon(player, "").getDestroyPacket());
    
    players.remove(player.getUniqueId());
  }
  
  private static String cleanMessage(String message)
  {
    if (message.length() > 64) {
      message = message.substring(0, 63);
    }
    return message;
  }
  
  private static void sendDragon(FakeDragon dragon, Player player)
  {
    Util.sendPacket(player, dragon.getMetaPacket(dragon.getWatcher()));
    Util.sendPacket(player, dragon
      .getTeleportPacket(getDragonLocation(player.getLocation())));
  }
  
  private static FakeDragon getDragon(Player player, String message)
  {
    if (hasBar(player)) {
      return (FakeDragon)players.get(player.getUniqueId());
    }
    return addDragon(player, cleanMessage(message));
  }
  
  private static FakeDragon addDragon(Player player, String message)
  {
    FakeDragon dragon = Util.newDragon(message, 
      getDragonLocation(player.getLocation()));
    
    Util.sendPacket(player, dragon.getSpawnPacket());
    
    players.put(player.getUniqueId(), dragon);
    
    return dragon;
  }
  
  private static FakeDragon addDragon(Player player, Location loc, String message)
  {
    FakeDragon dragon = Util.newDragon(message, getDragonLocation(loc));
    
    Util.sendPacket(player, dragon.getSpawnPacket());
    
    players.put(player.getUniqueId(), dragon);
    
    return dragon;
  }
  
  private static Location getDragonLocation(Location loc)
  {
    loc.add(0.0D, -300.0D, 0.0D);
    return loc;
  }
}
