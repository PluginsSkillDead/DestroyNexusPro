package com.github.PluginSkillDead.anniEvents;

import com.github.PluginSkillDead.anniGame.AnniPlayer;

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