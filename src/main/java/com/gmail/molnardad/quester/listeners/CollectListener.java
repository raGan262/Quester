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

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.CollectObjective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;

public class CollectListener implements Listener {
	
	private ProfileManager profMan = null;
	
	public CollectListener(Quester plugin) {
		this.profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPickup(PlayerPickupItemEvent event) {
	    Player player = event.getPlayer();
    	Quest quest = profMan.getProfile(player.getName()).getQuest();
	    if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		int[] progress = profMan.getProfile(player.getName()).getProgress().getProgress();
	    		// check if Objective is type COLLECT
	    		if(objs.get(i).getType().equalsIgnoreCase("COLLECT")) {
	    			if(!profMan.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			CollectObjective obj = (CollectObjective)objs.get(i);
	    			ItemStack item = event.getItem().getItemStack();
	    			// compare block ID
	    			if(item.getTypeId() == obj.getMaterial().getId()) {
	    				// if DATA >= 0 compare
	    				if(obj.getData() < 0 || obj.getData() == item.getDurability()) {
	    					int rem = event.getRemaining(); // amount not picked up (full inventory)
	    					int req = obj.getTargetAmount() - progress[i]; // amount required by objective
	    					if(req < 0) { // can't be less than 0
	    						req = 0;
	    					}
	    					int more = item.getAmount() - req; // difference between amount picked up and amount required
	    					if(more < 0) { // can't be less than 0
	    						more = 0;
	    					}
	    					profMan.incProgress(player, ActionSource.listenerSource(event), i, item.getAmount()); // increase by amount actually picked up
	    					if(QConfiguration.colRemPickup) {
		    					Location loc = event.getItem().getLocation();
		    					event.getItem().remove();
		    					if((more + rem) > 0) {
		    						ItemStack newit = item.clone();
		    						newit.setAmount(rem + more); // spawn left on the ground +
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
