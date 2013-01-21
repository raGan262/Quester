package com.gmail.molnardad.quester.listeners;

import java.util.List;

import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.molnardad.quester.LanguageManager;
import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.DataManager;
import com.gmail.molnardad.quester.QuestHolder;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.QuesterTrait;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.exceptions.HolderException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.objectives.NpcKillObjective;
import com.gmail.molnardad.quester.objectives.NpcObjective;
import com.gmail.molnardad.quester.strings.QuesterStrings;
import com.gmail.molnardad.quester.utils.Util;

public class Citizens2Listener implements Listener {
	
	private QuestManager qm;
	private QuesterStrings lang;
	
	public Citizens2Listener() {
		this.qm = QuestManager.getInstance();
		this.lang = LanguageManager.getInstance().getDefaultLang();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeftClick(NPCLeftClickEvent event) {
		if(event.getNPC().hasTrait(QuesterTrait.class)) {
			QuestHolder qh = qm.getHolder(event.getNPC().getTrait(QuesterTrait.class).getHolderID());
			Player player = event.getClicker();
			if(!Util.permCheck(player, DataManager.PERM_USE_NPC, true)) {
				return;
			}
			// If player has perms and holds blaze rod
			boolean isOp = Util.permCheck(player, DataManager.PERM_MODIFY, false);
			if(isOp) {
				if(player.getItemInHand().getTypeId() == 369) {
					event.getNPC().getTrait(QuesterTrait.class).setHolderID(-1);
					player.sendMessage(ChatColor.GREEN + lang.HOL_UNASSIGNED);
				    return;
				}
			}
			if(qh == null) {
				player.sendMessage(ChatColor.RED + lang.ERROR_HOL_NOT_ASSIGNED);
				return;
			}
			try {
				qh.selectNext();
			} catch (HolderException e) {
				player.sendMessage(e.getMessage());
				if(!isOp) {
					return;
				}
				
			}
			
			player.sendMessage(Util.line(ChatColor.BLUE, event.getNPC().getName() + "'s quests", ChatColor.GOLD));
			if(isOp) {
				qh.showQuestsModify(player);
			} else {
				qh.showQuestsUse(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRightClick(NPCRightClickEvent event) {
		if(event.getNPC().hasTrait(QuesterTrait.class)) {
			QuestHolder qh = qm.getHolder(event.getNPC().getTrait(QuesterTrait.class).getHolderID());
			Player player = event.getClicker();
			if(!Util.permCheck(player, DataManager.PERM_USE_NPC, true)) {
				return;
			}
			boolean isOP = Util.permCheck(player, DataManager.PERM_MODIFY, false);
			// If player has perms and holds blaze rod
			if(isOP) {
				if(player.getItemInHand().getTypeId() == 369) {
					int sel = qm.getSelectedHolderID(player.getName());
					if(sel < 0){
						player.sendMessage(ChatColor.RED + lang.ERROR_HOL_NOT_SELECTED);
					} else {
						event.getNPC().getTrait(QuesterTrait.class).setHolderID(sel);
						player.sendMessage(ChatColor.GREEN + lang.HOL_ASSIGNED);
					}
				    return;
				}
			}
			if(qh == null) {
				player.sendMessage(ChatColor.RED + lang.ERROR_HOL_NOT_ASSIGNED);
				return;
			}
			int selected = qh.getSelected();
			List<Integer> qsts = qh.getQuests();
			
			Quest currentQuest = qm.getPlayerQuest(player.getName());
			if(!player.isSneaking()) {
				int questID = currentQuest == null ? -1 : currentQuest.getID();
				// player has quest and quest giver does not accept this quest
				if(questID >= 0 && !qsts.contains(questID)) {
					player.sendMessage(ChatColor.RED + lang.ERROR_Q_NOT_HERE);
					return;
				}
				// player has quest and quest giver accepts this quest
				if(questID >= 0 && qsts.contains(questID)) {
					try {
						qm.complete(player, false);
					} catch (QuesterException e) {
						try {
							qm.showProgress(player);
						} catch (QuesterException f) {
							player.sendMessage(ChatColor.DARK_PURPLE + lang.ERROR_INTERESTING);
						}
					}
					return;
				}
			}
			// player doesn't have quest
			if(qm.isQuestActive(selected)) {
				try {
					qm.startQuest(player, qm.getQuestNameByID(selected), false);
				} catch (QuesterException e) {
					player.sendMessage(e.getMessage());
				}
			} else {
				player.sendMessage(ChatColor.RED + lang.ERROR_Q_NOT_SELECTED);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAnyClick(NPCRightClickEvent event) {
		Player player = event.getClicker();
    	Quest quest = qm.getPlayerQuest(player.getName());
	    if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		if(objs.get(i).getType().equalsIgnoreCase("NPC")) {
		    		if(!qm.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			NpcObjective obj = (NpcObjective)objs.get(i);
	    			if(obj.checkNpc(event.getNPC().getId())) {
	    				qm.incProgress(player, i);
	    				if(obj.getCancel()) {
	    					event.setCancelled(true);
	    				}
	    				return;
	    			}
	    		}
	    	}
	    }
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onNpcDeath(NPCDeathEvent event) {
		Player player = event.getNPC().getBukkitEntity().getKiller();
		if(player == null) {
			return;
		}
    	Quest quest = qm.getPlayerQuest(player.getName());
	    if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		if(objs.get(i).getType().equalsIgnoreCase("NPCKILL")) {
		    		if(!qm.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			NpcKillObjective obj = (NpcKillObjective)objs.get(i);
	    			if(obj.checkNpc(event.getNPC().getName())) {
	    				qm.incProgress(player, i);
	    				return;
	    			}
	    		}
	    	}
	    }
	}
}
