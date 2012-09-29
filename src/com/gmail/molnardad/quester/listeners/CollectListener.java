package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.CollectObjective;
import com.gmail.molnardad.quester.objectives.Objective;

public class CollectListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPickup(PlayerPickupItemEvent event) {
	    QuestManager qm = Quester.qMan;
	    Player player = event.getPlayer();
	    if(qm.hasQuest(player.getName())) {
	    	Quest quest = qm.getPlayerQuest(player.getName());
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		// check if Objective is type COLLECT
	    		if(objs.get(i).getType().equalsIgnoreCase("COLLECT")) {
	    			if(!qm.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			CollectObjective obj = (CollectObjective)objs.get(i);
	    			ItemStack item = event.getItem().getItemStack();
	    			// compare block ID
	    			if(item.getTypeId() == obj.getMaterial().getId()) {
	    				// if DATA >= 0 compare
	    				if(obj.getData() < 0 || obj.getData() == item.getDurability()) {
	    					qm.incProgress(player, i, item.getAmount());
	    					if(QuestData.colRemPickup) {
		    					int rem = event.getRemaining();
		    					Location loc = event.getItem().getLocation();
		    					event.getItem().remove();
		    					if(rem > 0) {
		    						ItemStack newit = item.clone();
		    						newit.setAmount(rem);
		    						Item it = event.getItem().getWorld().dropItem(loc, newit);
		    						it.setVelocity(new Vector(0, 0, 0));
		    					}
		    					event.setCancelled(true);
	    					}
	    					return;
	    				}
	    			}
	    		}
	    	}
	    	
	    }
	}
	
}
