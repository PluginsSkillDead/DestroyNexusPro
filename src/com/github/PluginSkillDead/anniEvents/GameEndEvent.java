package com.github.PluginSkillDead.anniEvents;

import com.github.PluginSkillDead.anniGame.AnniTeam;

public final class GameEndEvent extends AnniEvent
{
  private final AnniTeam winner;

  public GameEndEvent(AnniTeam winningTeam)
  {
    this.winner = winningTeam;
  }

  public AnniTeam getWinningTeam()
  {
    return this.winner;
  }
}