package com.github.PluginSkillDead.main;

import com.github.PluginSkillDead.anniGame.AnniPlayer;
import com.github.PluginSkillDead.anniGame.AnniTeam;
import com.github.PluginSkillDead.anniGame.Game;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class TeamCommand
  implements CommandExecutor
{
  private ChatColor r = ChatColor.RED;

  public TeamCommand(AnnihilationMain plugin)
  {
    plugin.getCommand("Team").setExecutor(this);
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if ((sender instanceof Player))
    {
      AnniPlayer p = AnniPlayer.getPlayer(((Player)sender).getUniqueId());
      if (args.length == 0)
      {
        String[] messages = new String[8];
        for (int x = 0; x < 4; x++)
        {
          AnniTeam t = (AnniTeam)AnniTeam.Teams.get(x);
          int cat = x * 2;
          messages[cat] = (t.Color + "/Team " + t.getName() + ":");
          int y = ScoreboardAPI.getPlayerCount(t);
          messages[(cat + 1)] = (t.Color + "There are " + y + " Players on the " + t.toString() + " Team.");
        }
        sender.sendMessage(messages);
      }
      else if (args.length == 1)
      {
        if (args[0].equalsIgnoreCase("leave"))
        {
          if (!Game.isGameRunning())
          {
            if (p.getTeam() != null)
            {
              sender.sendMessage(ChatColor.DARK_PURPLE + "You left " + p.getTeam().Color + p.getTeam().getName() + " Team");
              ScoreboardAPI.removePlayer(p.getTeam(), p.getPlayer());
              p.setTeam(null);
            } else {
              sender.sendMessage(ChatColor.RED + "You do not have a team to leave.");
            }
          } else sender.sendMessage(ChatColor.RED + "You cannot leave a team while the game is running.");
        }
        else
        {
          AnniTeam t = AnniTeam.getTeamByName(args[0]);
          if (t != null)
          {
            attemptJoin(t, p);
          }
          else
          {
            sender.sendMessage(this.r + "Invalid Team specified!");
          }
        }
      } else { sender.sendMessage("/Team [Name] (Red,Green,Blue,Yellow)"); }
    }
    else {
      sender.sendMessage(this.r + "This command can only be used by a player!");
    }return true;
  }

  private void attemptJoin(AnniTeam team, AnniPlayer p)
  {
    if ((p != null) && (team != null))
    {
      Object obj = p.getData("TeamDelay");
      if ((obj == null) || (System.currentTimeMillis() >= ((Long)obj).longValue()))
      {
        p.setData("TeamDelay", Long.valueOf(System.currentTimeMillis() + 1000L));
        Player player = p.getPlayer();
        if (Game.Phase < 3)
        {
          if (p.getTeam() == null)
          {
            if (team.isTeamDead())
            {
              player.sendMessage(ChatColor.RED + "You Cannot Join a Team Whose Nexus is Destroyed!");
              return;
            }

            int x = ScoreboardAPI.getPlayerCount(team);
            Integer[] counts = ScoreboardAPI.teamCounts();
            if (x + 1 - counts[0].intValue() > 2)
            {
              player.sendMessage(ChatColor.RED + "This team is currently full!");
              return;
            }

            p.setTeam(team);
            ScoreboardAPI.addPlayer(team, player);
            player.sendMessage(ChatColor.DARK_PURPLE + "You have joined " + team.Color + team.toString() + ChatColor.DARK_PURPLE + " Team");
            if (Game.isGameRunning())
              player.setHealth(0.0D);
          } else {
            player.sendMessage(ChatColor.RED + "You Already Have a Team!");
          }
        } else player.sendMessage(ChatColor.RED + "You cannot join a team after Phase 2");
      }
    }
  }
}