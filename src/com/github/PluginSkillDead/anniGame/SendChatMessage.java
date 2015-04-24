package com.github.PluginSkillDead.anniGame;

import com.github.PluginSkillDead.main.ScoreboardAPI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SendChatMessage extends BukkitRunnable
{
  private final AnniPlayer sender;
  private String message;
  private final ChatColor g = ChatColor.GRAY;

  public SendChatMessage(AnniPlayer p, String message)
  {
    this.sender = p;
    this.message = message;
  }

  public void run()
  {
    if (this.sender != null)
    {
      AnniPlayer player;
      if (this.sender.getTeam() == null)
      {
        String x = this.g + "(All) [" + ChatColor.DARK_PURPLE + "Lobby" + this.g + "] " + ChatColor.WHITE + this.sender.Name + ": " + this.message;
        for (Iterator localIterator = AnniPlayer.getPlayers().values().iterator(); localIterator.hasNext(); ) { player = (AnniPlayer)localIterator.next();

          Player target = player.getPlayer();
          if (target != null)
            target.sendMessage(x);
        }
      }
      else if (this.message.startsWith("!"))
      {
        this.message = this.message.substring(1);
        for (AnniPlayer player : AnniPlayer.getPlayers().values())
        {
          Player target = player.getPlayer();
          if (target != null)
            target.sendMessage(this.g + "(All) [" + this.sender.getTeam().Color + this.sender.getTeam().toString() + this.g + "] " + ChatColor.WHITE + this.sender.Name + ": " + this.message);
        }
      }
      else
      {
        for (OfflinePlayer t : ScoreboardAPI.getPlayers(this.sender.getTeam()))
        {
          Player target = Bukkit.getPlayer(t.getUniqueId());
          if (target != null)
            target.sendMessage(this.g + "(Team) [" + this.sender.getTeam().Color + this.sender.getTeam().toString() + this.g + "] " + ChatColor.WHITE + this.sender.Name + ": " + this.message);
        }
      }
    }
  }
}
