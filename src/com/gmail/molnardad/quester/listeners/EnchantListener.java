package com.gmail.molnardad.quester.listeners;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.objectives.EnchantObjective;
import com.gmail.molnardad.quester.objectives.Objective;


public class EnchantListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEnchant(EnchantItemEvent event) {
		QuestManager qm = Quester.qMan;
	    Player player = event.getEnchanter();
	    if(qm.hasQuest(player.getName())) {
	    	ArrayList<Objective> objs = qm.getPlayerQuest(player.getName()).getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		// check if Objective is type CRAFT
	    		if(objs.get(i).getType().equalsIgnoreCase("ENCHANT")) {
		    		if(qm.achievedTarget(player, i)){
	    				continue;
	    			}
	    			EnchantObjective obj = (EnchantObjective)objs.get(i);
	    			ItemStack item = event.getItem();
	    			Map<Enchantment, Integer> enchs = event.getEnchantsToAdd();
	    			if(obj.check(item, enchs)) {
	    				qm.incProgress(player, i);
	    				return;
	    			}
	    		}
	    	}
	    	
	    }
	}
	
}
