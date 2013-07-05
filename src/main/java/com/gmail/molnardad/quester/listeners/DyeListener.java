package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.DyeObjective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;

public class DyeListener implements Listener {

	private ProfileManager profMan;
	
	public DyeListener(Quester plugin) {
		this.profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityRightClick(PlayerInteractEntityEvent event) {
	    Player player = event.getPlayer();
    	Quest quest = profMan.getProfile(player.getName()).getQuest();
	    if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
			Entity entity = event.getRightClicked();
			ItemStack item = player.getItemInHand();
	    	for(int i = 0; i < objs.size(); i++) {
	    		if(objs.get(i).getType().equalsIgnoreCase("DYE")) {
		    		if(!profMan.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			DyeObjective obj = (DyeObjective)objs.get(i);
	    			if(entity.getType() == EntityType.SHEEP) {
    					Sheep sheep = (Sheep) entity;
	    				if(item.getType() == Material.INK_SACK 
	    						&& obj.checkDye(15 - item.getDurability())
	    						&& sheep.getColor().getDyeData() != (15 - item.getDurability())) {
	    					profMan.incProgress(player, ActionSource.listenerSource(event), i);
	    					return;
	    				}
	    			}
	    		}
	    	}
	    }
	}
	
}
