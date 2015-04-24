package com.github.PluginSkillDead.mapBuilder;

import com.github.PluginSkillDead.anniGame.AnniTeam;
import com.github.PluginSkillDead.kits.KitUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class TeamBlock
{
  public static final TeamBlock Red = new TeamBlock(AnniTeam.Red);
  public static final TeamBlock Blue = new TeamBlock(AnniTeam.Blue);
  public static final TeamBlock Green = new TeamBlock(AnniTeam.Green);
  public static final TeamBlock Yellow = new TeamBlock(AnniTeam.Yellow);
  public final AnniTeam Team;
  private final List<String> lore;
  private final byte datavalue;

  public static TeamBlock getByTeam(AnniTeam team)
  {
    if (team.getName().equalsIgnoreCase("red"))
      return Red;
    if (team.getName().equalsIgnoreCase("blue"))
      return Blue;
    if (team.getName().equalsIgnoreCase("green"))
      return Green;
    return Yellow;
  }

  private TeamBlock(AnniTeam team)
  {
    this.Team = team;
    this.lore = new ArrayList();
    if (team.equals(AnniTeam.Red))
      this.datavalue = 14;
    else if (team.equals(AnniTeam.Blue))
      this.datavalue = 11;
    else if (team.equals(AnniTeam.Green))
      this.datavalue = 13;
    else
      this.datavalue = 4;
  }

  public TeamBlock addLine(Action action, ChatColor color1, String message)
  {
    String str = "";
    if ((action == Action.LEFT_CLICK_BLOCK) || (action == Action.LEFT_CLICK_AIR))
      str = color1 + "Left click to ";
    else if ((action == Action.RIGHT_CLICK_BLOCK) || (action == Action.RIGHT_CLICK_AIR))
      str = color1 + "Right click to ";
    str = str + message;
    this.lore.add(str);
    return this;
  }

  public void clearLines()
  {
    this.lore.clear();
  }

  public String getName()
  {
    return this.Team.Color + this.Team.getName() + " Team";
  }

  public String toString()
  {
    return getName();
  }

  private ItemStack toItemStack()
  {
    ItemStack stack = new ItemStack(Material.WOOL, 1, (short)0, Byte.valueOf(this.datavalue));
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(getName());
    if (this.lore != null)
      meta.setLore(this.lore);
    stack.setItemMeta(meta);
    return KitUtils.addSoulbound(stack);
  }

  public void giveToPlayer(Player player)
  {
    ItemStack[] inv = player.getInventory().getContents();
    for (int x = 0; x < inv.length; x++)
    {
      if ((inv[x] != null) && (inv[x].getType() == Material.WOOL))
      {
        if (KitUtils.itemHasName(inv[x], getName()))
          player.getInventory().clear(x);
      }
    }
    player.getInventory().addItem(new ItemStack[] { toItemStack() });
  }

  static abstract interface TeamBlockHandler
  {
    public abstract void onBlockClick(Player paramPlayer, AnniTeam paramAnniTeam, Action paramAction, Block paramBlock, BlockFace paramBlockFace);
  }
}