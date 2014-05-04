package me.ragan262.quester.listeners;

import java.util.List;

import me.ragan262.quester.ActionSource;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.objectives.RegionObjective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestFlag;
import me.ragan262.quester.quests.QuestManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PositionListener implements Runnable {
	
	private ProfileManager profMan = null;
	private LanguageManager langMan = null;
	private QuestManager qMan = null;
	
	public PositionListener(final Quester plugin) {
		qMan = plugin.getQuestManager();
		profMan = plugin.getProfileManager();
		langMan = plugin.getLanguageManager();
	}
	
	@Override
	public void run() {
		for(final Player player : Bukkit.getServer().getOnlinePlayers()) {
			final PlayerProfile prof = profMan.getProfile(player);
			final Quest quest = prof.getQuest();
			if(quest != null) {
				// LOCATION CHECK
				if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
					continue;
				}
				final List<Objective> objs = quest.getObjectives();
				for(int i = 0; i < objs.size(); i++) {
					if(objs.get(i).getType().equalsIgnoreCase("REGION")) {
						if(!profMan.isObjectiveActive(prof, i)) {
							continue;
						}
						final RegionObjective obj = (RegionObjective) objs.get(i);
						if(obj.checkLocation(player.getLocation())) {
							profMan.incProgress(player, ActionSource.otherSource(null), i);
							break;
						}
					}
				}
				
			}
			else {
				final Location loc = player.getLocation();
				for(final int ID : qMan.questLocations.keySet()) {
					final Quest qst = qMan.getQuest(ID);
					final Location loc2 = qMan.questLocations.get(ID);
					if(loc2.getWorld().getName().equals(loc.getWorld().getName())) {
						if(loc2.distanceSquared(loc) <= qst.getRange() * qst.getRange()
								&& qst.hasFlag(QuestFlag.ACTIVE)) {
							try {
								profMan.startQuest(player, qst.getName(),
										ActionSource.otherSource(null),
										langMan.getLang(prof.getLanguage()));
							}
							catch (final QuesterException e) {}
						}
					}
				}
			}
		}
	}
	
}
