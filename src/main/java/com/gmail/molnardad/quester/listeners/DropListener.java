package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.CollectObjective;
import com.gmail.molnardad.quester.objectives.Objective;

public class DropListener implements Listener {

	private QuestManager qm;
	
	public DropListener(Quester plugin) {
		this.qm = plugin.getQuestManager();
	}
		
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		if(Quester.data.colSubOnDrop) {
		    Player player = event.getPlayer();
	    	Quest quest = qm.getPlayerQuest(player.getName());
		    if(quest != null) {
		    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
		    		return;
		    	List<Objective> objs = quest.getObjectives();
		    	for(int i = 0; i < objs.size(); i++) {
		    		// check if Objective is type COLLECT
		    		if(objs.get(i).getType().equalsIgnoreCase("COLLECT")) {
		    			if(!qm.isObjectiveActive(player, i)){
		    				continue;
		    			}
		    			CollectObjective obj = (CollectObjective)objs.get(i);
		    			ItemStack item = event.getItemDrop().getItemStack();
		    			if(item.getTypeId() == obj.getMaterial().getId()) {
		    				if(obj.getData() < 0 || obj.getData() == item.getDurability()) {
		    					qm.incProgress(player, i, -item.getAmount());
		    					return;
		    				}
		    			}
		    		}
		    	}
		    	
		    }
		}
	}
}
