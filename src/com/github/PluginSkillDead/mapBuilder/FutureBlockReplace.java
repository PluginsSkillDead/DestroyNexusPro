package com.github.PluginSkillDead.mapBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

public class FutureBlockReplace
  implements Runnable
{
  private final BlockState state;

  public FutureBlockReplace(Block b)
  {
    this.state = b.getState();
  }

  public FutureBlockReplace(Block b, boolean cobble)
  {
    this.state = b.getState();
    b.setType(cobble ? Material.COBBLESTONE : Material.AIR);
  }

  public void run()
  {
    Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Annihilation"), new Runnable()
    {
      public void run()
      {
        FutureBlockReplace.this.state.update(true);
      }
    });
  }
}