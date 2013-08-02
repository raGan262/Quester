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
import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.BreakObjective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;

public class BreakListener implements Listener {
	
	private final ProfileManager profMan;
	
	public BreakListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBreak(final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		final Quest quest = profMan.getProfile(player.getName()).getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				// check if Objective is type BREAK
				if(objs.get(i).getType().equalsIgnoreCase("BREAK")) {
					if(!profMan.isObjectiveActive(player, i)) {
						continue;
					}
					final BreakObjective obj = (BreakObjective) objs.get(i);
					final Block block = event.getBlock();
					if(obj.checkBlock(block) && obj.checkHand(player.getItemInHand().getTypeId())) {
						if(QConfiguration.brkNoDrops) {
							block.setType(Material.AIR);
						}
						profMan.incProgress(player, ActionSource.listenerSource(event), i);
						return;
					}
				}
			}
		}
	}
	
}
