package com.gmail.molnardad.quester.listeners;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.Objective;

public class FishListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFish(PlayerFishEvent event) {
		if(event.getState() == State.CAUGHT_FISH) {
		    QuestManager qm = Quester.qMan;
			Player player = event.getPlayer();
			if(qm.hasQuest(player.getName())) {
		    	ArrayList<Objective> objs = qm.getPlayerQuest(player.getName()).getObjectives();
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("FISH")) {
			    		if(qm.achievedTarget(player, i)){
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
