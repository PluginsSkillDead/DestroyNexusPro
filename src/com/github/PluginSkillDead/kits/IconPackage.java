package com.github.PluginSkillDead.kits;

import org.bukkit.inventory.ItemStack;

public class IconPackage
{
  public final ItemStack Stack;
  public final String[] Lore;

  public IconPackage(ItemStack Stack, String[] lore)
  {
    this.Stack = Stack;
    this.Lore = lore;
  }
}