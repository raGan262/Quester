package com.gmail.molnardad.quester.listeners;

import java.util.ArrayList;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.objectives.ShearObjective;

public class ShearListener implements Listener {

	private QuestManager qm = Quester.qMan;
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShear(PlayerShearEntityEvent event) {
		if(event.getEntity().getType() == EntityType.SHEEP) {
			Player player = event.getPlayer();
			Sheep sheep = (Sheep) event.getEntity();
			if(qm.hasQuest(player.getName())) {
				Quest quest = qm.getPlayerQuest(player.getName());
				ArrayList<Objective> objs = quest.getObjectives();
				// if quest is ordered, process current objective
				if(quest.isOrdered()) {
					int curr = qm.getCurrentObjective(player);
					Objective obj = objs.get(curr);
					if(obj != null) {
						if(obj.getType().equalsIgnoreCase("SHEAR")) {
							ShearObjective sObj = (ShearObjective)obj;
			    			if(sObj.check(sheep.getColor())) {
			    				qm.incProgress(player, curr);
			    				return;
			    			}
						}
					}
					return;
				}
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("SHEAR")) {
			    		if(qm.achievedTarget(player, i)){
		    				continue;
		    			}
		    			ShearObjective obj = (ShearObjective)objs.get(i);
		    			if(obj.check(sheep.getColor())) {
		    				qm.incProgress(player, i);
		    				return;
		    			}
		    		}
		    	}
			}
		}
	}
}
