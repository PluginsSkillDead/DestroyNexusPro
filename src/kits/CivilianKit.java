package kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CivilianKit
  extends Kit
{
  private static final ItemStack Sword = KitUtils.addSoulbound(new ItemStack(Material.WOOD_SWORD));
  private static final ItemStack Pick = KitUtils.addSoulbound(new ItemStack(Material.WOOD_PICKAXE));
  private static final ItemStack Axe = KitUtils.addSoulbound(new ItemStack(Material.WOOD_AXE));
  private static final ItemStack CraftingTable = KitUtils.addSoulbound(new ItemStack(Material.WORKBENCH, 1));
  
  public void Initialize() {}
  
  public String getName()
  {
    return "Civilian";
  }
  
  public IconPackage getIconPackage()
  {
    return new IconPackage(new ItemStack(Material.WOOD_SWORD), 
      new String[] { ChatColor.AQUA + "You are the Soldier.", 
      "", 
      ChatColor.AQUA + "Stem the tide of battle", 
      ChatColor.AQUA + "with your set of wood tools!", 
      "", 
      ChatColor.GREEN + "The Default Kit. Spawns with a set of", 
      ChatColor.GREEN + "wood tools (not shovel)" });
  }
  
  public void onPlayerSpawn(Player player)
  {
    KitUtils.giveTeamArmor(player);
    player.getInventory().addItem(new ItemStack[] { Sword });
    player.getInventory().addItem(new ItemStack[] { Pick });
    player.getInventory().addItem(new ItemStack[] { Axe });
    player.getInventory().addItem(new ItemStack[] { CraftingTable });
  }
  
  public void cleanup(Player player) {}
  
  public boolean canSelect(Player player)
  {
    return true;
  }
}