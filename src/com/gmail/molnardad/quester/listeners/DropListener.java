package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.CollectObjective;
import com.gmail.molnardad.quester.objectives.Objective;

public class DropListener implements Listener {

    private QuestManager qm = Quester.qMan;
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		if(QuestData.colSubOnDrop) {
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
		    			if(obj.getType().equalsIgnoreCase("COLLECT")) {
		    				CollectObjective cObj = (CollectObjective)obj;
			    			ItemStack item = event.getItemDrop().getItemStack();
			    			// compare block ID
			    			if(item.getTypeId() == cObj.getMaterial().getId()) {
			    				// if DATA >= 0 compare
			    				if(cObj.getData() < 0 || cObj.getData() == item.getDurability()) {
			    					qm.incProgress(player, curr, -item.getAmount());
			    					return;
			    				}
			    			}
		    			}
		    		}
		    		return;
		    	}
		    	for(int i = 0; i < objs.size(); i++) {
		    		// check if Objective is type COLLECT
		    		if(objs.get(i).getType().equalsIgnoreCase("COLLECT")) {
			    		if(qm.achievedTarget(player, i)){
		    				continue;
		    			}
		    			CollectObjective obj = (CollectObjective)objs.get(i);
		    			ItemStack item = event.getItemDrop().getItemStack();
		    			// compare block ID
		    			if(item.getTypeId() == obj.getMaterial().getId()) {
		    				// if DATA >= 0 compare
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
