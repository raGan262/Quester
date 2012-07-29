package com.gmail.molnardad.quester.listeners;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.DeathObjective;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.objectives.PlayerKillObjective;

public class DeathListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		QuestManager qm = Quester.qMan;
	    Player player = event.getEntity();
	    // DEATH OBJECTIVE
	    if(qm.hasQuest(player.getName())) {
	    	// DEATH CHECK
	    	if(!qm.getPlayerQuest(player.getName()).getObjectives("DEATH").isEmpty()) {
		    	ArrayList<Objective> objs = qm.getPlayerQuest(player.getName()).getObjectives();
		    	for(int i = 0; i < objs.size(); i++) {
		    		// already completed objective ?
		    		if(qm.achievedTarget(player, i)){
	    				continue;
	    			}
		    		if(objs.get(i).getType().equalsIgnoreCase("DEATH")) {
		    			DeathObjective obj = (DeathObjective)objs.get(i);
		    			if(obj.checkDeath(player.getLocation())) {
		    				qm.incProgress(player, i);
		    				break;
		    			}
		    		}
		    	}
	    	}
	    	
	    }
	    // PLAYER KILL OBJECTIVE
	    Player killer = event.getEntity().getKiller();
	    if(killer != null ) {
	    	if(qm.hasQuest(killer.getName())) {
	    		// PLAYERKILL CHECK
	    		if(!qm.getPlayerQuest(killer.getName()).getObjectives("PLAYERKILL").isEmpty()) {
		    		ArrayList<Objective> objs = qm.getPlayerQuest(killer.getName()).getObjectives();
			    	for(int i = 0; i < objs.size(); i++) {
			    		// already completed objective ?
			    		if(qm.achievedTarget(killer, i)){
		    				continue;
		    			}
			    		if(objs.get(i).getType().equalsIgnoreCase("PLAYERKILL")) {
			    			PlayerKillObjective obj = (PlayerKillObjective)objs.get(i);
			    			if(obj.checkPlayer(player)) {
			    				qm.incProgress(killer, i);
			    				break;
			    			}
			    		}
			    	}
	    		}
	    	}
	    }
	}
}
