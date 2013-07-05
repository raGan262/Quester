package com.gmail.molnardad.quester.listeners;

import java.util.List;

import me.ThaH3lper.com.Api.BossDeathEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.BossObjective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;

public class BossDeathListener implements Listener {

	private ProfileManager profMan;
	
	public BossDeathListener(Quester plugin) {
		this.profMan = plugin.getProfileManager();
	}
		
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBossDeath(BossDeathEvent event) {
		Player player = event.getPlayer();
		if(player == null) {
			return;
		}
		Quest quest = profMan.getProfile(player.getName()).getQuest();
		if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
			List<Objective> objs = quest.getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		if(objs.get(i).getType().equalsIgnoreCase("BOSS")) {
	    			BossObjective obj = (BossObjective) objs.get(i);
	    			if(!profMan.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			if(obj.nameCheck(event.getBossName())) {
		    			profMan.incProgress(player, ActionSource.listenerSource(event), i);
		    			return;
	    			}
	    		}
	    	}
		}
	}
}
