package com.github.PluginSkillDead.kits;

import com.gmail.nuclearcat1337.anniGame.AnniPlayer;
import com.gmail.nuclearcat1337.anniGame.Game;
import com.gmail.nuclearcat1337.utils.IconMenu.OptionClickEvent;
import com.gmail.nuclearcat1337.utils.IconMenu.OptionClickEventHandler;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitMenuHandler
  implements IconMenu.OptionClickEventHandler
{
  private final Map<String, Kit> KitMap;

  public KitMenuHandler(Map<String, Kit> kitMap)
  {
    this.KitMap = kitMap;
  }

  public void onOptionClick(IconMenu.OptionClickEvent event)
  {
    Player player = event.getPlayer();
    if (player != null)
    {
      event.setWillClose(true);
      AnniPlayer anniplayer = AnniPlayer.getPlayer(player.getUniqueId());
      Kit k = (Kit)this.KitMap.get(event.getName());
      if ((k != null) && (anniplayer != null))
      {
        if (k.canSelect(player))
        {
          if ((Game.isGameRunning()) && (anniplayer.getKit() != null))
            anniplayer.getKit().cleanup(player);
          anniplayer.setKit(k);
          player.sendMessage(ChatColor.DARK_PURPLE + k.getName() + " selected.");
          if (Game.isGameRunning())
            player.setHealth(0.0D);
        }
      }
    }
  }
}