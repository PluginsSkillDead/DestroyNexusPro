package com.github.PluginSkillDead.kits;

import com.gmail.nuclearcat1337.utils.IconMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class KitListeners
  implements Listener
{
  private final Map<String, Kit> Kits;
  private final IconMenu menu;

  public KitListeners(Plugin p)
  {
    Bukkit.getPluginManager().registerEvents(this, p);

    this.Kits = new TreeMap();
    this.Kits.put(Kit.CivilianInstance.getName(), Kit.CivilianInstance);

    this.menu = new IconMenu("Kit Selector", (numDiv(this.Kits.size(), 9) + 1) * 9, new KitMenuHandler(this.Kits), p, false);
    int i = 0;
    for (Kit k : this.Kits.values())
    {
      this.menu.setOption(i, k.getIconPackage().Stack, k.getName(), k.getIconPackage().Lore);
      i++;
    }
  }

  private int numDiv(int a, int b)
  {
    if (b < 2)
      throw new IllegalArgumentException();
    int result = 0;
    for (; a % b == 0; a /= b)
      result++;
    return result;
  }

  @EventHandler(priority=EventPriority.HIGHEST)
  public void openKitMenuCheck(PlayerInteractEvent event)
  {
    if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_AIR))
    {
      Player player = event.getPlayer();
      if (KitUtils.itemHasName(player.getItemInHand(), CustomItem.KITMAP.getName()))
      {
        this.menu.open(player);
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
  public void StopDrops(PlayerDropItemEvent event)
  {
    Player player = event.getPlayer();
    Item item = event.getItemDrop();
    if (item != null)
    {
      ItemStack stack = item.getItemStack();
      if (stack != null)
      {
        if (KitUtils.isSoulbound(stack))
        {
          player.playSound(player.getLocation(), Sound.BLAZE_HIT, 1.0F, 0.3F);
          event.getItemDrop().remove();
        }
      }
    }
  }

  @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
  public void RemoveDeathDrops(PlayerDeathEvent event)
  {
    for (ItemStack s : new ArrayList(event.getDrops()))
    {
      if (KitUtils.isSoulbound(s))
        event.getDrops().remove(s);
    }
  }

  @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
  public void StopClicking(InventoryClickEvent event)
  {
    HumanEntity entity = event.getWhoClicked();
    ItemStack stack = event.getCurrentItem();
    InventoryType top = event.getView().getTopInventory().getType();

    if ((stack != null) && ((entity instanceof Player)))
    {
      if ((top == InventoryType.PLAYER) || (top == InventoryType.WORKBENCH) || (top == InventoryType.CRAFTING)) {
        return;
      }
      if (KitUtils.isSoulbound(stack))
        event.setCancelled(true);
    }
  }
}