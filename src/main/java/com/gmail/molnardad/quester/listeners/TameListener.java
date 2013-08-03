package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.TameObjective;
import com.gmail.molnardad.quester.profiles.PlayerProfile;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;

public class TameListener implements Listener {
	
	private final ProfileManager profMan;
	
	public TameListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTame(final EntityTameEvent event) {
		if(event.getOwner() instanceof Player) {
			final Player player = (Player) event.getOwner();
			final PlayerProfile prof = profMan.getProfile(player.getName());
			final Quest quest = prof.getQuest();
			if(quest != null) {
				if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
					return;
				}
				final List<Objective> objs = quest.getObjectives();
				final EntityType ent = event.getEntityType();
				for(int i = 0; i < objs.size(); i++) {
					if(objs.get(i).getType().equalsIgnoreCase("TAME")) {
						if(!profMan.isObjectiveActive(prof, i)) {
							continue;
						}
						final TameObjective obj = (TameObjective) objs.get(i);
						if(obj.check(ent)) {
							profMan.incProgress(player, ActionSource.listenerSource(event), i);
							return;
						}
					}
				}
			}
		}
	}
}
