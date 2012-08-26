package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.QuestFlag;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.objectives.LocObjective;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.objectives.WorldObjective;

public class PositionListener implements Runnable {
	
	private QuestManager qm;
	
	public PositionListener(QuestManager qman) {
		qm = qman;
	}
	
	@Override
	public void run() {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
	    	Quest quest = qm.getPlayerQuest(player.getName());
		    if(quest != null) {
		    	// LOCATION CHECK
		    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
		    		return;
		    	List<Objective> objs = quest.getObjectives();
		    	// if quest is ordered, process current objective
		    	if(quest.hasFlag(QuestFlag.ORDERED)) {
		    		int curr = qm.getCurrentObjective(player);
		    		Objective obj = objs.get(curr);
		    		if(obj != null) {
		    			if(obj.getType().equalsIgnoreCase("LOCATION")) {
			    			LocObjective lObj = (LocObjective)obj;
			    			if(lObj.checkLocation(player.getLocation())) {
			    				qm.incProgress(player, curr);
			    				return;
			    			}
			    		} else if(obj.getType().equalsIgnoreCase("WORLD")) {
			    			WorldObjective wObj = (WorldObjective)obj;
			    			if(wObj.checkWorld(player.getWorld().getName())) {
			    				qm.incProgress(player, curr);
			    				return;
			    			}
			    		}
		    		}
		    		return;
		    	}
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("LOCATION")) {
			    		if(qm.achievedTarget(player, i)){
		    				continue;
		    			}
		    			LocObjective obj = (LocObjective)objs.get(i);
		    			if(obj.checkLocation(player.getLocation())) {
		    				qm.incProgress(player, i);
		    				return;
		    			}
		    		} else if(objs.get(i).getType().equalsIgnoreCase("WORLD")) {
			    		if(qm.achievedTarget(player, i)){
		    				continue;
		    			}
		    			WorldObjective obj = (WorldObjective)objs.get(i);
		    			if(obj.checkWorld(player.getWorld().getName())) {
		    				qm.incProgress(player, i);
		    				return;
		    			}
		    		}
		    	}
		    	
		    } else {
		    	Location loc = player.getLocation();
		    	for(int ID : QuestData.questLocations.keySet()) {
		    		Quest qst = qm.getQuest(ID);
		    		if(QuestData.questLocations.get(ID).distance(loc) <= qst.getRange() && qst.hasFlag(QuestFlag.ACTIVE)) {
		    			try {
							qm.startQuest(player, qst.getName(), false);
						} catch (QuesterException e) {
						}
		    		}
		    	}
		    }
		}
	}

}
