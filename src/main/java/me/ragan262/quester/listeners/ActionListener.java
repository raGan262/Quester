package me.ragan262.quester.listeners;

import java.util.List;

import me.ragan262.quester.ActionSource;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.objectives.ActionObjective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.utils.Util;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ActionListener implements Listener {
	
	private final ProfileManager profMan;
	
	public ActionListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onAction(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if(!Util.isPlayer(player)) {
			return;
		}
		final PlayerProfile prof = profMan.getProfile(player);
		final Quest quest = prof.getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			final Block block = event.getClickedBlock();
			final ItemStack item = player.getItemInHand();
			for(int i = 0; i < objs.size(); i++) {
				if(objs.get(i).getType().equalsIgnoreCase("ACTION")) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final ActionObjective obj = (ActionObjective) objs.get(i);
					if(block != null) {
						if(!obj.checkLocation(block.getLocation())) {
							continue;
						}
					}
					if(obj.checkClick(event.getAction()) && obj.checkBlock(block)
							&& obj.checkHand(item)) {
						profMan.incProgress(player, ActionSource.listenerSource(event), i);
						return;
					}
				}
			}
		}
	}
	
}
