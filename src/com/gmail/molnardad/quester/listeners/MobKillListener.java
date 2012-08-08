package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.MobKillObjective;
import com.gmail.molnardad.quester.objectives.Objective;

public class MobKillListener implements Listener {

	private QuestManager qm = Quester.qMan;
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		if(event.getEntity().getKiller() != null) {
			Player player = event.getEntity().getKiller();
			if(qm.hasQuest(player.getName())) {
		    	Quest quest = qm.getPlayerQuest(player.getName());
		    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
		    		return;
		    	List<Objective> objs = quest.getObjectives();
		    	// if quest is ordered, process current objective
		    	if(quest.isOrdered()) {
		    		int curr = qm.getCurrentObjective(player);
		    		Objective obj = objs.get(curr);
		    		if(obj != null) {
		    			if(obj.getType().equalsIgnoreCase("MOBKILL")) {
		    				EntityType ent = event.getEntity().getType();
			    			MobKillObjective mkObj = (MobKillObjective)obj;
			    			if(mkObj.check(ent)) {
			    				qm.incProgress(player, curr);
			    				return;
			    			}
		    			}
		    		}
		    		return;
		    	}
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("MOBKILL")) {
			    		if(qm.achievedTarget(player, i)){
		    				continue;
		    			}
			    		EntityType ent = event.getEntity().getType();
		    			MobKillObjective obj = (MobKillObjective)objs.get(i);
		    			if(obj.check(ent)) {
		    				qm.incProgress(player, i);
		    				return;
		    			}
		    		}
		    	}
			}
		}
	}
}
