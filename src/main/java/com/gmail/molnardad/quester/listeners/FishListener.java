package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.managers.QuestManager;

public class FishListener implements Listener {

	/**
	 * @uml.property  name="qm"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private QuestManager qm;
	
	public FishListener(Quester plugin) {
		this.qm = plugin.getQuestManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFish(PlayerFishEvent event) {
		if(event.getState() == State.CAUGHT_FISH) {
			Player player = event.getPlayer();
	    	Quest quest = qm.getPlayerQuest(player.getName());
		    if(quest != null) {
		    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
		    		return;
				List<Objective> objs = quest.getObjectives();
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("FISH")) {
		    			if(!qm.isObjectiveActive(player, i)){
		    				continue;
		    			}
		    			qm.incProgress(player, i);
		    			return;
		    		}
		    	}
			}
		}
	}
}
