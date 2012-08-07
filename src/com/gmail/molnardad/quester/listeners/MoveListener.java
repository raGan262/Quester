package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.LocObjective;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.objectives.WorldObjective;

public class MoveListener implements Listener {

	private QuestManager qm = Quester.qMan;
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
	    Player player = event.getPlayer();
	    if(qm.hasQuest(player.getName())) {
	    	// LOCATION CHECK
	    	Quest quest = qm.getPlayerQuest(player.getName());
	    	List<Objective> objs = quest.getObjectives();
	    	// if quest is ordered, process current objective
	    	if(quest.isOrdered()) {
	    		int curr = qm.getCurrentObjective(player);
	    		Objective obj = objs.get(curr);
	    		if(obj != null) {
	    			if(obj.getType().equalsIgnoreCase("LOCATION")) {
		    			LocObjective lObj = (LocObjective)obj;
		    			if(lObj.checkLocation(player.getLocation())) {
		    				qm.incProgress(player, curr);
		    				return;
		    			}
		    		} else if(obj.getType().equalsIgnoreCase("WORLD")) {
		    			WorldObjective wObj = (WorldObjective)obj;
		    			if(wObj.checkWorld(player.getWorld().getName())) {
		    				qm.incProgress(player, curr);
		    				return;
		    			}
		    		}
	    		}
	    		return;
	    	}
	    	for(int i = 0; i < objs.size(); i++) {
	    		// check if Objective is type BREAK
	    		if(objs.get(i).getType().equalsIgnoreCase("LOCATION")) {
		    		if(qm.achievedTarget(player, i)){
	    				continue;
	    			}
	    			LocObjective obj = (LocObjective)objs.get(i);
	    			if(obj.checkLocation(player.getLocation())) {
	    				qm.incProgress(player, i);
	    				return;
	    			}
	    		} else if(objs.get(i).getType().equalsIgnoreCase("WORLD")) {
		    		if(qm.achievedTarget(player, i)){
	    				continue;
	    			}
	    			WorldObjective obj = (WorldObjective)objs.get(i);
	    			if(obj.checkWorld(player.getWorld().getName())) {
	    				qm.incProgress(player, i);
	    				return;
	    			}
	    		}
	    	}
	    	
	    }
	}
	
}
