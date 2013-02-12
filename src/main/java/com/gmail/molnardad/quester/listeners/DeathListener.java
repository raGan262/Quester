package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestFlag;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.managers.LanguageManager;
import com.gmail.molnardad.quester.managers.QuestManager;
import com.gmail.molnardad.quester.objectives.DeathObjective;
import com.gmail.molnardad.quester.objectives.PlayerKillObjective;

public class DeathListener implements Listener {

	private QuestManager qm = null;
	private LanguageManager langMan = null;
	
	public DeathListener(Quester plugin) {
		this.qm = plugin.getQuestManager();
		this.langMan = plugin.getLanguageManager();
	}
		
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
	    Player player = event.getEntity();
	    // DEATH OBJECTIVE
    	Quest quest = qm.getPlayerQuest(player.getName());
	    if(quest != null) {
	    	// DEATH CHECK
	    	if(quest.hasFlag(QuestFlag.DEATHCANCEL)) {
	    		try {
					qm.cancelQuest(player, false, langMan.getPlayerLang(player.getName()));
				} catch (QuesterException e) {
				}
	    		return;
	    	}
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		if(objs.get(i).getType().equalsIgnoreCase("DEATH")) {
	    			if(!qm.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			DeathObjective obj = (DeathObjective)objs.get(i);
	    			if(obj.checkDeath(player.getLocation())) {
	    				qm.incProgress(player, i);
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
	    if(player.hasMetadata("NPC")) {
	    	return;
	    }
	    if(killer != null ) {
    		Quest quest = qm.getPlayerQuest(killer.getName());
	    	if(quest != null) {
	    		// PLAYERKILL CHECK
		    	if(!quest.allowedWorld(killer.getWorld().getName().toLowerCase()))
		    		return;
	    		List<Objective> objs = quest.getObjectives();
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(!qm.isObjectiveActive(killer, i)){
	    				continue;
	    			}
		    		if(objs.get(i).getType().equalsIgnoreCase("PLAYERKILL")) {
		    			PlayerKillObjective obj = (PlayerKillObjective)objs.get(i);
		    			if(obj.checkPlayer(player)) {
		    				qm.incProgress(killer, i);
		    				return;
		    			}
		    		}
		    	}
	    	}
	    }
	}
}
