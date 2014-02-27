package me.ragan262.quester.listeners;

import java.util.List;

import me.ragan262.quester.ActionSource;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.objectives.CraftObjective;
import me.ragan262.quester.objectives.SmeltObjective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CraftSmeltListener implements Listener {
	
	private final ProfileManager profMan;
	
	public CraftSmeltListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCraft(final CraftItemEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		final Player player = (Player) event.getWhoClicked();
		final PlayerProfile prof = profMan.getProfile(player.getName());
		final Quest quest = prof.getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				// check if Objective is type CRAFT
				if(objs.get(i).getType().equalsIgnoreCase("CRAFT")) {
					// check if it is already complete
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final CraftObjective obj = (CraftObjective) objs.get(i);
					final ItemStack item = event.getCurrentItem();
					// check type of click
					if(event.isShiftClick()) {
						if(obj.check(item)) {
							// how many results can be crafted
							final int count = getCraftedAmount(event.getInventory());
							// how many results can fit into inventory
							final int spc =
									(int) Math
											.floor(getInvSpace(player.getInventory(), item, count)
													/ item.getAmount());
							// actual crafted amount
							final int amtCrafted = Math.min(spc, count);
							if(amtCrafted > 0) {
								profMan.incProgress(player, ActionSource.listenerSource(event), i,
										item.getAmount() * amtCrafted);
								return;
							}
						}
					}
					else {
						final ItemStack inHand = event.getCursor();
						if(obj.check(item) && checkHand(inHand, item)) {
							profMan.incProgress(player, ActionSource.listenerSource(event), i,
									item.getAmount());
							return;
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onClick(final InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		if(event.getInventory().getType() != InventoryType.FURNACE || event.getRawSlot() != 2) {
			return;
		}
		final Player player = (Player) event.getWhoClicked();
		final PlayerProfile prof = profMan.getProfile(player.getName());
		final Quest quest = prof.getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				// check if Objective is type SMELT
				if(objs.get(i).getType().equalsIgnoreCase("SMELT")) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final SmeltObjective obj = (SmeltObjective) objs.get(i);
					final ItemStack item = event.getCurrentItem();
					if(event.isShiftClick()) {
						if(obj.check(item)) {
							final int spc = getInvSpace(player.getInventory(), item, 1);
							if(spc != 0) {
								profMan.incProgress(player, ActionSource.listenerSource(event), i,
										Math.min(item.getAmount(), spc));
								return;
							}
						}
					}
					else {
						final ItemStack inHand = event.getCursor();
						if(obj.check(item) && checkHand(inHand, item)) {
							profMan.incProgress(player, ActionSource.listenerSource(event), i,
									item.getAmount());
							return;
						}
					}
				}
			}
		}
	}
	
	public boolean checkHand(final ItemStack hand, final ItemStack item) {
		if(hand.getTypeId() == 0) {
			return true;
		}
		if(item.getTypeId() != hand.getTypeId()) {
			return false;
		}
		if(item.getDurability() != hand.getDurability()) {
			return false;
		}
		if(item.getAmount() + hand.getAmount() > item.getMaxStackSize()) {
			return false;
		}
		return true;
	}
	
	private int getInvSpace(final Inventory inv, final ItemStack item, final int times) {
		int result = 0;
		final int amt = item.getAmount() * times;
		final ItemStack[] cont = inv.getContents();
		for(final ItemStack i : cont) {
			if(i == null) {
				result += item.getMaxStackSize();
			}
			else {
				if(i.getData().equals(item.getData())) {
					result += item.getMaxStackSize() - i.getAmount();
				}
			}
			if(result >= amt) {
				break;
			}
		}
		return result;
	}
	
	private int getCraftedAmount(final CraftingInventory inv) {
		final ItemStack[] cont = inv.getContents();
		int result = Integer.MAX_VALUE;
		for(int i = 0; i < cont.length; i++) {
			if(cont[i] == null || cont[i].getType() == Material.AIR || i == 0) {
				continue;
			}
			if(cont[i].getAmount() < result) {
				result = cont[i].getAmount();
			}
		}
		return result;
	}
}
