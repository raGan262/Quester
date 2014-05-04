package me.ragan262.quester.listeners;

import java.util.List;

import me.ragan262.quester.ActionSource;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

public class FishListener implements Listener {
	
	private final ProfileManager profMan;
	
	public FishListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFish(final PlayerFishEvent event) {
		if(event.getState() == State.CAUGHT_FISH) {
			final Player player = event.getPlayer();
			final PlayerProfile prof = profMan.getProfile(player);
			final Quest quest = prof.getQuest();
			if(quest != null) {
				if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
					return;
				}
				final List<Objective> objs = quest.getObjectives();
				for(int i = 0; i < objs.size(); i++) {
					if(objs.get(i).getType().equalsIgnoreCase("FISH")) {
						if(!profMan.isObjectiveActive(prof, i)) {
							continue;
						}
						profMan.incProgress(player, ActionSource.listenerSource(event), i);
						return;
					}
				}
			}
		}
	}
}
