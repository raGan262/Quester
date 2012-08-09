package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestFlag;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.DeathObjective;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.objectives.PlayerKillObjective;

public class DeathListener implements Listener {

	private QuestManager qm = Quester.qMan;
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
	    Player player = event.getEntity();
	    // DEATH OBJECTIVE
	    if(qm.hasQuest(player.getName())) {
	    	// DEATH CHECK
	    	Quest quest = qm.getPlayerQuest(player.getName());
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
	    	// if quest is ordered, process current objective
	    	if(quest.hasFlag(QuestFlag.ORDERED)) {
	    		int curr = qm.getCurrentObjective(player);
	    		Objective obj = objs.get(curr);
	    		if(obj != null) {
	    			if(obj.getType().equalsIgnoreCase("DEATH")) {
	    				DeathObjective dObj = (DeathObjective)obj;
		    			if(dObj.checkDeath(player.getLocation())) {
		    				qm.incProgress(player, curr);
		    				return;
		    			}
	    			}
	    		}
	    		return;
	    	}
	    	for(int i = 0; i < objs.size(); i++) {
	    		if(objs.get(i).getType().equalsIgnoreCase("DEATH")) {
		    		// already completed objective ?
		    		if(qm.achievedTarget(player, i)){
	    				continue;
	    			}
	    			DeathObjective obj = (DeathObjective)objs.get(i);
	    			if(obj.checkDeath(player.getLocation())) {
	    				qm.incProgress(player, i);
	    				return;
	    			}
	    		}
	    	}
	    }
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onKill(PlayerDeathEvent event) {
	    // PLAYER KILL OBJECTIVE
	    Player killer = event.getEntity().getKiller();
	    Player player = event.getEntity();
	    if(killer != null ) {
	    	if(qm.hasQuest(killer.getName())) {
	    		// PLAYERKILL CHECK
	    		Quest quest = qm.getPlayerQuest(player.getName());
		    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
		    		return;
	    		List<Objective> objs = quest.getObjectives();
	    		// if quest is ordered, process current objective
	    		if(quest.hasFlag(QuestFlag.ORDERED)) {
	    			int curr = qm.getCurrentObjective(player);
	    			Objective obj = objs.get(curr);
	    			if(obj != null) {
	    				if(obj.getType().equalsIgnoreCase("PLAYERKILL")) {
	    					PlayerKillObjective kObj = (PlayerKillObjective)obj;
			    			if(kObj.checkPlayer(player)) {
			    				qm.incProgress(killer, curr);
			    				return;
			    			}
	    				}
	    			}
	    			return;
	    		}
		    	for(int i = 0; i < objs.size(); i++) {
		    		// already completed objective ?
		    		if(qm.achievedTarget(killer, i)){
	    				continue;
	    			}
		    		if(objs.get(i).getType().equalsIgnoreCase("PLAYERKILL")) {
		    			PlayerKillObjective obj = (PlayerKillObjective)objs.get(i);
		    			if(obj.checkPlayer(player)) {
		    				qm.incProgress(killer, i);
		    				return;
		    			}
		    		}
		    	}
	    	}
	    }
	}
}
