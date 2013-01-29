package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestFlag;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.managers.DataManager;
import com.gmail.molnardad.quester.managers.QuestManager;
import com.gmail.molnardad.quester.objectives.LocObjective;
import com.gmail.molnardad.quester.objectives.WorldObjective;

public class PositionListener implements Runnable {
	
	private QuestManager qm;
	private DataManager qData;
	
	public PositionListener(Quester plugin) {
		this.qm = plugin.getQuestManager();
		qData = DataManager.getInstance();
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
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("LOCATION")) {
		    			if(!qm.isObjectiveActive(player, i)){
		    				continue;
		    			}
		    			LocObjective obj = (LocObjective)objs.get(i);
		    			if(obj.checkLocation(player.getLocation())) {
		    				qm.incProgress(player, i);
		    				return;
		    			}
		    		} else if(objs.get(i).getType().equalsIgnoreCase("WORLD")) {
		    			if(!qm.isObjectiveActive(player, i)){
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
		    	for(int ID : qData.questLocations.keySet()) {
		    		Quest qst = qm.getQuest(ID);
		    		Location loc2 = qData.questLocations.get(ID);
		    		if(loc2.getWorld().getName().equals(loc.getWorld().getName())) {
			    		if(loc2.distanceSquared(loc) <= qst.getRange()*qst.getRange() && qst.hasFlag(QuestFlag.ACTIVE)) {
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

}
