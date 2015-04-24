package com.github.PluginSkillDead.main;

import com.gmail.nuclearcat1337.anniGame.Game;
import com.gmail.nuclearcat1337.kits.CustomItem;
import com.gmail.nuclearcat1337.mapBuilder.MapBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class AnniCommand
  implements CommandExecutor
{
  private AnnihilationMain plugin;

  public AnniCommand(AnnihilationMain plugin)
  {
    this.plugin = plugin;
    plugin.getCommand("anni").setExecutor(this);
    new MapBuilder(plugin);
  }

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if ((!(sender instanceof Player)) || (((Player)sender).hasPermission("A.anni")))
    {
      if (args.length == 1)
      {
        if (args[0].equalsIgnoreCase("start"))
        {
          if (!Game.isGameRunning())
          {
            Game.startGame();
            sender.sendMessage(ChatColor.GREEN + "The game has begun!");
          } else {
            sender.sendMessage(ChatColor.RED + "The game is already running.");
          }
        } else if (args[0].equalsIgnoreCase("stop"))
        {
          if (Game.isGameRunning())
          {
            Game.stopGame(null);
            sender.sendMessage(ChatColor.GREEN + "The game has been stopped.");
          } else {
            sender.sendMessage(ChatColor.RED + "The game is not running.");
          }
        } else if (args[0].equalsIgnoreCase("kitmap"))
        {
          if ((sender instanceof Player))
          {
            Player p = (Player)sender;
            p.getInventory().addItem(new ItemStack[] { CustomItem.KITMAP.toItemStack(1) });
          } else {
            sender.sendMessage(ChatColor.RED + "Must be a player to get a kitmap");
          }
        } else if (args[0].equalsIgnoreCase("mapbuilder"))
        {
          if ((sender instanceof Player))
          {
            Player player = (Player)sender;
            player.getInventory().addItem(new ItemStack[] { CustomItem.MAPBUILDER.toItemStack(1) });
          } else {
            sender.sendMessage(ChatColor.RED + "Must be a player to get a mapbuilder");
          }
        } else if (args[0].equalsIgnoreCase("save"))
        {
          this.plugin.saveStuff();
          sender.sendMessage(ChatColor.GREEN + "Annihilation Config Saved!");
        } else {
          sender.sendMessage(ChatColor.LIGHT_PURPLE + "/Anni " + ChatColor.GREEN + "[Start,Stop,Mapbuilder]");
        }
      } else sender.sendMessage(ChatColor.LIGHT_PURPLE + "/Anni " + ChatColor.GREEN + "[Start,Stop,Mapbuilder]");
    }
    return true;
  }
}