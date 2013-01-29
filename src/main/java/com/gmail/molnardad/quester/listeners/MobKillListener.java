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
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.MobKillObjective;

public class MobKillListener implements Listener {

	private QuestManager qm;
	
	public MobKillListener(Quester plugin) {
		this.qm = plugin.getQuestManager();
	}
		
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		if(event.getEntity().getKiller() != null) {
			Player player = event.getEntity().getKiller();
	    	Quest quest = qm.getPlayerQuest(player.getName());
		    if(quest != null) {
		    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
		    		return;
		    	List<Objective> objs = quest.getObjectives();
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("MOBKILL")) {
		    			if(!qm.isObjectiveActive(player, i)){
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
