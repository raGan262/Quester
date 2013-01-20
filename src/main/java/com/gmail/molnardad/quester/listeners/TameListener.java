package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.objectives.TameObjective;

public class TameListener implements Listener {

	private QuestManager qm;
	
	public TameListener() {
		this.qm = QuestManager.getInstance();
	}
		
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTame(EntityTameEvent event) {
		if(event.getOwner() instanceof Player) {
		    Player player = (Player) event.getOwner();
	    	Quest quest = qm.getPlayerQuest(player.getName());
		    if(quest != null) {
		    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
		    		return;
				List<Objective> objs = quest.getObjectives();
				EntityType ent = event.getEntityType();
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("TAME")) {
		    			if(!qm.isObjectiveActive(player, i)){
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
