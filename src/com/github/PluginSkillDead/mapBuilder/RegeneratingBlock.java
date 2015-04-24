package com.github.PluginSkillDead.mapBuilder;

import com.gmail.nuclearcat1337.anniEvents.AnniEvent;
import com.gmail.nuclearcat1337.anniEvents.ResourceBreakEvent;
import com.gmail.nuclearcat1337.anniGame.AnniPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class RegeneratingBlock
{
  private static Map<Material, Map<Integer, RegeneratingBlock>> blocks = new HashMap();
  public final Material Type;
  public final int MaterialData;
  public final boolean Regenerate;
  public final boolean CobbleReplace;
  public final boolean NaturalBreak;
  public final int Time;
  public final TimeUnit Unit;
  public final int XP;
  public final Material Product;
  public final String Amount;
  public final int ProductData;
  public final String Effect;

  public static void registerListeners(Plugin p)
  {
    blocks.clear();
    Bukkit.getPluginManager().registerEvents(new BlockListeners(), p);
  }

  public static void addRegeneratingBlock(RegeneratingBlock block)
  {
    Map datas = (Map)blocks.get(block.Type);
    if (datas == null)
      datas = new HashMap();
    datas.put(Integer.valueOf(block.MaterialData), block);
    blocks.put(block.Type, datas);
  }

  public static boolean removeRegeneratingBlock(Material type, Integer data)
  {
    if (blocks.containsKey(type))
    {
      if (data.intValue() == -1)
      {
        blocks.remove(type);
        return true;
      }

      Map datas = (Map)blocks.get(type);
      if (datas != null)
      {
        if (datas.containsKey(data))
        {
          datas.remove(data);
          return true;
        }
      }
    }
    return false;
  }

  public static Map<Material, Map<Integer, RegeneratingBlock>> getRegeneratingBlocks()
  {
    return Collections.unmodifiableMap(blocks);
  }

  public static RegeneratingBlock getRegeneratingBlock(Material type, Integer data)
  {
    Map datas = (Map)blocks.get(type);
    if (datas != null)
      return (RegeneratingBlock)datas.get(data);
    return null;
  }

  public RegeneratingBlock(Material Type, int MaterialData, boolean Regenerate, boolean CobbleReplace, boolean NaturalBreak, int Time, TimeUnit Unit, int XP, Material Product, String Amount, int ProductData, String Effect)
  {
    this.Type = Type;
    this.Regenerate = Regenerate;
    this.CobbleReplace = CobbleReplace;
    this.NaturalBreak = NaturalBreak;
    this.Time = Time;
    this.Unit = Unit;
    this.XP = XP;
    this.Product = Product;
    this.Amount = Amount;
    this.MaterialData = MaterialData;
    this.Effect = Effect;
    this.ProductData = ProductData;
  }

  private static class BlockListeners
    implements Listener
  {
    private final ScheduledExecutorService executor;
    private final Random rand;

    public BlockListeners()
    {
      this.executor = Executors.newScheduledThreadPool(4);
      this.rand = new Random(System.currentTimeMillis());
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void oreBreak(BlockBreakEvent event)
    {
      if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
      {
        Player p = event.getPlayer();
        Block block = event.getBlock();
        AnniPlayer player = AnniPlayer.getPlayer(p.getUniqueId());
        if (player != null)
        {
          RegeneratingBlock b = RegeneratingBlock.getRegeneratingBlock(block.getType(), Integer.valueOf(-1));
          if (b == null) {
            b = RegeneratingBlock.getRegeneratingBlock(block.getType(), Integer.valueOf(block.getData()));
          }

          if (b != null)
          {
            if (b.NaturalBreak)
            {
              ResourceBreakEvent e = new ResourceBreakEvent(player, b, 0, null);
              AnniEvent.callEvent(e);
              if (!e.isCancelled())
                this.executor.schedule(new FutureBlockReplace(event.getBlock()), b.Time, b.Unit);
              else
                event.setCancelled(true);
              return;
            }
            event.setCancelled(true);
            if (!b.Regenerate)
              return;
            int max;
            ResourceBreakEvent e;
            ItemStack s;
            if (b.Effect == null)
            {
              int amount = 0;
              try
              {
                amount = Integer.parseInt(b.Amount);
              }
              catch (NumberFormatException e)
              {
                try
                {
                  if (b.Amount.contains("RANDOM"))
                  {
                    String x = b.Amount.split(",")[0];
                    String y = b.Amount.split(",")[1];
                    x = x.substring(7);
                    y = y.substring(0, y.length() - 1);
                    try
                    {
                      int min = Integer.parseInt(x);
                      max = Integer.parseInt(y);
                      amount = min + (int)(Math.random() * (max - min + 1));
                    }
                    catch (NumberFormatException exx)
                    {
                      return;
                    }
                  }
                }
                catch (ArrayIndexOutOfBoundsException ex)
                {
                  return;
                }

              }

              int xp = b.XP;
              ItemStack stack;
              ItemStack stack;
              if (b.ProductData != -1)
                stack = new ItemStack(b.Product, amount, (byte)b.ProductData);
              else
                stack = new ItemStack(b.Product, amount);
              e = new ResourceBreakEvent(player, b, xp, new ItemStack[] { stack });
              AnniEvent.callEvent(e);
              if (!e.isCancelled())
              {
                if (e.getXP() > 0)
                  p.playSound(p.getLocation(), Sound.ORB_PICKUP, 0.6F, this.rand.nextFloat());
                p.giveExp(e.getXP());

                if (e.getProducts() != null)
                {
                  for (s : e.getProducts())
                  {
                    if (s != null)
                    {
                      p.getInventory().addItem(new ItemStack[] { s });
                    }
                  }
                }
                this.executor.schedule(new FutureBlockReplace(event.getBlock(), b.CobbleReplace), b.Time, b.Unit);
              }
            }
            else if (b.Effect.equalsIgnoreCase("Gravel"))
            {
              List l = new ArrayList();
              for (int x = 0; x < 5; x++)
              {
                switch (x)
                {
                case 0:
                  int z = this.rand.nextInt(2);
                  if (z != 0)
                    l.add(new ItemStack(Material.BONE, z));
                  break;
                case 1:
                  int z = this.rand.nextInt(3);
                  if (z != 0)
                    l.add(new ItemStack(Material.FEATHER, z));
                  break;
                case 2:
                  int z = this.rand.nextInt(4);
                  if (z != 0)
                    l.add(new ItemStack(Material.ARROW, z));
                  break;
                case 3:
                  int z = this.rand.nextInt(2);
                  if (z != 0)
                    l.add(new ItemStack(Material.STRING, z));
                  break;
                case 4:
                  int z = this.rand.nextInt(3);
                  if (z != 0)
                    l.add(new ItemStack(Material.FLINT, z));
                  break;
                }
              }
              ResourceBreakEvent e = new ResourceBreakEvent(player, b, b.XP, (ItemStack[])l.toArray());
              AnniEvent.callEvent(e);
              if (!e.isCancelled())
              {
                if (e.getXP() > 0)
                  p.playSound(p.getLocation(), Sound.ORB_PICKUP, 0.6F, this.rand.nextFloat());
                p.giveExp(e.getXP());
                if (e.getProducts() != null)
                {
                  s = (max = e.getProducts()).length; for (e = 0; e < s; e++) { ItemStack s = max[e];
                    p.getInventory().addItem(new ItemStack[] { s }); }
                }
                this.executor.schedule(new FutureBlockReplace(event.getBlock(), b.CobbleReplace), b.Time, b.Unit);
              }
              return;
            }
          }
        }
      }
    }
  }
}
