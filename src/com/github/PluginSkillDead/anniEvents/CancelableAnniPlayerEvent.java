package com.github.PluginSkillDead.anniEvents;

abstract class CancelableAnniEvent extends AnniEvent
{
  protected boolean canceled;

  public CancelableAnniEvent()
  {
    this.canceled = false;
  }

  public boolean isCancelled()
  {
    return this.canceled;
  }

  public void setCancelled(boolean cancel)
  {
    this.canceled = cancel;
  }
}