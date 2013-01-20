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

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.objectives.DyeObjective;
import com.gmail.molnardad.quester.objectives.Objective;

public class DyeListener implements Listener {

	private QuestManager qm;
	
	public DyeListener() {
		this.qm = QuestManager.getInstance();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityRightClick(PlayerInteractEntityEvent event) {
	    Player player = event.getPlayer();
    	Quest quest = qm.getPlayerQuest(player.getName());
	    if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
			Entity entity = event.getRightClicked();
			ItemStack item = player.getItemInHand();
	    	for(int i = 0; i < objs.size(); i++) {
	    		if(objs.get(i).getType().equalsIgnoreCase("DYE")) {
		    		if(!qm.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			DyeObjective obj = (DyeObjective)objs.get(i);
	    			if(entity.getType() == EntityType.SHEEP) {
    					Sheep sheep = (Sheep) entity;
	    				if(item.getType() == Material.INK_SACK 
	    						&& obj.checkDye(15 - item.getDurability())
	    						&& sheep.getColor().getDyeData() != (15 - item.getDurability())) {
	    					qm.incProgress(player, i);
	    					return;
	    				}
	    			}
	    		}
	    	}
	    }
	}
	
}
