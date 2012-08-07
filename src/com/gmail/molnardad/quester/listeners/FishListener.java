package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.Objective;

public class FishListener implements Listener {

	private QuestManager qm = Quester.qMan;
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFish(PlayerFishEvent event) {
		if(event.getState() == State.CAUGHT_FISH) {
			Player player = event.getPlayer();
			if(qm.hasQuest(player.getName())) {
				Quest quest = qm.getPlayerQuest(player.getName());
				List<Objective> objs = quest.getObjectives();
				// if quest is ordered, process current objective
				if(quest.isOrdered()) {
					int curr = qm.getCurrentObjective(player);
					Objective obj = objs.get(curr);
					if(obj != null) {
						if(obj.getType().equalsIgnoreCase("FISH")) {
							qm.incProgress(player, curr);
			    			return;
						}
					}
					return;
				}
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
