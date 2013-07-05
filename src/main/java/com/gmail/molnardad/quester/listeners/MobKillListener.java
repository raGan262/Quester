package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.MobKillObjective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.utils.Util;

public class MobKillListener implements Listener {

	private ProfileManager profMan;
	
	public MobKillListener(Quester plugin) {
		this.profMan = plugin.getProfileManager();
	}
		
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		if(event.getEntity().getKiller() != null) {
			Player player = event.getEntity().getKiller();
			if(!Util.isPlayer(player)) {
				return;
			}
	    	Quest quest = profMan.getProfile(player.getName()).getQuest();
		    if(quest != null) {
		    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
		    		return;
		    	List<Objective> objs = quest.getObjectives();
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("MOBKILL")) {
		    			if(!profMan.isObjectiveActive(player, i)){
		    				continue;
		    			}
			    		EntityType ent = event.getEntity().getType();
		    			MobKillObjective obj = (MobKillObjective)objs.get(i);
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
