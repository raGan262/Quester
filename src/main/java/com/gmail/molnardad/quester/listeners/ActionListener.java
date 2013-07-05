package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.ActionObjective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;

public class ActionListener implements Listener {

	private ProfileManager profMan;
	
	public ActionListener(Quester plugin) {
		this.profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onAction(PlayerInteractEvent event) {
	    Player player = event.getPlayer();
    	Quest quest = profMan.getProfile(player.getName()).getQuest();
	    if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
			Block block = event.getClickedBlock();
			ItemStack item = player.getItemInHand();
	    	for(int i = 0; i < objs.size(); i++) {
	    		if(objs.get(i).getType().equalsIgnoreCase("ACTION")) {
		    		if(!profMan.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			ActionObjective obj = (ActionObjective)objs.get(i);
	    			if(block != null) {
	    				if(!obj.checkLocation(block.getLocation())) {
	    					continue;
	    				}
	    			}
	    			if(obj.checkClick(event.getAction()) &&
	    					obj.checkBlock(block) &&
	    					obj.checkHand(item) ) {
		    			profMan.incProgress(player, ActionSource.listenerSource(event), i);
		    			return;
	    			}
	    		}
	    	}
	    }
	}
	
}
