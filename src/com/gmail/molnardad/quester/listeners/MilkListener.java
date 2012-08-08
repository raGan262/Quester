package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.Objective;

public class MilkListener implements Listener {

	private QuestManager qm = Quester.qMan;
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMilk(PlayerBucketFillEvent event) {
		if(event.getItemStack().getTypeId() == 335) {
			Player player = event.getPlayer();
			if(qm.hasQuest(player.getName())) {
				Quest quest = qm.getPlayerQuest(player.getName());
		    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
		    		return;
				List<Objective> objs = quest.getObjectives();
				// if quest is ordered, process current objective
				if(quest.isOrdered()) {
					int curr = qm.getCurrentObjective(player);
					Objective obj = objs.get(curr);
					if(obj != null) {
						if(obj.getType().equalsIgnoreCase("MILK")) {
							qm.incProgress(player, curr);
			    			return;
						}
					}
					return;
				}
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("MILK")) {
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
