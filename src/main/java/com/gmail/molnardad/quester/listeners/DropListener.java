package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.objectives.CollectObjective;
import com.gmail.molnardad.quester.objectives.DropObjective;
import com.gmail.molnardad.quester.profiles.PlayerProfile;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;

public class DropListener implements Listener {
	
	private final ProfileManager profMan;
	private final Quester plugin;
	
	public DropListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDrop(final PlayerDropItemEvent event) {
		boolean collectObj = true;
		boolean dropObj = true;
		final Player player = event.getPlayer();
		final PlayerProfile prof = profMan.getProfile(player.getName());
		final Quest quest = prof.getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				// check if Objective is type COLLECT
				if(QConfiguration.colSubOnDrop && collectObj
						&& objs.get(i).getType().equalsIgnoreCase("COLLECT")) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final CollectObjective obj = (CollectObjective) objs.get(i);
					final ItemStack item = event.getItemDrop().getItemStack();
					if(item.getTypeId() == obj.getMaterial().getId()) {
						if(obj.getData() < 0 || obj.getData() == item.getDurability()) {
							profMan.incProgress(player, ActionSource.listenerSource(event), i,
									-item.getAmount());
							collectObj = false;
						}
					}
				}
				else if(dropObj && objs.get(i).getType().equalsIgnoreCase("DROP")) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final DropObjective obj = (DropObjective) objs.get(i);
					if(obj.isMatching(event.getItemDrop().getItemStack())) {
						new DropTask(event.getItemDrop(), obj, player, i).runTaskTimer(plugin, 20L,
								10L);
						dropObj = false;
					}
				}
				if(!dropObj && !collectObj) {
					return;
				}
			}
			
		}
	}
	
	class DropTask extends BukkitRunnable {
		
		private final Item item;
		private final Player player;
		private final int id;
		private final DropObjective obj;
		
		public DropTask(final Item item, final DropObjective obj, final Player player, final int id) {
			this.item = item;
			this.player = player;
			this.id = id;
			this.obj = obj;
		}
		
		@Override
		public void run() {
			final Vector vel = item.getVelocity();
			if(!item.isValid() || !player.isValid()) {
				cancel();
				return;
			}
			if(vel.lengthSquared() < 0.001D) {
				if(obj.isMatching(item.getLocation().getBlock().getLocation())) {
					profMan.incProgress(player, ActionSource.otherSource(null), id, item
							.getItemStack().getAmount());
				}
				cancel();
			}
		}
	}
}
