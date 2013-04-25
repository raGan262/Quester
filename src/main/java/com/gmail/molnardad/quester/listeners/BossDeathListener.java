package com.gmail.molnardad.quester.listeners;

import java.util.List;

import me.ThaH3lper.com.Api.BossDeathEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.managers.QuestManager;
import com.gmail.molnardad.quester.objectives.BossObjective;

public class BossDeathListener implements Listener {

	/**
	 * @uml.property  name="qm"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private QuestManager qm;
	
	public BossDeathListener(Quester plugin) {
		this.qm = plugin.getQuestManager();
	}
		
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFish(BossDeathEvent event) {
		Player player = event.getPlayer();
		if(player == null) {
			return;
		}
		Quest quest = qm.getPlayerQuest(player.getName());
		if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
			List<Objective> objs = quest.getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		if(objs.get(i).getType().equalsIgnoreCase("BOSS")) {
	    			BossObjective obj = (BossObjective) objs.get(i);
	    			if(!qm.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			if(obj.nameCheck(event.getBossName())) {
		    			qm.incProgress(player, i);
		    			return;
	    			}
	    		}
	    	}
		}
	}
}
