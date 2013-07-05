package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.LanguageManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.objectives.DeathObjective;
import com.gmail.molnardad.quester.objectives.PlayerKillObjective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.quests.QuestFlag;
import com.gmail.molnardad.quester.utils.Util;

public class DeathListener implements Listener {

	private ProfileManager profMan = null;
	private LanguageManager langMan = null;
	
	public DeathListener(Quester plugin) {
		this.profMan = plugin.getProfileManager();
		this.langMan = plugin.getLanguageManager();
	}
		
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
	    Player player = event.getEntity();
	    if(!Util.isPlayer(player)) {
	    	return;
	    }
	    // DEATH OBJECTIVE
    	Quest quest = profMan.getProfile(player.getName()).getQuest();
	    if(quest != null) {
	    	// DEATH CHECK
	    	if(quest.hasFlag(QuestFlag.DEATHCANCEL)) {
	    		try {
					profMan.cancelQuest(player, ActionSource.listenerSource(event), langMan.getPlayerLang(player.getName()));
				} catch (QuesterException e) {
				}
	    		return;
	    	}
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		if(objs.get(i).getType().equalsIgnoreCase("DEATH")) {
	    			if(!profMan.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			DeathObjective obj = (DeathObjective)objs.get(i);
	    			if(obj.checkDeath(player.getLocation())) {
	    				profMan.incProgress(player, ActionSource.listenerSource(event), i);
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
	    if(!Util.isPlayer(player)) {
	    	return;
	    }
	    if(killer != null ) {
    		Quest quest = profMan.getProfile(killer.getName()).getQuest();
	    	if(quest != null) {
	    		// PLAYERKILL CHECK
		    	if(!quest.allowedWorld(killer.getWorld().getName().toLowerCase()))
		    		return;
	    		List<Objective> objs = quest.getObjectives();
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(!profMan.isObjectiveActive(killer, i)){
	    				continue;
	    			}
		    		if(objs.get(i).getType().equalsIgnoreCase("PLAYERKILL")) {
		    			PlayerKillObjective obj = (PlayerKillObjective)objs.get(i);
		    			if(obj.checkPlayer(player)) {
		    				profMan.incProgress(killer, ActionSource.listenerSource(event), i);
		    				return;
		    			}
		    		}
		    	}
	    	}
	    }
	}
}
