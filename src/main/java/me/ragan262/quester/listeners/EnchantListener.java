package me.ragan262.quester.listeners;

import java.util.List;
import java.util.Map;
import me.ragan262.quester.ActionSource;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.objectives.EnchantObjective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantListener implements Listener {
	
	private final ProfileManager profMan;
	
	public EnchantListener(final Quester plugin) {
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEnchant(final EnchantItemEvent event) {
		final Player player = event.getEnchanter();
		final PlayerProfile prof = profMan.getProfile(player);
		final Quest quest = prof.getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				// check if Objective is type CRAFT
				if(objs.get(i).getType().equalsIgnoreCase("ENCHANT")) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final EnchantObjective obj = (EnchantObjective)objs.get(i);
					final ItemStack item = event.getItem();
					final Map<Enchantment, Integer> enchs = event.getEnchantsToAdd();
					if(obj.check(item, enchs)) {
						profMan.incProgress(player, ActionSource.listenerSource(event), i);
						return;
					}
				}
			}
			
		}
	}
	
}
