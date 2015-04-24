package com.github.PluginSkillDead.mapBuilder;

import com.github.PluginSkillDead.anniGame.AnniPlayer;
import com.github.PluginSkillDead.anniGame.AnniTeam;
import com.github.PluginSkillDead.anniGame.Game;
import com.github.PluginSkillDead.anniGame.Nexus;
import com.github.PluginSkillDead.anniGame.StandardPhaseHandler;
import com.github.PluginSkillDead.kits.CustomItem;
import com.github.PluginSkillDead.kits.KitUtils;
import com.github.PluginSkillDead.utils.IconMenu;
import com.github.PluginSkillDead.utils.IconMenu.OptionClickEvent;
import com.github.PluginSkillDead.utils.IconMenu.OptionClickEventHandler;
import com.github.PluginSkillDead.utils.Loc;
import com.github.PluginSkillDead.utils.SignHandler;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class MapBuilder
  implements IconMenu.OptionClickEventHandler, Listener
{
  private final IconMenu menu;
  private final ConversationFactory factory;

  public static TimeUnit getUnit(String input)
  {
    String str;
    switch ((str = input.toLowerCase()).hashCode()) { case -1074026988:
      if (str.equals("minute")) break label406; break;
    case -906279820:
      if (str.equals("second")) break label399; break;
    case 100:
      if (str.equals("d")) break label420; break;
    case 104:
      if (str.equals("h")) break label413; break;
    case 109:
      if (str.equals("m")) break label406; break;
    case 115:
      if (str.equals("s")) break label399; break;
    case 3338:
      if (str.equals("hr")) break label413; break;
    case 99228:
      if (str.equals("day")) break label420; break;
    case 103593:
      if (str.equals("hrs")) break label413; break;
    case 108114:
      if (str.equals("min")) break label406; break;
    case 113745:
      if (str.equals("sec")) break label399; break;
    case 3076183:
      if (str.equals("days")) break label420; break;
    case 3208676:
      if (str.equals("hour")) break label413; break;
    case 3351649:
      if (str.equals("mins")) break label406; break;
    case 3526210:
      if (str.equals("secs")) break label399; case 96784904:
      if ((goto 397) || (str.equals("error"))) break; break;
    case 99469071:
      if (str.equals("hours")) break label413; break;
    case 1064901855:
      if (str.equals("minutes")) break label406; break;
    case 1970096767:
      if (str.equals("seconds")) {
        break label399;
      }
    }
    return null;

    label399: TimeUnit u = TimeUnit.SECONDS;
    break label424;

    label406: TimeUnit u = TimeUnit.MINUTES;
    break label424;

    label413: TimeUnit u = TimeUnit.HOURS;
    break label424;

    label420: TimeUnit u = TimeUnit.DAYS;

    label424: return u;
  }

  public MapBuilder(Plugin plugin)
  {
    Bukkit.getPluginManager().registerEvents(this, plugin);
    this.factory = new ConversationFactory(plugin);
    this.menu = new IconMenu("Annihilation Map Builder", 18, this, plugin, false);
    this.menu.setOption(0, new ItemStack(Material.WOOL), "Set Lobby Location", new String[] { ChatColor.GREEN + "Sets the lobby at this location." });
    this.menu.setOption(1, new ItemStack(Material.NETHER_STAR), "Set Game World", new String[] { ChatColor.GREEN + "Sets this world as the game world." });
    this.menu.setOption(2, new ItemStack(Material.BEACON), "Nexus Helper", new String[] { ChatColor.GREEN + "Lets you setup team nexuses." });
    this.menu.setOption(3, new ItemStack(Material.BED), "Add Team Spawns", new String[] { ChatColor.GREEN + "Lets you setup team spawns." });
    this.menu.setOption(4, new ItemStack(Material.SIGN), "Set Team Signs", new String[] { ChatColor.GREEN + "Lets you setup team join signs." });
    this.menu.setOption(5, new ItemStack(Material.BREWING_STAND_ITEM), "Set Brewing Shop", new String[] { ChatColor.GREEN + "Lets you set brewing shops." });
    this.menu.setOption(6, new ItemStack(Material.ARROW), "Set Weapon Shop", new String[] { ChatColor.GREEN + "Lets you set weapon shops." });
    this.menu.setOption(7, new ItemStack(Material.EYE_OF_ENDER), "Set Ender Furnace", new String[] { ChatColor.GREEN + "Lets you set ender furnaces." });
    this.menu.setOption(8, new ItemStack(Material.STICK), "Regenerating Block Helper", new String[] { ChatColor.GREEN + "Lets you setup regenerating blocks." });
    this.menu.setOption(9, new ItemStack(Material.BLAZE_ROD), "Area Helper", new String[] { ChatColor.GREEN + "Lets you setup protected areas." });
    this.menu.setOption(10, new ItemStack(Material.DIAMOND), "Diamond Helper", new String[] { ChatColor.GREEN + "Lets you setup diamond spawns." });
    this.menu.setOption(11, new ItemStack(Material.WATCH), "Phase Time", new String[] { ChatColor.GREEN + "Lets you set how long a phase is." });
    this.menu.setOption(12, new ItemStack(Material.FEATHER), "Set Team Spectator Locations", new String[] { ChatColor.GREEN + "Lets you set team spectator locations." });
  }

  public static String getReadableLocation(Location loc, ChatColor numColor, ChatColor normalColor, boolean withWorld)
  {
    return numColor + loc.getBlockX() + normalColor + ", " + numColor + loc.getBlockY() + normalColor + ", " + numColor + loc.getBlockZ() + normalColor + (
      withWorld ? " in world " + numColor + loc.getWorld().getName() + normalColor : "");
  }

  public static String getReadableLocation(Loc loc, ChatColor numColor, ChatColor normalColor, boolean withWorld)
  {
    return getReadableLocation(loc.toLocation(), numColor, normalColor, withWorld);
  }

  public void openMapBuilder(Player player)
  {
    this.menu.open(player);
  }

  public void onOptionClick(IconMenu.OptionClickEvent event)
  {
    event.setWillClose(true);
    Player player = event.getPlayer();
    AnniPlayer anniplayer = AnniPlayer.getPlayer(player.getUniqueId());
    String name = event.getName();
    if (anniplayer == null)
      anniplayer = AnniPlayer.forcePlayerLoad(player);
    String str1;
    switch ((str1 = name).hashCode()) { case -2106699678:
      if (str1.equals("Set Ender Furnace"));
      break;
    case -2074938468:
      if (str1.equals("Regenerating Block Helper"));
      break;
    case -1887571295:
      if (str1.equals("Area Helper"));
      break;
    case -1865501796:
      if (str1.equals("Set Weapon Shop"));
      break;
    case -1320525694:
      if (str1.equals("Set Game World"));
      break;
    case -959113944:
      if (str1.equals("Set Team Spectator Locations"));
      break;
    case -562398051:
      if (str1.equals("Set Lobby Location")) break; break;
    case 38811228:
      if (str1.equals("Add Team Spawns"));
      break;
    case 597440303:
      if (str1.equals("Nexus Helper"));
      break;
    case 847206769:
      if (str1.equals("Set Team Signs"));
      break;
    case 1562362580:
      if (str1.equals("Set Brewing Shop"));
      break;
    case 1623408274:
      if (str1.equals("Phase Time"));
      break;
    case 1962059834:
      if (!str1.equals("Diamond Helper")) { return;

        Game.LobbyLocation = player.getLocation();
        player.sendMessage(ChatColor.GREEN + "The game's Lobby Location has been set at " + ChatColor.LIGHT_PURPLE + getReadableLocation(Game.LobbyLocation, ChatColor.GOLD, ChatColor.LIGHT_PURPLE, true));
        return;

        Game.GameWorld = player.getWorld().getName();
        player.sendMessage(ChatColor.LIGHT_PURPLE + "The game world has been set as: " + ChatColor.GOLD + Game.GameWorld);
        return;

        for (AnniTeam team : AnniTeam.Teams)
        {
          TeamBlock t = TeamBlock.getByTeam(team);
          t.clearLines();
          t.addLine(Action.LEFT_CLICK_BLOCK, ChatColor.DARK_PURPLE, "set " + team.Color + team.getName() + " Nexus.");
          t.giveToPlayer(player);
        }
        anniplayer.setData("Team Handler", 
          new TeamBlock.TeamBlockHandler()
        {
          public void onBlockClick(Player player, AnniTeam team, Action action, Block block, BlockFace face)
          {
            if (action == Action.LEFT_CLICK_BLOCK)
            {
              Location loc = block.getLocation();
              team.Nexus.setLocation(new Loc(loc));
              player.sendMessage(team.Color + team.getName() + " Nexus " + ChatColor.LIGHT_PURPLE + "was set at " + MapBuilder.getReadableLocation(loc, ChatColor.GOLD, ChatColor.LIGHT_PURPLE, false));
            }
          }
        });
        return;

        for (AnniTeam team : AnniTeam.Teams)
        {
          TeamBlock t = TeamBlock.getByTeam(team);
          t.clearLines();
          t.addLine(Action.LEFT_CLICK_BLOCK, ChatColor.DARK_PURPLE, "add a " + team.Color + team.getName() + " spawn.");
          t.giveToPlayer(player);
        }
        anniplayer.setData("Team Handler", 
          new TeamBlock.TeamBlockHandler()
        {
          public void onBlockClick(Player player, AnniTeam team, Action action, Block block, BlockFace face)
          {
            if ((action == Action.LEFT_CLICK_BLOCK) || (action == Action.LEFT_CLICK_AIR))
            {
              team.addSpawn(player.getLocation());
              player.sendMessage(ChatColor.LIGHT_PURPLE + "A " + team.Color + team.getName() + " team " + ChatColor.LIGHT_PURPLE + "spawn has been set at " + MapBuilder.getReadableLocation(player.getLocation(), ChatColor.GOLD, ChatColor.LIGHT_PURPLE, false));
            }
          }
        });
        return;

        for (AnniTeam team : AnniTeam.Teams)
        {
          TeamBlock t = TeamBlock.getByTeam(team);
          t.clearLines();
          t.addLine(Action.RIGHT_CLICK_BLOCK, ChatColor.DARK_PURPLE, "add a " + team.Color + team.getName() + " join sign.");
          t.addLine(Action.LEFT_CLICK_BLOCK, ChatColor.DARK_PURPLE, "remove a join sign.");
          t.giveToPlayer(player);
        }
        anniplayer.setData("Team Handler", 
          new TeamBlock.TeamBlockHandler()
        {
          public void onBlockClick(Player player, AnniTeam team, Action action, Block block, BlockFace face)
          {
            if (action == Action.RIGHT_CLICK_BLOCK)
            {
              Block b = block.getRelative(face);
              if (b != null)
              {
                SignHandler.addTeamSign(team, b, MapBuilder.getCardinalDirection(player).getOppositeFace(), face == BlockFace.UP);
                player.sendMessage(team.Color + team.getName() + " team " + ChatColor.LIGHT_PURPLE + "join sign has been added at " + MapBuilder.getReadableLocation(player.getLocation(), ChatColor.GOLD, ChatColor.LIGHT_PURPLE, false));
              }
            }
            else if (action == Action.LEFT_CLICK_BLOCK)
            {
              if (SignHandler.removeJoinSign(block.getLocation()))
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Removed a join sign.");
            }
          }
        });
        return;

        player.getInventory().addItem(new ItemStack[] { CustomItem.BREWINGSHOP.toItemStack(1) });
        return;

        player.getInventory().addItem(new ItemStack[] { CustomItem.WEAPONSHOP.toItemStack(1) });
        return;

        player.getInventory().addItem(new ItemStack[] { CustomItem.ENDERFURNACE.toItemStack(1) });
        return;

        player.getInventory().addItem(new ItemStack[] { CustomItem.REGENBLOCKHELPER.toItemStack(1) });
        return;

        player.getInventory().addItem(new ItemStack[] { CustomItem.AREAWAND.toItemStack(1) });
      }
      else
      {
        player.getInventory().addItem(new ItemStack[] { CustomItem.DIAMONDHELPER.toItemStack(1) });
        return;

        player.sendMessage(ChatColor.RED + "Sorry, changing of the phase time is disabled on the trial version.");
        return;

        for (AnniTeam team : AnniTeam.Teams)
        {
          TeamBlock t = TeamBlock.getByTeam(team);
          t.clearLines();
          t.addLine(Action.LEFT_CLICK_BLOCK, ChatColor.DARK_PURPLE, "set " + team.Color + team.getName() + " spectator location.");
          t.giveToPlayer(player);
        }
        anniplayer.setData("Team Handler", 
          new TeamBlock.TeamBlockHandler()
        {
          public void onBlockClick(Player player, AnniTeam team, Action action, Block block, BlockFace face)
          {
            if (action == Action.LEFT_CLICK_AIR)
            {
              Location loc = player.getLocation();
              team.setSpectatorLocation(loc);
              player.sendMessage(team.Color + team.getName() + " team " + ChatColor.LIGHT_PURPLE + "spectator spawn has been set at " + MapBuilder.getReadableLocation(player.getLocation(), ChatColor.GOLD, ChatColor.LIGHT_PURPLE, false));
            }
          }
        });
      }
      break;
    }
  }

  public static BlockFace getCardinalDirection(Player player)
  {
    BlockFace f = null;
    double rotation = (player.getLocation().getYaw() - 90.0F) % 360.0F;
    if (rotation < 0.0D)
    {
      rotation += 360.0D;
    }
    if ((0.0D <= rotation) && (rotation < 22.5D))
    {
      f = BlockFace.EAST;
    }
    else if ((22.5D <= rotation) && (rotation < 33.75D))
    {
      f = BlockFace.EAST;
    }
    else if ((33.75D <= rotation) && (rotation < 67.5D))
    {
      f = BlockFace.SOUTH;
    }
    else if ((67.5D <= rotation) && (rotation < 112.5D))
    {
      f = BlockFace.SOUTH;
    }
    else if ((112.5D <= rotation) && (rotation < 78.75D))
    {
      f = BlockFace.SOUTH;
    }
    else if ((78.75D <= rotation) && (rotation < 157.5D))
    {
      f = BlockFace.WEST;
    }
    else if ((157.5D <= rotation) && (rotation < 202.5D))
    {
      f = BlockFace.WEST;
    }
    else if ((202.5D <= rotation) && (rotation < 123.75D))
    {
      f = BlockFace.WEST;
    }
    else if ((123.75D <= rotation) && (rotation < 247.5D))
    {
      f = BlockFace.NORTH;
    }
    else if ((247.5D <= rotation) && (rotation < 292.5D))
    {
      f = BlockFace.NORTH;
    }
    else if ((292.5D <= rotation) && (rotation < 168.75D))
    {
      f = BlockFace.NORTH;
    }
    else if ((168.75D <= rotation) && (rotation < 337.5D))
    {
      f = BlockFace.EAST;
    }
    else if ((337.5D <= rotation) && (rotation < 360.0D))
    {
      f = BlockFace.EAST;
    }
    else
    {
      f = null;
    }
    return f.getOppositeFace();
  }

  @EventHandler(priority=EventPriority.HIGH)
  public void openMapBuilderCheck(PlayerInteractEvent event)
  {
    if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR))
    {
      Player player = event.getPlayer();
      if (KitUtils.itemHasName(player.getItemInHand(), CustomItem.MAPBUILDER.getName()))
      {
        this.menu.open(player);
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
  public void brewingAndWeaponCheck(PlayerInteractEvent event)
  {
    if ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_BLOCK))
    {
      Player player = event.getPlayer();
      if (KitUtils.itemHasName(player.getItemInHand(), CustomItem.BREWINGSHOP.getName()))
      {
        event.setCancelled(true);
        Block b = event.getClickedBlock().getRelative(event.getBlockFace());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
          BlockFace face = event.getBlockFace();
          if (b != null) {
            SignHandler.addBrewingSign(b, getCardinalDirection(player).getOppositeFace(), face == BlockFace.UP);
          }

        }
        else if (SignHandler.removeBrewingSign(event.getClickedBlock().getLocation())) {
          player.sendMessage(ChatColor.LIGHT_PURPLE + "Removed a Brewing Shop.");
        }
      }
      else if (KitUtils.itemHasName(player.getItemInHand(), CustomItem.WEAPONSHOP.getName()))
      {
        event.setCancelled(true);
        Block b = event.getClickedBlock().getRelative(event.getBlockFace());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
          BlockFace face = event.getBlockFace();
          if (b != null) {
            SignHandler.addWeaponSign(b, getCardinalDirection(player).getOppositeFace(), face == BlockFace.UP);
          }

        }
        else if (SignHandler.removeWeaponSign(event.getClickedBlock().getLocation())) {
          player.sendMessage(ChatColor.LIGHT_PURPLE + "Removed a Weapon Shop.");
        }
      }
      else if (KitUtils.itemHasName(player.getItemInHand(), CustomItem.ENDERFURNACE.getName()))
      {
        event.setCancelled(true);
        Block b = event.getClickedBlock().getRelative(event.getBlockFace());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
          if (b != null) {
            SignHandler.addEnderFurnace(b, getCardinalDirection(player).getOppositeFace());
          }

        }
        else if (SignHandler.removeEnderFurnace(event.getClickedBlock().getLocation())) {
          player.sendMessage(ChatColor.LIGHT_PURPLE + "Removed an Ender Furnace.");
        }

      }
      else if (KitUtils.itemHasName(player.getItemInHand(), CustomItem.REGENBLOCKHELPER.getName()))
      {
        event.setCancelled(true);
        if (!player.isConversing())
        {
          Block b = event.getClickedBlock();
          Conversation conv = this.factory.withModality(true).withFirstPrompt(new RegenBlockPrompt(b)).withLocalEcho(true).buildConversation(player);
          conv.begin();
        }
      }
      else if (KitUtils.itemHasName(player.getItemInHand(), CustomItem.DIAMONDHELPER.getName()))
      {
        event.setCancelled(true);
        Block b = event.getClickedBlock();
        if (event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
          StandardPhaseHandler.addDiamond(new Loc(b.getLocation()));
          player.sendMessage(ChatColor.LIGHT_PURPLE + "Diamond added!");
        }
        else if (StandardPhaseHandler.removeDiamond(new Loc(b.getLocation()))) {
          player.sendMessage(ChatColor.LIGHT_PURPLE + "Diamond removed!");
        }
      }
    }
  }

  @EventHandler(priority=EventPriority.HIGH)
  public void nexusHelperCheck(PlayerInteractEvent event)
  {
    if ((event.getAction() == Action.LEFT_CLICK_AIR) || (event.getAction() == Action.LEFT_CLICK_BLOCK) || 
      (event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK))
    {
      Player player = event.getPlayer();
      TeamBlock t = null;
      if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Red.getName()))
        t = TeamBlock.Red;
      else if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Blue.getName()))
        t = TeamBlock.Blue;
      else if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Green.getName()))
        t = TeamBlock.Green;
      else if (KitUtils.itemHasName(player.getItemInHand(), TeamBlock.Yellow.getName()))
        t = TeamBlock.Yellow;
      if (t != null)
      {
        event.setCancelled(true);
        AnniPlayer anniplayer = AnniPlayer.getPlayer(player.getUniqueId());
        if (anniplayer != null)
        {
          Object obj = anniplayer.getData("Team Handler");
          if ((obj != null) && ((obj instanceof TeamBlock.TeamBlockHandler)))
          {
            ((TeamBlock.TeamBlockHandler)obj).onBlockClick(player, t.Team, event.getAction(), event.getClickedBlock(), event.getBlockFace());
          }
        }
      }
    }
  }
}