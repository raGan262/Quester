package com.gmail.molnardad.quester.listeners;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.CollectObjective;
import com.gmail.molnardad.quester.objectives.Objective;

public class DropListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		if(QuestData.colSubOnDrop) {
		    QuestManager qm = Quester.qMan;
		    Player player = event.getPlayer();
		    if(qm.hasQuest(player.getName())) {
		    	ArrayList<Objective> objs = qm.getPlayerQuest(player.getName()).getObjectives();
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
