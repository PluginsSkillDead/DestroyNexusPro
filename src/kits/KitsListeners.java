package kits;

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
    




