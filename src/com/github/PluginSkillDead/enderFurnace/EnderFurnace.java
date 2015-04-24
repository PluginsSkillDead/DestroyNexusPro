package com.github.PluginSkillDead.enderFurnace;

import net.minecraft.server.v1_7_R3.Block;
import net.minecraft.server.v1_7_R3.Blocks;
import net.minecraft.server.v1_7_R3.EntityHuman;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.TileEntityFurnace;
import net.minecraft.server.v1_7_R3.World;
import org.bukkit.block.Furnace;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.block.CraftFurnace;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class EnderFurnace extends TileEntityFurnace
  implements AbstractFurnace
{
  private EntityPlayer owningPlayer;
  private int id;

  public EnderFurnace(Player p, int i)
  {
    EntityPlayer player = ((CraftPlayer)p).getHandle();
    this.owningPlayer = player;
    this.world = player.world;
    this.id = i;
    try
    {
      ReflectionUtil.setSuperValue(this, "o", "Ender Furnace");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public boolean a(EntityHuman entityhuman)
  {
    return true;
  }

  public int p()
  {
    return 0;
  }

  public Block q()
  {
    return Blocks.FURNACE;
  }

  public InventoryHolder getOwner()
  {
    Furnace furnace = new CraftFurnace(this.world.getWorld().getBlockAt(0, 
      0, 0));
    try
    {
      ReflectionUtil.setValue(furnace, "furnace", this);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return furnace;
  }

  public void open()
  {
    this.owningPlayer.openFurnace(this);
  }

  public int getId()
  {
    return this.id;
  }

  public void tick()
  {
    h();
  }

  public void setItemStack(int i, ItemStack itemstack)
  {
    setItem(i, CraftItemStack.asNMSCopy(itemstack));
  }

  public ItemStack getItemStack(int i)
  {
    return CraftItemStack.asBukkitCopy(getItem(i));
  }
}