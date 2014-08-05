package me.ragan262.quester.listeners;

import java.util.List;
import me.ragan262.quester.ActionSource;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.objectives.BreakObjective;
import me.ragan262.quester.objectives.PlaceObjective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceListener implements Listener {
	
	private final ProfileManager profMan;
	
	public PlaceListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(final BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		final PlayerProfile prof = profMan.getProfile(player);
		final Quest quest = prof.getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			final Block block = event.getBlock();
			if(QConfiguration.brkSubOnPlace) {
				for(int i = 0; i < objs.size(); i++) {
					// check if Objective is type BREAK
					if(objs.get(i).getType().equalsIgnoreCase("BREAK")) {
						if(!profMan.isObjectiveActive(prof, i)) {
							continue;
						}
						final BreakObjective obj = (BreakObjective)objs.get(i);
						// compare block ID
						if(obj.checkBlock(event.getBlock())) {
							profMan.incProgress(player, ActionSource.listenerSource(event), i, -1);
							break;
						}
					}
				}
			}
			for(int i = 0; i < objs.size(); i++) {
				// check if Objective is type PLACE
				if(objs.get(i).getType().equalsIgnoreCase("PLACE")) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final PlaceObjective obj = (PlaceObjective)objs.get(i);
					// compare block ID
					if(block.getTypeId() == obj.getMaterial().getId()) {
						// if DATA >= 0 compare
						if(obj.getData() < 0 || obj.getData() == block.getData()) {
							profMan.incProgress(player, ActionSource.listenerSource(event), i);
							return;
						}
					}
				}
			}
			
		}
	}
	
}
