package me.ragan262.quester.listeners;

import java.util.List;

import me.ragan262.quester.ActionSource;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.objectives.MobKillObjective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.utils.Util;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

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
			final PlayerProfile prof = profMan.getProfile(player);
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
