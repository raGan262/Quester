package com.gmail.molnardad.quester.listeners;

import java.util.ArrayList;

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

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.CraftObjective;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.objectives.SmeltObjective;


public class CraftSmeltListener implements Listener {

	private QuestManager qm = Quester.qMan;
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCraft(CraftItemEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) {
			return;
		}
	    Player player = (Player) event.getWhoClicked();
	    if(qm.hasQuest(player.getName())) {
			Quest quest = qm.getPlayerQuest(player.getName());
			ArrayList<Objective> objs = quest.getObjectives();
			// if quest is ordered, process current objective
			if(quest.isOrdered()) {
				int curr = qm.getCurrentObjective(player);
				Objective obj = objs.get(curr);
				if(obj != null) {
					if(obj.getType().equalsIgnoreCase("CRAFT")) {
						CraftObjective cObj = (CraftObjective) obj;
		    			ItemStack item = event.getCurrentItem();
			    		//check type of click
		    			if(event.isShiftClick()) {
		    				if(cObj.check(item)) {
		    					// how many results can be crafted
			    				int count = getCraftedAmount(event.getInventory());
			    				// how many results can fit into inventory
			    				int spc = (int) Math.floor(getInvSpace(player.getInventory(), item, count)/item.getAmount());
			    				// actual crafted amount
			    				int amtCrafted = Math.min(spc, count);
			    				if(amtCrafted > 0) {
			    					qm.incProgress(player, curr, item.getAmount()*amtCrafted);
				    				return;
			    				}
		    				}
		    			} else {
		    				ItemStack inHand = event.getCursor();
		    				if(cObj.check(item) && checkHand(inHand, item)) {
		    					qm.incProgress(player, curr, item.getAmount());
		    					return;
		    				}
		    			}
					}
				}
				return;
			}
	    	for(int i = 0; i < objs.size(); i++) {
	    		// check if Objective is type CRAFT
	    		if(objs.get(i).getType().equalsIgnoreCase("CRAFT")) {
	    			// check if it is already complete
	    			if(qm.achievedTarget(player, i)){
	    				continue;
	    			}
	    			CraftObjective obj = (CraftObjective)objs.get(i);
	    			ItemStack item = event.getCurrentItem();
		    		//check type of click
	    			if(event.isShiftClick()) {
	    				if(obj.check(item)) {
	    					// how many results can be crafted
		    				int count = getCraftedAmount(event.getInventory());
		    				// how many results can fit into inventory
		    				int spc = (int) Math.floor(getInvSpace(player.getInventory(), item, count)/item.getAmount());
		    				// actual crafted amount
		    				int amtCrafted = Math.min(spc, count);
		    				if(amtCrafted > 0) {
		    					qm.incProgress(player, i, item.getAmount()*amtCrafted);
			    				return;
		    				}
	    				}
	    			} else {
	    				ItemStack inHand = event.getCursor();
	    				if(obj.check(item) && checkHand(inHand, item)) {
	    					qm.incProgress(player, i, item.getAmount());
	    					return;
	    				}
	    			}
	    		}
	    	}
	    }
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onClick(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		if(event.getInventory().getType() != InventoryType.FURNACE || event.getRawSlot() != 2) {
			return;
		}
	    Player player = (Player) event.getWhoClicked();
	    if(qm.hasQuest(player.getName())) {
	    	Quest quest = qm.getPlayerQuest(player.getName());
	    	ArrayList<Objective> objs = quest.getObjectives();
	    	// if quest is ordered, process current objective
	    	if(quest.isOrdered()) {
	    		int curr = qm.getCurrentObjective(player);
	    		Objective obj = objs.get(curr);
	    		if(obj != null) {
	    			if(obj.getType().equalsIgnoreCase("SMELT")) {
	    				SmeltObjective sObj = (SmeltObjective) obj;
		    			ItemStack item = event.getCurrentItem();
		    			if(event.isShiftClick()) { 
		    				if(sObj.check(item)) {
			    				int spc = getInvSpace(player.getInventory(), item, 1);
			    				if(spc != 0) {
			    					qm.incProgress(player, curr, Math.min(item.getAmount(), spc));
				    				return;
			    				}	    			
		    				}
		    			} else {
			    			ItemStack inHand = event.getCursor();
			    			if(sObj.check(item) && checkHand(inHand, item)) {
			    				qm.incProgress(player, curr, item.getAmount());
			    				return;
			    			}
		    			}
	    			}
	    		}
	    		return;
	    	}
	    	for(int i = 0; i < objs.size(); i++) {
	    		// check if Objective is type SMELT
	    		if(objs.get(i).getType().equalsIgnoreCase("SMELT")) {
		    		if(qm.achievedTarget(player, i)){
	    				continue;
	    			}
	    			SmeltObjective obj = (SmeltObjective)objs.get(i);
	    			ItemStack item = event.getCurrentItem();
	    			if(event.isShiftClick()) { 
	    				if(obj.check(item)) {
		    				int spc = getInvSpace(player.getInventory(), item, 1);
		    				if(spc != 0) {
		    					qm.incProgress(player, i, Math.min(item.getAmount(), spc));
			    				return;
		    				}	    			
	    				}
	    			} else {
		    			ItemStack inHand = event.getCursor();
		    			if(obj.check(item) && checkHand(inHand, item)) {
		    				qm.incProgress(player, i, item.getAmount());
		    				return;
		    			}
	    			}
	    		}
	    	}
		}
	}
	
	public boolean checkHand(ItemStack hand, ItemStack item) {
		if(hand.getTypeId() == 0) {
			return true;
		}
		if(item.getTypeId() != hand.getTypeId())
			return false;
		if(item.getDurability() != hand.getDurability())
			return false;
		if((item.getAmount() + hand.getAmount()) > item.getMaxStackSize()) {
			return false;
		}
		return true;
	}
	

	private int getInvSpace(Inventory inv, ItemStack item, int times) {
		int result = 0;
		int amt = item.getAmount() * times;
		ItemStack[] cont = inv.getContents();
		for(ItemStack i : cont) {
			if(i == null) {
				result += item.getMaxStackSize();
			} else {
				if(i.getData().equals(item.getData())) {
					result += item.getMaxStackSize() - i.getAmount();
				}
			}
			if(result >= amt)
				break;
		}
		return result;
	}
	
	private int getCraftedAmount(CraftingInventory inv) {
		ItemStack[] cont = inv.getContents();
		int result = Integer.MAX_VALUE;
		for(int i=0; i<cont.length; i++) {
			if(cont[i] == null || cont[i].getType() == Material.AIR || i == 0)
				continue;
			if(cont[i].getAmount() < result)
				result = cont[i].getAmount();
		}
		return result;
	}
}
