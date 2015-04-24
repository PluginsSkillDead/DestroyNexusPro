package com.github.PluginSkillDead.anniEvents;

import com.gmail.nuclearcat1337.anniGame.AnniPlayer;

abstract class AnniPlayerEvent extends AnniEvent
{
  private final AnniPlayer player;

  public AnniPlayerEvent(AnniPlayer player)
  {
    this.player = player;
  }

  public AnniPlayer getPlayer()
  {
    return this.player;
  }
}