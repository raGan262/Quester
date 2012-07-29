package com.gmail.molnardad.quester.listeners;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.LocObjective;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.objectives.WorldObjective;

public class MoveListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		QuestManager qm = Quester.qMan;
	    Player player = event.getPlayer();
	    if(qm.hasQuest(player.getName())) {
	    	// LOCATION CHECK
	    	if(qm.getPlayerQuest(player.getName()).getObjectives("LOCATION").isEmpty() && qm.getPlayerQuest(player.getName()).getObjectives("WORLD").isEmpty()) {
	    		return;
	    	}
	    	ArrayList<Objective> objs = qm.getPlayerQuest(player.getName()).getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		// check if Objective is type BREAK
	    		if(qm.achievedTarget(player, i)){
    				continue;
    			}
	    		if(objs.get(i).getType().equalsIgnoreCase("LOCATION")) {
	    			LocObjective obj = (LocObjective)objs.get(i);
	    			if(obj.checkLocation(player.getLocation())) {
	    				qm.incProgress(player, i);
	    			}
	    		} else if(objs.get(i).getType().equalsIgnoreCase("WORLD")) {
	    			WorldObjective obj = (WorldObjective)objs.get(i);
	    			if(obj.checkWorld(player.getWorld().getName())) {
	    				qm.incProgress(player, i);
	    			}
	    		}
	    	}
	    	
	    }
	}
	
}
