package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.TameObjective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;

public class TameListener implements Listener {

	private ProfileManager profMan;
	
	public TameListener(Quester plugin) {
		this.profMan = plugin.getProfileManager();
	}
		
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTame(EntityTameEvent event) {
		if(event.getOwner() instanceof Player) {
		    Player player = (Player) event.getOwner();
	    	Quest quest = profMan.getProfile(player.getName()).getQuest();
		    if(quest != null) {
		    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
		    		return;
				List<Objective> objs = quest.getObjectives();
				EntityType ent = event.getEntityType();
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("TAME")) {
		    			if(!profMan.isObjectiveActive(player, i)){
		    				continue;
		    			}
		    			TameObjective obj = (TameObjective)objs.get(i);
		    			if(obj.check(ent)) {
		    				profMan.incProgress(player, ActionSource.listenerSource(event), i);
		    				return;
		    			}
		    		}
		    	}
			}
		}
	}
}
