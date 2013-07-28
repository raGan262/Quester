package com.gmail.molnardad.quester.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.EnchantObjective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;

public class EnchantListener implements Listener {
	
	private final ProfileManager profMan;
	
	public EnchantListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEnchant(final EnchantItemEvent event) {
		final Player player = event.getEnchanter();
		final Quest quest = profMan.getProfile(player.getName()).getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				// check if Objective is type CRAFT
				if(objs.get(i).getType().equalsIgnoreCase("ENCHANT")) {
					if(!profMan.isObjectiveActive(player, i)) {
						continue;
					}
					final EnchantObjective obj = (EnchantObjective) objs.get(i);
					final ItemStack item = event.getItem();
					final Map<Enchantment, Integer> enchs = event.getEnchantsToAdd();
					if(obj.check(item, enchs)) {
						profMan.incProgress(player, ActionSource.listenerSource(event), i);
						return;
					}
				}
			}
			
		}
	}
	
}
