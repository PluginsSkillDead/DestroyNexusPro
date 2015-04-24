package com.github.PluginSkillDead.anniEvents;

import com.gmail.nuclearcat1337.anniGame.AnniPlayer;
import com.gmail.nuclearcat1337.mapBuilder.RegeneratingBlock;
import org.bukkit.inventory.ItemStack;

public class ResourceBreakEvent extends CancelableAnniPlayerEvent
{
  private RegeneratingBlock resource;
  private int xp;
  private ItemStack[] endresult;

  public ResourceBreakEvent(AnniPlayer player, RegeneratingBlock resource, int XP, ItemStack[] endResult)
  {
    super(player);
    this.resource = resource;
    this.xp = XP;
    this.endresult = endResult;
  }

  public RegeneratingBlock getResource()
  {
    return this.resource;
  }

  public int getXP()
  {
    return this.xp;
  }

  public void setXP(int XP)
  {
    this.xp = XP;
  }

  public ItemStack[] getProducts()
  {
    return this.endresult;
  }

  public void setProducts(ItemStack[] results)
  {
    this.endresult = results;
  }
}