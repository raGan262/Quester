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

import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.managers.DataManager;
import com.gmail.molnardad.quester.managers.QuestManager;
import com.gmail.molnardad.quester.objectives.CollectObjective;
import com.gmail.molnardad.quester.objectives.DropObjective;

public class DropListener implements Listener {

	private QuestManager qm;
	private Quester plugin;
	
	public DropListener(Quester plugin) {
		this.qm = plugin.getQuestManager();
		this.plugin = plugin;
	}
		
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		boolean collectObj = true;
		boolean dropObj = true;
	    Player player = event.getPlayer();
		Quest quest = qm.getPlayerQuest(player.getName());
	    if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		// check if Objective is type COLLECT
	    		if(DataManager.colSubOnDrop && collectObj && objs.get(i).getType().equalsIgnoreCase("COLLECT")) {
	    			if(!qm.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			CollectObjective obj = (CollectObjective)objs.get(i);
	    			ItemStack item = event.getItemDrop().getItemStack();
	    			if(item.getTypeId() == obj.getMaterial().getId()) {
	    				if(obj.getData() < 0 || obj.getData() == item.getDurability()) {
	    					qm.incProgress(player, i, -item.getAmount());
	    					collectObj = false;
	    				}
	    			}
	    		}
	    		else if(dropObj && objs.get(i).getType().equalsIgnoreCase("DROP")){
	    			if(!qm.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			DropObjective obj = (DropObjective)objs.get(i);
	    			if(obj.isMatching(event.getItemDrop().getItemStack())) {
	    				new DropTask(event.getItemDrop(), obj, player, i, qm).runTaskTimer(plugin, 20L, 10L);
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
		private final QuestManager qm;
		private final DropObjective obj;
		
		public DropTask(Item item, DropObjective obj, Player player, int id, QuestManager qm) {
			this.item = item;
			this.player = player;
			this.id = id;
			this.qm = qm;
			this.obj = obj;
		}
		
		@Override
		public void run() {
			Vector vel = item.getVelocity();
			if(!item.isValid() || !player.isValid()) { 
				cancel();
				return;
			}
			if(vel.lengthSquared() < 0.001D) {
				if(obj.isMatching(item.getLocation().getBlock().getLocation())) {
					qm.incProgress(player, id, item.getItemStack().getAmount());
				}
				cancel();
			}
		}
	}
}
