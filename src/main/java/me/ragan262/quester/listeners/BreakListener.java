package me.ragan262.quester.listeners;

import java.util.List;
import me.ragan262.quester.ActionSource;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.objectives.BreakObjective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakListener implements Listener {
	
	private final ProfileManager profMan;
	
	public BreakListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBreak(final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		final PlayerProfile prof = profMan.getProfile(player);
		final Quest quest = prof.getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				// check if Objective is type BREAK
				if(objs.get(i).getType().equalsIgnoreCase("BREAK")) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final BreakObjective obj = (BreakObjective)objs.get(i);
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
