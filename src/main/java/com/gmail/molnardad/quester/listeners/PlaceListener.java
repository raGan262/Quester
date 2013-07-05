package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.BreakObjective;
import com.gmail.molnardad.quester.objectives.PlaceObjective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;

public class PlaceListener implements Listener {

	private ProfileManager profMan;
	
	public PlaceListener(Quester plugin) {
		this.profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockPlaceEvent event) {
	    Player player = event.getPlayer();
    	Quest quest = profMan.getProfile(player.getName()).getQuest();
	    if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
			Block block = event.getBlock();
	    	if(QConfiguration.brkSubOnPlace) {
		    	for(int i = 0; i < objs.size(); i++) {
		    		// check if Objective is type BREAK
		    		if(objs.get(i).getType().equalsIgnoreCase("BREAK")) {
		    			if(!profMan.isObjectiveActive(player, i)){
		    				continue;
		    			}
			    		BreakObjective obj = (BreakObjective)objs.get(i);
		    			// compare block ID
		    			if(block.getTypeId() == obj.getMaterial().getId()) {
		    				// if DATA >= 0 compare
		    				if(obj.getData() < 0 || obj.getData() == block.getData()) {
		    					profMan.incProgress(player, ActionSource.listenerSource(event), i, -1);
		    					break;
		    				}
		    			}
		    		}
		    	}
	    	}
	    	for(int i = 0; i < objs.size(); i++) {
	    		// check if Objective is type PLACE
	    		if(objs.get(i).getType().equalsIgnoreCase("PLACE")) {
	    			if(!profMan.isObjectiveActive(player, i)){
	    				continue;
	    			}
		    		PlaceObjective obj = (PlaceObjective)objs.get(i);
	    			// compare block ID
	    			if(block.getTypeId() == obj.getMaterial().getId()) {
	    				// if DATA >= 0 compare
	    				if(obj.getData() < 0 || obj.getData() == block.getData()) {
	    					profMan.incProgress(player, ActionSource.listenerSource(event), i);
	    					return;
	    				}
	    			}
	    		}
	    	}
	    	
	    }
	}
	
}
