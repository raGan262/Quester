package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.LanguageManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.objectives.LocObjective;
import com.gmail.molnardad.quester.objectives.WorldObjective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.quests.QuestFlag;
import com.gmail.molnardad.quester.quests.QuestManager;

public class PositionListener implements Runnable {
	
	private ProfileManager profMan = null;
	private LanguageManager langMan = null;
	private QuestManager qMan = null;
	
	public PositionListener(Quester plugin) {
		this.qMan = plugin.getQuestManager();
		this.profMan = plugin.getProfileManager();
		this.langMan = plugin.getLanguageManager();
	}
	
	@Override
	public void run() {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
	    	Quest quest = profMan.getProfile(player.getName()).getQuest();
		    if(quest != null) {
		    	// LOCATION CHECK
		    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
		    		continue;
		    	List<Objective> objs = quest.getObjectives();
		    	for(int i = 0; i < objs.size(); i++) {
		    		if(objs.get(i).getType().equalsIgnoreCase("LOCATION")) {
		    			if(!profMan.isObjectiveActive(player, i)){
		    				continue;
		    			}
		    			LocObjective obj = (LocObjective)objs.get(i);
		    			if(obj.checkLocation(player.getLocation())) {
		    				profMan.incProgress(player, ActionSource.otherSource(null), i);
		    				break;
		    			}
		    		} else if(objs.get(i).getType().equalsIgnoreCase("WORLD")) {
		    			if(!profMan.isObjectiveActive(player, i)){
		    				continue;
		    			}
		    			WorldObjective obj = (WorldObjective)objs.get(i);
		    			if(obj.checkWorld(player.getWorld().getName())) {
		    				profMan.incProgress(player, ActionSource.otherSource(null), i);
		    				break;
		    			}
		    		}
		    	}
		    	
		    } else {
		    	Location loc = player.getLocation();
		    	for(int ID : qMan.questLocations.keySet()) {
		    		Quest qst = qMan.getQuest(ID);
		    		Location loc2 = qMan.questLocations.get(ID);
		    		if(loc2.getWorld().getName().equals(loc.getWorld().getName())) {
			    		if(loc2.distanceSquared(loc) <= qst.getRange()*qst.getRange() && qst.hasFlag(QuestFlag.ACTIVE)) {
			    			try {
								profMan.startQuest(player, qst.getName(), ActionSource.otherSource(null), langMan.getPlayerLang(player.getName()));
							} catch (QuesterException e) {
							}
			    		}
		    		}
		    	}
		    }
		}
	}

}
