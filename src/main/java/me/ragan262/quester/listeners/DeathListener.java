package me.ragan262.quester.listeners;

import java.util.List;
import me.ragan262.quester.ActionSource;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.objectives.DeathObjective;
import me.ragan262.quester.objectives.PlayerKillObjective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestFlag;
import me.ragan262.quester.utils.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
	
	private ProfileManager profMan = null;
	private LanguageManager langMan = null;
	
	public DeathListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
		langMan = plugin.getLanguageManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity();
		if(!Util.isPlayer(player)) {
			return;
		}
		// DEATH OBJECTIVE
		final PlayerProfile prof = profMan.getProfile(player);
		final Quest quest = prof.getQuest();
		if(quest != null) {
			// DEATH CHECK
			if(quest.hasFlag(QuestFlag.DEATHCANCEL)) {
				try {
					profMan.cancelQuest(player, ActionSource.listenerSource(event), langMan.getLang(prof.getLanguage()));
				}
				catch(final QuesterException e) {}
				return;
			}
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				if(objs.get(i).getType().equalsIgnoreCase("DEATH")) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final DeathObjective obj = (DeathObjective)objs.get(i);
					if(obj.checkDeath(player.getLocation())) {
						profMan.incProgress(player, ActionSource.listenerSource(event), i);
						return;
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onKill(final PlayerDeathEvent event) {
		// PLAYER KILL OBJECTIVE
		final Player killer = event.getEntity().getKiller();
		final Player victim = event.getEntity();
		if(!Util.isPlayer(victim)) {
			return;
		}
		if(killer != null) {
			if(!Util.isPlayer(killer)) {
				return;
			}
			final PlayerProfile prof = profMan.getProfile(killer);
			final Quest quest = prof.getQuest();
			if(quest != null) {
				// PLAYERKILL CHECK
				if(!quest.allowedWorld(killer.getWorld().getName().toLowerCase())) {
					return;
				}
				final List<Objective> objs = quest.getObjectives();
				for(int i = 0; i < objs.size(); i++) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					if(objs.get(i).getType().equalsIgnoreCase("PLAYERKILL")) {
						final PlayerKillObjective obj = (PlayerKillObjective)objs.get(i);
						if(obj.checkPlayer(victim)) {
							profMan.incProgress(killer, ActionSource.listenerSource(event), i);
							return;
						}
					}
				}
			}
		}
	}
}
