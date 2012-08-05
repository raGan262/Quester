package com.gmail.molnardad.quester.listeners;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.BreakObjective;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.objectives.PlaceObjective;

public class PlaceListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockPlaceEvent event) {
	    QuestManager qm = Quester.qMan;
	    Player player = event.getPlayer();
	    if(qm.hasQuest(player.getName())) {
	    	ArrayList<Objective> objs = qm.getPlayerQuest(player.getName()).getObjectives();
	    	if(QuestData.brkSubOnPlace) {
		    	for(int i = 0; i < objs.size(); i++) {
		    		// check if Objective is type BREAK
		    		if(objs.get(i).getType().equalsIgnoreCase("BREAK")) {
			    		if(qm.achievedTarget(player, i)){
		    				continue;
		    			}
			    		BreakObjective obj = (BreakObjective)objs.get(i);
		    			Block block = event.getBlock();
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
		    		if(qm.achievedTarget(player, i)){
	    				continue;
	    			}
		    		PlaceObjective obj = (PlaceObjective)objs.get(i);
	    			Block block = event.getBlock();
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
