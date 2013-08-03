package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.MobKillObjective;
import com.gmail.molnardad.quester.profiles.PlayerProfile;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.utils.Util;

public class MobKillListener implements Listener {
	
	private final ProfileManager profMan;
	
	public MobKillListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(final EntityDeathEvent event) {
		if(event.getEntity().getKiller() != null) {
			final Player player = event.getEntity().getKiller();
			if(!Util.isPlayer(player)) {
				return;
			}
			final PlayerProfile prof = profMan.getProfile(player.getName());
			final Quest quest = prof.getQuest();
			if(quest != null) {
				if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
					return;
				}
				final List<Objective> objs = quest.getObjectives();
				for(int i = 0; i < objs.size(); i++) {
					if(objs.get(i).getType().equalsIgnoreCase("MOBKILL")) {
						if(!profMan.isObjectiveActive(prof, i)) {
							continue;
						}
						final EntityType ent = event.getEntity().getType();
						final MobKillObjective obj = (MobKillObjective) objs.get(i);
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
