package me.ragan262.quester.listeners;

import java.util.List;
import me.ragan262.quester.ActionSource;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.objectives.CollectObjective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CollectListener implements Listener {
	
	private ProfileManager profMan = null;
	
	public CollectListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPickup(final PlayerPickupItemEvent event) {
		final Player player = event.getPlayer();
		final PlayerProfile prof = profMan.getProfile(player);
		final Quest quest = prof.getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				final int[] progress = prof.getProgress().getProgress();
				// check if Objective is type COLLECT
				if(objs.get(i).getType().equalsIgnoreCase("COLLECT")) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final CollectObjective obj = (CollectObjective)objs.get(i);
					final ItemStack item = event.getItem().getItemStack();
					// compare block ID
					if(item.getTypeId() == obj.getMaterial().getId()) {
						// if DATA >= 0 compare
						if(obj.getData() < 0 || obj.getData() == item.getDurability()) {
							final int rem = event.getRemaining(); // amount not picked up (full
																	// inventory)
							int req = obj.getTargetAmount() - progress[i]; // amount required by
																			// objective
							if(req < 0) { // can't be less than 0
								req = 0;
							}
							int more = item.getAmount() - req; // difference between amount picked
																// up and amount required
							if(more < 0) { // can't be less than 0
								more = 0;
							}
							profMan.incProgress(player, ActionSource.listenerSource(event), i, item.getAmount());
							if(QConfiguration.colRemPickup) {
								final Location loc = event.getItem().getLocation();
								event.getItem().remove();
								if(more + rem > 0) {
									final ItemStack newit = item.clone();
									newit.setAmount(rem + more); // spawn left on the ground +
									final Item it = event.getItem().getWorld().dropItem(loc, newit);
									it.setVelocity(new Vector(0, 0, 0));
								}
								event.setCancelled(true);
							}
							return;
						}
					}
				}
			}
			
		}
	}
	
}
