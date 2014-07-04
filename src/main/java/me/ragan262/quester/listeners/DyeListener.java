package me.ragan262.quester.listeners;

import java.util.List;

import me.ragan262.quester.ActionSource;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.objectives.DyeObjective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class DyeListener implements Listener {
	
	private final ProfileManager profMan;
	
	public DyeListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityRightClick(final PlayerInteractEntityEvent event) {
		final Player player = event.getPlayer();
		final PlayerProfile prof = profMan.getProfile(player);
		final Quest quest = prof.getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			final Entity entity = event.getRightClicked();
			final ItemStack item = player.getItemInHand();
			for(int i = 0; i < objs.size(); i++) {
				if(objs.get(i).getType().equalsIgnoreCase("DYE")) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final DyeObjective obj = (DyeObjective) objs.get(i);
					if(entity.getType() == EntityType.SHEEP) {
						final Sheep sheep = (Sheep) entity;
						if(item.getType() == Material.INK_SACK
								&& obj.checkDye(15 - item.getDurability())
								&& sheep.getColor().getDyeData() != 15 - item.getDurability()) {
							profMan.incProgress(player, ActionSource.listenerSource(event), i);
							return;
						}
					}
				}
			}
		}
	}
	
}
