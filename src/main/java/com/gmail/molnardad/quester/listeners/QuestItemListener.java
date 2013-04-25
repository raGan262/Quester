package com.gmail.molnardad.quester.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.utils.Util;

public class QuestItemListener implements Listener {

	/**
	 * @uml.property  name="items"
	 * @uml.associationEnd  qualifier="getName:java.lang.String [Lorg.bukkit.inventory.ItemStack;"
	 */
	private Map<String, ItemStack[]> items = new HashMap<String, ItemStack[]>();
	
	private boolean isArmor(int id) {
		return id > 297 && id < 318;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onAction(InventoryClickEvent event) {
		if(Util.isQuestItem(event.getCurrentItem())) {
			if(!event.isShiftClick() || !event.getInventory().getType().equals(InventoryType.CRAFTING) || isArmor(event.getCurrentItem().getTypeId())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onDrop(PlayerDropItemEvent event) {
		if(Util.isQuestItem(event.getItemDrop().getItemStack())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		if(Util.isPlayer(event.getEntity())) {
			List<ItemStack> itms = new ArrayList<ItemStack>();
			Iterator<ItemStack> it = event.getDrops().iterator();
			while(it.hasNext()) {
				ItemStack i = it.next();
				if(Util.isQuestItem(i)) {
					itms.add(i);
					it.remove();
				}
			}
			if(!itms.isEmpty()) {
				items.put(event.getEntity().getName(), itms.toArray(new ItemStack[0]));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSpawn(PlayerRespawnEvent event) {
		ItemStack[] itemList = items.get(event.getPlayer().getName());
		if(itemList != null) {
			Inventory inv = event.getPlayer().getInventory();
			inv.addItem(itemList);
			items.remove(event.getPlayer().getName());
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInteract(PlayerInteractEvent event) {
		if(Util.isQuestItem(event.getItem())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onDamage(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if(!Util.isPlayer(player)) {
				return;
			}
			if(Util.isQuestItem(player.getItemInHand())) {
				event.setCancelled(true);
			}
		}
	}
}
