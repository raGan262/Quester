package me.ragan262.quester.listeners;

import java.util.List;

import me.ragan262.quester.ActionSource;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.objectives.ChatObjective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatListener implements Listener {
	
	private final ProfileManager profMan;
	private final Quester plugin;
	
	public ChatListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(final AsyncPlayerChatEvent event) {
		Bukkit.getScheduler().runTask(plugin, new ChatTask(event));
	}
	
	class ChatTask extends BukkitRunnable {
		
		final AsyncPlayerChatEvent event;
		
		public ChatTask(final AsyncPlayerChatEvent event) {
			this.event = event;
		}
		
		@Override
		public void run() {
			final Player player = event.getPlayer();
			final PlayerProfile prof = profMan.getProfile(player.getName());
			final Quest quest = prof.getQuest();
			if(quest != null) {
				if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
					return;
				}
				final List<Objective> objs = quest.getObjectives();
				for(int i = 0; i < objs.size(); i++) {
					if(objs.get(i).getType().equalsIgnoreCase("CHAT")) {
						if(!profMan.isObjectiveActive(prof, i)) {
							continue;
						}
						if(((ChatObjective) objs.get(i)).matches(event.getMessage())) {
							profMan.incProgress(player, ActionSource.listenerSource(event), i);
							return;
						}
					}
				}
			}
		}
		
	}
}
