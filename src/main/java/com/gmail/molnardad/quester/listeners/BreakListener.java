package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.managers.QConfiguration;
import com.gmail.molnardad.quester.managers.QuestManager;
import com.gmail.molnardad.quester.objectives.BreakObjective;

public class BreakListener implements Listener {

	private QuestManager qm;
	
	public BreakListener(Quester plugin) {
		this.qm = plugin.getQuestManager();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
	    Player player = event.getPlayer();
    	Quest quest = qm.getPlayerQuest(player.getName());
	    if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		// check if Objective is type BREAK
	    		if(objs.get(i).getType().equalsIgnoreCase("BREAK")) {
		    		if(!qm.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			BreakObjective obj = (BreakObjective)objs.get(i);
	    			Block block = event.getBlock();
	    			// compare block ID && check for redstone
	    			boolean passed;
    				int id = block.getTypeId();
    				if(id != Material.REDSTONE_ORE.getId()) {
    					passed = id == obj.getMaterial().getId();
    				} else {
    					passed = id == Material.GLOWING_REDSTONE_ORE.getId() || id == Material.REDSTONE_ORE.getId();
    				}
	    			if(passed && obj.checkHand(player.getItemInHand().getTypeId())) {
	    				// if DATA >= 0 compare
	    				if(obj.getData() < 0 || obj.getData() == block.getData()) {
	    					if(QConfiguration.brkNoDrops) {
	    						block.setType(Material.AIR);
	    					}
	    					qm.incProgress(player, ActionSource.listenerSource(event), i);
	    					return;
	    				}
	    			}
	    		}
	    	}
	    }
	}
	
}
