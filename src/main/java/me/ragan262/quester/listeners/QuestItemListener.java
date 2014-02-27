package me.ragan262.quester.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.utils.Util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class QuestItemListener implements Listener {
	
	private final Map<String, ItemStack[]> items = new HashMap<String, ItemStack[]>();
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onAction(final PlayerInteractEvent event) {
		if(Util.isQuestItem(event.getItem())
				&& !QConfiguration.questItemInteractable.contains(event.getItem().getTypeId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onClick(final InventoryClickEvent event) {
		if(Util.isQuestItem(event.getCurrentItem())) {
			if(!event.isShiftClick()
					|| !event.getInventory().getType().equals(InventoryType.CRAFTING)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onDrop(final PlayerDropItemEvent event) {
		if(Util.isQuestItem(event.getItemDrop().getItemStack())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(final PlayerDeathEvent event) {
		if(Util.isPlayer(event.getEntity())) {
			final List<ItemStack> itms = new ArrayList<ItemStack>();
			final Iterator<ItemStack> it = event.getDrops().iterator();
			while(it.hasNext()) {
				final ItemStack i = it.next();
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
	public void onSpawn(final PlayerRespawnEvent event) {
		final ItemStack[] itemList = items.get(event.getPlayer().getName());
		if(itemList != null) {
			final Inventory inv = event.getPlayer().getInventory();
			inv.addItem(itemList);
			items.remove(event.getPlayer().getName());
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlace(final BlockPlaceEvent event) {
		if(Util.isQuestItem(event.getItemInHand())) {
			event.setCancelled(true);
		}
	}
}
