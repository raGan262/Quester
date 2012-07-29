package com.gmail.molnardad.quester.listeners;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.MobKillObjective;
import com.gmail.molnardad.quester.objectives.Objective;

public class MobKillListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		if(event.getEntity().getKiller() != null) {
		    QuestManager qm = Quester.qMan;
			Player player = event.getEntity().getKiller();
			if(qm.hasQuest(player.getName())) {
		    	if(qm.getPlayerQuest(player.getName()).getObjectives("MOBKILL").isEmpty()) {
		    		return;
		    	}
		    	ArrayList<Objective> objs = qm.getPlayerQuest(player.getName()).getObjectives();
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("MOBKILL")) {
			    		if(qm.achievedTarget(player, i)){
		    				continue;
		    			}
			    		org.bukkit.entity.EntityType ent = event.getEntityType();
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
