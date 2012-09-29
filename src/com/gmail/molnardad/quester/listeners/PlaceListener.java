package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.BreakObjective;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.objectives.PlaceObjective;

public class PlaceListener implements Listener {

	private QuestManager qm = Quester.qMan;
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockPlaceEvent event) {
	    Player player = event.getPlayer();
	    if(qm.hasQuest(player.getName())) {
	    	Quest quest = qm.getPlayerQuest(player.getName());
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
			Block block = event.getBlock();
	    	if(QuestData.brkSubOnPlace) {
		    	for(int i = 0; i < objs.size(); i++) {
		    		// check if Objective is type BREAK
		    		if(objs.get(i).getType().equalsIgnoreCase("BREAK")) {
		    			if(!qm.isObjectiveActive(player, i)){
		    				continue;
		    			}
			    		BreakObjective obj = (BreakObjective)objs.get(i);
		    			// compare block ID
		    			if(block.getTypeId() == obj.getMaterial().getId()) {
		    				// if DATA >= 0 compare
		    				if(obj.getData() < 0 || obj.getData() == block.getData()) {
		    					qm.incProgress(player, i, -1);
		    					break;
		    				}
		    			}
		    		}
		    	}
	    	}
	    	for(int i = 0; i < objs.size(); i++) {
	    		// check if Objective is type PLACE
	    		if(objs.get(i).getType().equalsIgnoreCase("PLACE")) {
	    			if(!qm.isObjectiveActive(player, i)){
	    				continue;
	    			}
		    		PlaceObjective obj = (PlaceObjective)objs.get(i);
	    			// compare block ID
	    			if(block.getTypeId() == obj.getMaterial().getId()) {
	    				// if DATA >= 0 compare
	    				if(obj.getData() < 0 || obj.getData() == block.getData()) {
	    					qm.incProgress(player, i);
	    					return;
	    				}
	    			}
	    		}
	    	}
	    	
	    }
	}
	
}
