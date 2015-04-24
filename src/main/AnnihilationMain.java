package main;


import com.gmail.nuclearcat1337.anniEvents.AnniEvent;
import com.gmail.nuclearcat1337.anniEvents.AnnihilationEvent;
import com.gmail.nuclearcat1337.anniEvents.GameEndEvent;
import com.gmail.nuclearcat1337.anniEvents.GameStartEvent;
import com.gmail.nuclearcat1337.anniGame.AnniPlayer;
import com.gmail.nuclearcat1337.anniGame.AnniTeam;
import com.gmail.nuclearcat1337.anniGame.Game;
import com.gmail.nuclearcat1337.anniGame.GameListeners;
import com.gmail.nuclearcat1337.anniGame.Nexus;
import com.gmail.nuclearcat1337.anniGame.StandardPhaseHandler;
import com.gmail.nuclearcat1337.kits.CustomItem;
import com.gmail.nuclearcat1337.kits.Kit;
import com.gmail.nuclearcat1337.kits.KitListeners;
import com.gmail.nuclearcat1337.mapBuilder.RegeneratingBlock;
import com.gmail.nuclearcat1337.phaseAPI.PhaseAPI;
import com.gmail.nuclearcat1337.utils.Area;
import com.gmail.nuclearcat1337.utils.Loc;
import com.gmail.nuclearcat1337.utils.SignHandler;
import com.gmail.nuclearcat1337.utils.SignHandler.JoinSign;
import com.gmail.nuclearcat1337.utils.SignHandler.ShopSign;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AnnihilationMain
  extends JavaPlugin
  implements Listener
{
  public static boolean useProtocalHack = false;
  public static final String Name = "Annihilation";
  private FileConfiguration config;
  private File configFile;
  
  public void onEnable()
  {
    SignHandler.registerListeners(this);
    RegeneratingBlock.registerListeners(this);
    Area.registerListeners(this);
    AnniEvent.registerListener(this);
    


    loadVars();
    


    World world = Bukkit.getWorld(Game.GameWorld);
    if (world != null)
    {
      world.setGameRuleValue("doMobSpawning", "false");
      world.setGameRuleValue("doFireTick", "false");
    }
    AnniPlayer.RegisterListener(this);
    if (Game.LobbyLocation != null) {
      for (AnniPlayer p : AnniPlayer.getPlayers().values())
      {
        Player pl = p.getPlayer();
        if (pl != null)
        {
          pl.getInventory().clear();
          
          pl.getInventory().setArmorContents(null);
          pl.teleport(Game.LobbyLocation);
          pl.getInventory().addItem(new ItemStack[] { CustomItem.KITMAP.toItemStack(1) });
        }
      }
    }
    ScoreboardAPI.registerListener(this);
    new AnniCommand(this);
    
    new PhaseAPI(this);
    new TeamCommand(this);
    new GameListeners(this);
    

    PhaseAPI.setPhaseHandler(new StandardPhaseHandler());
    
    new KitListeners(this);
    

    new BukkitRunnable()
    {
      public void run()
      {
        Bukkit.broadcastMessage(ChatColor.GREEN + "You have loaded the trial version of Annihilation, " + ChatColor.LIGHT_PURPLE + "Annihilation Lite!");
      }
    }.runTaskLater(this, 20L);
  }
  
  @AnnihilationEvent
  public void onGameStart(GameStartEvent event)
  {
    for (AnniPlayer p : AnniPlayer.getPlayers().values())
    {
      Player player = p.getPlayer();
      if ((player != null) && (p.getTeam() != null))
      {
        player.setHealth(player.getHealth());
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.teleport(p.getTeam().getRandomSpawn());
        p.getKit().onPlayerSpawn(player);
      }
    }
    PhaseAPI.beginPhase(1);
    Game.Phase = 1;
    Game.NexusInvincible = true;
    ScoreboardAPI.showBoard(ChatColor.GOLD + "Map: " + (Game.GameWorld.equals(" ") ? "Test" : Game.GameWorld));
  }
  
  @AnnihilationEvent
  public void onGameEnd(GameEndEvent event)
  {
    PhaseAPI.Reset();
    Game.Phase = 0;
    Game.NexusInvincible = true;
    ScoreboardAPI.resetScoreboard();
    if (event.getWinningTeam() != null) {
      Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "The game has eneded! " + event.getWinningTeam().Color + event.getWinningTeam().getName() + " Team " + ChatColor.DARK_PURPLE + "has won!");
    }
  }
  
  public void onDisable()
  {
    PhaseAPI.Disable();
    saveStuff();
    

    Bukkit.broadcastMessage(ChatColor.GREEN + "Thank you for using the trial version of Annihilation, " + ChatColor.LIGHT_PURPLE + "Annihilation Lite!");
  }
  
  public void saveStuff()
  {
    this.config.set("GameWorldName", Game.GameWorld);
    Loc nexusLoc;
    int counter;
    Iterator localIterator2;
    for (Iterator localIterator1 = AnniTeam.Teams.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      AnniTeam team = (AnniTeam)localIterator1.next();
      
      ConfigurationSection teamSection = this.config.createSection(team.getName() + " Team");
      
      nexusLoc = team.Nexus.getLocation();
      if (nexusLoc != null) {
        ConfigUtils.saveLocation(nexusLoc, teamSection.createSection("Nexus.Location"));
      }
      Location spectatorspawn = team.getSpectatorLocation();
      if (spectatorspawn != null) {
        ConfigUtils.saveLocation(spectatorspawn, teamSection.createSection("SpectatorLocation"));
      }
      ConfigurationSection spawnSection = teamSection.createSection("Spawns");
      List<Loc> spawns = team.getSpawnList();
      if ((spawns != null) && (!spawns.isEmpty())) {
        for (int x = 0; x < spawns.size(); x++) {
          ConfigUtils.savePreciseLocation((Loc)spawns.get(x), spawnSection.createSection(x));
        }
      }
      ConfigurationSection signSection = teamSection.createSection("Signs");
      Map<String, SignHandler.JoinSign> teamsigns = SignHandler.getTeamSigns();
      counter = 1;
      localIterator2 = teamsigns.entrySet().iterator(); continue;Map.Entry<String, SignHandler.JoinSign> entry = (Map.Entry)localIterator2.next();
      if (((SignHandler.JoinSign)entry.getValue()).TeamName.equals(team.getName()))
      {
        ConfigUtils.saveLocation(Loc.fromMapKey((String)entry.getKey()), signSection.createSection(counter + ".Location"));
        signSection.set(counter + ".Direction", ((SignHandler.JoinSign)entry.getValue()).Face.name());
        signSection.set(counter + ".SignPost", Boolean.valueOf(((SignHandler.JoinSign)entry.getValue()).signPost));
        counter++;
      }
    }
    ConfigurationSection brewingSignsSection = this.config.createSection("BrewingSigns");
    int counter = 1;
    Loc loc;
    for (Map.Entry<String, SignHandler.ShopSign> entry : SignHandler.getBrewingSigns().entrySet())
    {
      loc = Loc.fromMapKey((String)entry.getKey());
      ConfigUtils.saveLocation(loc, brewingSignsSection.createSection(counter + ".Location"));
      brewingSignsSection.set(counter + ".Direction", ((SignHandler.ShopSign)entry.getValue()).Face.toString());
      brewingSignsSection.set(counter + ".SignPost", Boolean.valueOf(((SignHandler.ShopSign)entry.getValue()).signPost));
      counter++;
    }
    ConfigurationSection weaponSignsSection = this.config.createSection("WeaponSigns");
    counter = 1;
    Loc loc;
    for (Map.Entry<String, SignHandler.ShopSign> entry : SignHandler.getWeaponSigns().entrySet())
    {
      loc = Loc.fromMapKey((String)entry.getKey());
      ConfigUtils.saveLocation(loc, weaponSignsSection.createSection(counter + ".Location"));
      weaponSignsSection.set(counter + ".Direction", ((SignHandler.ShopSign)entry.getValue()).Face.toString());
      weaponSignsSection.set(counter + ".SignPost", Boolean.valueOf(((SignHandler.ShopSign)entry.getValue()).signPost));
      counter++;
    }
    ConfigurationSection enderFurnaceSection = this.config.createSection("EnderFurnaces");
    counter = 1;
    for (Map.Entry<String, SignHandler.ShopSign> entry : SignHandler.getEnderFurnaces().entrySet())
    {
      loc = Loc.fromMapKey((String)entry.getKey());
      ConfigUtils.saveLocation(loc, enderFurnaceSection.createSection(counter + ".Location"));
      enderFurnaceSection.set(counter + ".Direction", ((SignHandler.ShopSign)entry.getValue()).Face.toString());
      counter++;
    }
    ConfigurationSection regeneratingBlocksSection = this.config.createSection("RegeneratingBlocks");
    for (Loc loc = RegeneratingBlock.getRegeneratingBlocks().entrySet().iterator(); loc.hasNext(); counter.hasNext())
    {
      Map.Entry<Material, Map<Integer, RegeneratingBlock>> entry = (Map.Entry)loc.next();
      
      ConfigurationSection matSection = regeneratingBlocksSection.createSection(((Material)entry.getKey()).name());
      counter = ((Map)entry.getValue()).entrySet().iterator(); continue;Map.Entry<Integer, RegeneratingBlock> map = (Map.Entry)counter.next();
      
      ConfigurationSection dataSection = matSection.createSection(((Integer)map.getKey()).toString());
      RegeneratingBlock b = (RegeneratingBlock)map.getValue();
      dataSection.set("Type", b.Type.name());
      dataSection.set("MaterialData", Integer.valueOf(b.MaterialData));
      dataSection.set("Regenerate", Boolean.valueOf(b.Regenerate));
      dataSection.set("CobbleReplace", Boolean.valueOf(b.CobbleReplace));
      dataSection.set("NaturalBreak", Boolean.valueOf(b.NaturalBreak));
      dataSection.set("Time", Integer.valueOf(b.Time));
      dataSection.set("Unit", b.Unit.name());
      dataSection.set("XP", Integer.valueOf(b.XP));
      dataSection.set("Product", b.Product.name());
      dataSection.set("Amount", b.Amount);
      dataSection.set("ProductData", Integer.valueOf(b.ProductData));
      dataSection.set("Effect", b.Effect);
    }
    Area.saveAreas(this.config.createSection("Areas"));
    
    StandardPhaseHandler.saveDiamonds(this.config.createSection("Diamonds"));
    
    ConfigUtils.savePreciseLocation(Game.LobbyLocation, this.config.createSection("LobbyLocation"));
    try
    {
      this.config.save(this.configFile);
    }
    catch (Exception localException) {}
  }
  
  private void loadVars()
  {
    File file = new File(getDataFolder().getAbsolutePath());
    if ((!file.exists()) || (!file.isDirectory())) {
      file.mkdir();
    }
    this.configFile = new File(getDataFolder().getAbsolutePath() + "/AnniConfig.yml");
    try
    {
      if (!file.exists()) {
        file.createNewFile();
      }
      this.config = YamlConfiguration.loadConfiguration(this.configFile);
      
      useProtocalHack = this.config.getBoolean("ProtocalHack");
      if (!useProtocalHack) {
        this.config.set("ProtocalHack", Boolean.valueOf(false));
      }
      Game.GameWorld = this.config.getString("GameWorldName");
      if (Game.GameWorld == null) {
        Game.GameWorld = "";
      }
      ConfigurationSection teamSection;
      Location loc;
      for (AnniTeam team : AnniTeam.Teams)
      {
        teamSection = this.config.getConfigurationSection(team.getName() + " Team");
        if (teamSection != null)
        {
          Location nexusloc = ConfigUtils.getLocation(teamSection.getConfigurationSection("Nexus.Location"));
          if (nexusloc != null) {
            team.Nexus.setLocation(new Loc(nexusloc));
          }
          Location spectatorspawn = ConfigUtils.getLocation(teamSection.getConfigurationSection("SpectatorLocation"));
          if (spectatorspawn != null) {
            team.setSpectatorLocation(spectatorspawn);
          }
          ConfigurationSection spawns = teamSection.getConfigurationSection("Spawns");
          if (spawns != null) {
            for (String key : spawns.getKeys(false))
            {
              loc = ConfigUtils.getPreciseLocation(spawns.getConfigurationSection(key));
              if (loc != null) {
                team.addSpawn(loc);
              }
            }
          }
          ConfigurationSection signsSection = teamSection.getConfigurationSection("Signs");
          if (signsSection != null) {
            for (String key : signsSection.getKeys(false))
            {
              Location loc = ConfigUtils.getLocation(signsSection.getConfigurationSection(key + ".Location"));
              if (loc != null) {
                SignHandler.addTeamSign(team, loc.getWorld().getBlockAt(loc), BlockFace.valueOf(signsSection.getString(key + ".Direction")), signsSection.getBoolean("SignPost"));
              }
            }
          }
        }
      }
      ConfigurationSection brewingSignsSection = this.config.getConfigurationSection("BrewingSigns");
      ConfigurationSection sign;
      if (brewingSignsSection != null) {
        for (String key : brewingSignsSection.getKeys(false))
        {
          sign = brewingSignsSection.getConfigurationSection(key);
          if (sign != null)
          {
            Location loc = ConfigUtils.getLocation(sign.getConfigurationSection("Location"));
            BlockFace face = BlockFace.valueOf(sign.getString("Direction"));
            boolean post = sign.getBoolean("SignPost");
            SignHandler.addBrewingSign(loc.getWorld().getBlockAt(loc), face, post);
          }
        }
      }
      ConfigurationSection weaponSignsSection = this.config.getConfigurationSection("WeaponSigns");
      ConfigurationSection sign;
      if (weaponSignsSection != null) {
        for (String key : weaponSignsSection.getKeys(false))
        {
          sign = weaponSignsSection.getConfigurationSection(key);
          if (sign != null)
          {
            Location loc = ConfigUtils.getLocation(sign.getConfigurationSection("Location"));
            BlockFace face = BlockFace.valueOf(sign.getString("Direction"));
            boolean post = sign.getBoolean("SignPost");
            SignHandler.addWeaponSign(loc.getWorld().getBlockAt(loc), face, post);
          }
        }
      }
      ConfigurationSection enderFurnaceSection = this.config.getConfigurationSection("EnderFurnaces");
      ConfigurationSection furnace;
      if (enderFurnaceSection != null) {
        for (String key : enderFurnaceSection.getKeys(false))
        {
          furnace = enderFurnaceSection.getConfigurationSection(key);
          if (furnace != null)
          {
            Location loc = ConfigUtils.getLocation(furnace.getConfigurationSection("Location"));
            BlockFace face = BlockFace.valueOf(furnace.getString("Direction"));
            SignHandler.addEnderFurnace(loc.getWorld().getBlockAt(loc), face);
          }
        }
      }
      ConfigurationSection regeneratingBlocksSection = this.config.getConfigurationSection("RegeneratingBlocks");
      if (regeneratingBlocksSection != null) {
        for (String key : regeneratingBlocksSection.getKeys(false))
        {
          ConfigurationSection matSection = regeneratingBlocksSection.getConfigurationSection(key);
          if (matSection != null) {
            for (String dataKey : matSection.getKeys(false))
            {
              ConfigurationSection dataSection = matSection.getConfigurationSection(dataKey);
              if (dataSection != null)
              {
                Material mat = Material.getMaterial(dataSection.getString("Type"));
                Integer matData = Integer.valueOf(dataSection.getInt("MaterialData"));
                boolean regen = dataSection.getBoolean("Regenerate");
                boolean cobbleReplace = dataSection.getBoolean("CobbleReplace");
                boolean naturalBreak = dataSection.getBoolean("NaturalBreak");
                Integer time = Integer.valueOf(dataSection.getInt("Time"));
                TimeUnit unit = TimeUnit.valueOf(dataSection.getString("Unit"));
                Integer xp = Integer.valueOf(dataSection.getInt("XP"));
                Material product = Material.getMaterial(dataSection.getString("Product"));
                String amount = dataSection.getString("Amount");
                Integer productData = Integer.valueOf(dataSection.getInt("ProductData"));
                String effect = dataSection.getString("Effect");
                

                RegeneratingBlock.addRegeneratingBlock(new RegeneratingBlock(mat, matData.intValue(), regen, cobbleReplace, naturalBreak, time.intValue(), unit, xp.intValue(), product, amount, 
                  productData.intValue(), effect));
              }
            }
          }
        }
      }
      Area.loadAreas(this.config.getConfigurationSection("Areas"));
      
      StandardPhaseHandler.loadDiamonds(this.config.getConfigurationSection("Diamonds"));
      
      Location loc = ConfigUtils.getPreciseLocation(this.config.getConfigurationSection("LobbyLocation"));
      if (loc != null) {
        Game.LobbyLocation = loc;
      }
      Game.Phase = 0;
      try
      {
        this.config.save(this.configFile);
      }
      catch (Exception localException1) {}
      return;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
