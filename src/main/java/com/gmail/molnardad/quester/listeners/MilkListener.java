package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;

public class MilkListener implements Listener {

	private ProfileManager profMan;
	
	public MilkListener(Quester plugin) {
		this.profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMilk(PlayerBucketFillEvent event) {
		if(event.getItemStack().getTypeId() == 335) {
			Player player = event.getPlayer();
	    	Quest quest = profMan.getProfile(player.getName()).getQuest();
		    if(quest != null) {
		    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
		    		return;
				List<Objective> objs = quest.getObjectives();
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("MILK")) {
		    			if(!profMan.isObjectiveActive(player, i)){
		    				continue;
		    			}
		    			profMan.incProgress(player, ActionSource.listenerSource(event), i);
		    			return;
		    		}
		    	}
			}
		}
	}
}
