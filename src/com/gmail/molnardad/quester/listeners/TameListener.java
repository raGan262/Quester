package com.gmail.molnardad.quester.listeners;

import java.util.ArrayList;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.objectives.TameObjective;

public class TameListener implements Listener {

	private QuestManager qm = Quester.qMan;
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTame(EntityTameEvent event) {
		if(event.getOwner() instanceof Player) {
		    Player player = (Player) event.getOwner();
			if(qm.hasQuest(player.getName())) {
				Quest quest = qm.getPlayerQuest(player.getName());
				ArrayList<Objective> objs = quest.getObjectives();
				EntityType ent = event.getEntityType();
				// if quest is ordered, process current objective
				if(quest.isOrdered()) {
					int curr = qm.getCurrentObjective(player);
					Objective obj = objs.get(curr);
					if(obj != null) {
						if(obj.getType().equalsIgnoreCase("TAME")) {
			    			TameObjective tObj = (TameObjective)obj;
			    			if(tObj.check(ent)) {
			    				qm.incProgress(player, curr);
			    				return;
			    			}
						}
					}
					return;
				}
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("TAME")) {
			    		if(qm.achievedTarget(player, i)){
		    				continue;
		    			}
		    			TameObjective obj = (TameObjective)objs.get(i);
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
