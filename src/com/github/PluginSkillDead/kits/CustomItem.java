package com.github.PluginSkillDead.kits;

import com.gmail.nuclearcat1337.anniGame.AnniPlayer;
import com.gmail.nuclearcat1337.anniGame.AnniTeam;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class KitUtils
{
  private static ItemStack[] armor = { addSoulbound(new ItemStack(Material.LEATHER_BOOTS)), 
    addSoulbound(new ItemStack(Material.LEATHER_LEGGINGS)), 
    addSoulbound(new ItemStack(Material.LEATHER_CHESTPLATE)), 
    addSoulbound(new ItemStack(Material.LEATHER_HELMET)) };

  public static boolean isSoulbound(ItemStack stack)
  {
    ItemMeta meta = stack.getItemMeta();
    if (meta == null)
      return false;
    List lore = meta.getLore();
    if (lore == null)
      return false;
    return lore.contains(ChatColor.GOLD + "Soulbound");
  }

  public static ItemStack addSoulbound(ItemStack stack)
  {
    if (stack == null)
      return stack;
    ItemMeta meta = stack.getItemMeta();
    if (meta == null)
      meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
    List lore = meta.getLore();
    if (lore == null)
      lore = new ArrayList();
    lore.add(ChatColor.GOLD + "Soulbound");
    meta.setLore(lore);
    stack.setItemMeta(meta);
    return stack;
  }

  public static ItemStack addEnchant(ItemStack s, Enchantment m, int level)
  {
    s.addUnsafeEnchantment(m, level);
    return s;
  }

  public static boolean itemHasName(ItemStack stack, String name)
  {
    if (stack == null)
      return false;
    ItemMeta meta = stack.getItemMeta();
    if (meta == null)
      return false;
    if (!meta.hasDisplayName())
      return false;
    return meta.getDisplayName().equalsIgnoreCase(name);
  }

  public static ItemStack[] coloredArmor(AnniTeam team)
  {
    Color c;
    Color c;
    if (team.Color == ChatColor.RED) {
      c = Color.RED;
    }
    else
    {
      Color c;
      if (team.Color == ChatColor.BLUE) {
        c = Color.BLUE;
      }
      else
      {
        Color c;
        if (team.Color == ChatColor.GREEN)
          c = Color.GREEN;
        else
          c = Color.YELLOW; 
      }
    }
    for (ItemStack stack : armor)
    {
      LeatherArmorMeta meta = (LeatherArmorMeta)stack.getItemMeta();
      meta.setColor(c);
      stack.setItemMeta(meta);
    }
    return armor;
  }

  public static void giveTeamArmor(Player player)
  {
    AnniPlayer pl = AnniPlayer.getPlayer(player.getUniqueId());
    if (pl != null)
    {
      AnniTeam t = pl.getTeam();
      if (t != null)
      {
        player.getInventory().setArmorContents(coloredArmor(t));
      }
    }
  }
}