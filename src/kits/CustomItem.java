package kits;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum CustomItem
{
  KITMAP(ChatColor.AQUA + "Right Click to Choose a Kit", Material.BOOK, (byte)0, false, null),  MAPBUILDER(ChatColor.AQUA + "Right Click to Open the Map Builder", Material.DIAMOND_PICKAXE, (byte)0, true, null),  BREWINGSHOP(ChatColor.AQUA + "Brewing Shop Helper", Material.BREWING_STAND_ITEM, (byte)0, true, 
    new String[] {
    ChatColor.DARK_PURPLE + "Right click to add a Brewing Shop.", 
    ChatColor.DARK_PURPLE + "Left click to remove a Brewing Shop." }),  WEAPONSHOP(ChatColor.AQUA + "Weapon Shop Helper", Material.ARROW, (byte)0, true, 
    new String[] {
    ChatColor.DARK_PURPLE + "Right click to add a Weapon Shop.", 
    ChatColor.DARK_PURPLE + "Left click to remove a Weapon Shop." }),  ENDERFURNACE(ChatColor.AQUA + "Ender Furnace Helper", Material.EYE_OF_ENDER, (byte)0, true, 
    new String[] {
    ChatColor.DARK_PURPLE + "Right click to add an Ender Furnace.", 
    ChatColor.DARK_PURPLE + "Left click to remove an Ender Furnace." }),  REGENBLOCKHELPER(ChatColor.AQUA + "Regenerating Block Helper", Material.STICK, (byte)0, true, 
    new String[] {
    ChatColor.DARK_PURPLE + "Left or Right click a block to select it." }),  AREAWAND(ChatColor.AQUA + "Area Wand", Material.BLAZE_ROD, (byte)0, true, 
    new String[] {
    ChatColor.DARK_PURPLE + "Left click a block to set it as corner one.", 
    ChatColor.DARK_PURPLE + "Right click a block to set it as corner two." }),  DIAMONDHELPER(ChatColor.AQUA + "Diamond Helper", Material.DIAMOND, (byte)0, true, 
    new String[] {
    ChatColor.DARK_PURPLE + "Left click a block to add it as a diamond.", 
    ChatColor.DARK_PURPLE + "Right click a block to add it as a diamond." });
  
  private String name;
  private String[] lore;
  private Material type;
  private byte data;
  private boolean soulBound;
  
  private CustomItem(String name, Material type, byte data, boolean soulBound, String[] lore)
  {
    this.name = name;
    this.lore = lore;
    this.type = type;
    this.data = data;
    this.soulBound = soulBound;
  }
  
  public ItemStack toItemStack(int amount)
  {
    ItemStack stack = new ItemStack(this.type, amount, (short)0, Byte.valueOf(this.data));
    ItemMeta meta = stack.getItemMeta();
    if (this.name != null) {
      meta.setDisplayName(this.name);
    }
    if (this.lore != null) {
      meta.setLore(Arrays.asList(this.lore));
    }
    stack.setItemMeta(meta);
    return this.soulBound ? KitUtils.addSoulbound(stack) : stack;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String toString()
  {
    return getName();
  }
}
