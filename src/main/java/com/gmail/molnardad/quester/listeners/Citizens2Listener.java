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

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.LanguageManager;
import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.exceptions.HolderException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.holder.QuestHolder;
import com.gmail.molnardad.quester.holder.QuestHolderManager;
import com.gmail.molnardad.quester.holder.QuesterTrait;
import com.gmail.molnardad.quester.objectives.NpcKillObjective;
import com.gmail.molnardad.quester.objectives.NpcObjective;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.strings.QuesterLang;
import com.gmail.molnardad.quester.utils.Util;

public class Citizens2Listener implements Listener {
	
	private QuestManager qm = null;
	private QuestHolderManager holMan = null;
	private LanguageManager langMan = null;
	private ProfileManager profMan = null;
	
	public Citizens2Listener(Quester plugin) {
		this.qm = plugin.getQuestManager();
		this.langMan = plugin.getLanguageManager();
		this.holMan = plugin.getHolderManager();
		this.profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeftClick(NPCLeftClickEvent event) {
		if(event.getNPC().hasTrait(QuesterTrait.class)) {
			QuestHolder qh = holMan.getHolder(event.getNPC().getTrait(QuesterTrait.class).getHolderID());
			Player player = event.getClicker();
			QuesterLang lang = langMan.getPlayerLang(player.getName());
			if(!Util.permCheck(player, QConfiguration.PERM_USE_NPC, true, lang)) {
				return;
			}
			// If player has perms and holds blaze rod
			boolean isOp = Util.permCheck(player, QConfiguration.PERM_MODIFY, false, null);
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
			if(!qh.canInteract(player.getName())) {
				player.sendMessage(ChatColor.RED + lang.ERROR_HOL_INTERACT);
				return;
			}
			qh.interact(player.getName());
			
			Quest quest = qm.getQuest(holMan.getOne(qh));
			if(quest != null) {
				if(profMan.getProfile(player.getName()).hasQuest(quest)) {
					return;
				}
				else {
					try {
						qm.showQuest(player, quest.getName(), lang);
						return;
					}
					catch (QuesterException ignore) {}
				}
			}
			
			try {
				holMan.selectNext(player.getName(), qh, lang);
			} catch (HolderException e) {
				player.sendMessage(e.getMessage());
				if(!isOp) {
					return;
				}
				
			}
			
			player.sendMessage(Util.line(ChatColor.BLUE, event.getNPC().getName() + "'s quests", ChatColor.GOLD));
			if(isOp) {
				holMan.showQuestsModify(qh, player);
			} else {
				holMan.showQuestsUse(qh, player);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRightClick(NPCRightClickEvent event) {
		if(event.getNPC().hasTrait(QuesterTrait.class)) {
			QuestHolder qh = holMan.getHolder(event.getNPC().getTrait(QuesterTrait.class).getHolderID());
			Player player = event.getClicker();
			QuesterLang lang = langMan.getPlayerLang(player.getName());
			if(!Util.permCheck(player, QConfiguration.PERM_USE_NPC, true, lang)) {
				return;
			}
			boolean isOP = Util.permCheck(player, QConfiguration.PERM_MODIFY, false, null);
			// If player has perms and holds blaze rod
			if(isOP) {
				if(player.getItemInHand().getTypeId() == 369) {
					int sel = profMan.getProfile(player.getName()).getHolderID();
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
			if(!qh.canInteract(player.getName())) {
				player.sendMessage(ChatColor.RED + lang.ERROR_HOL_INTERACT);
				return;
			}
			qh.interact(player.getName());
			List<Integer> qsts = qh.getQuests();
			
			Quest currentQuest = profMan.getProfile(player.getName()).getQuest();
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
						profMan.complete(player, ActionSource.holderSource(qh), lang);
					} catch (QuesterException e) {
						try {
							profMan.showProgress(player, lang);
						} catch (QuesterException f) {
							player.sendMessage(ChatColor.DARK_PURPLE + lang.ERROR_INTERESTING);
						}
					}
					return;
				}
			}
			int selected = holMan.getOne(qh);
			if(selected < 0) {
				selected = qh.getSelectedId(player.getName());
			}
			// player doesn't have quest
			if(qm.isQuestActive(selected)) {
				try {
					profMan.startQuest(player, qm.getQuest(selected), ActionSource.holderSource(qh), lang);
				} catch (QuesterException e) {
					player.sendMessage(e.getMessage());
				}
			} else {
				player.sendMessage(ChatColor.RED + lang.ERROR_Q_NOT_SELECTED);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAnyClick(NPCRightClickEvent event) {
		Player player = event.getClicker();
    	Quest quest = profMan.getProfile(player.getName()).getQuest();
	    if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		if(objs.get(i).getType().equalsIgnoreCase("NPC")) {
		    		if(!profMan.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			NpcObjective obj = (NpcObjective)objs.get(i);
	    			if(obj.checkNpc(event.getNPC().getId())) {
	    				profMan.incProgress(player, ActionSource.listenerSource(event), i);
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
    	Quest quest = profMan.getProfile(player.getName()).getQuest();
	    if(quest != null) {
	    	if(!quest.allowedWorld(player.getWorld().getName().toLowerCase()))
	    		return;
	    	List<Objective> objs = quest.getObjectives();
	    	for(int i = 0; i < objs.size(); i++) {
	    		if(objs.get(i).getType().equalsIgnoreCase("NPCKILL")) {
		    		if(!profMan.isObjectiveActive(player, i)){
	    				continue;
	    			}
	    			NpcKillObjective obj = (NpcKillObjective)objs.get(i);
	    			if(obj.checkNpc(event.getNPC().getName())) {
	    				profMan.incProgress(player, ActionSource.listenerSource(event), i);
	    				return;
	    			}
	    		}
	    	}
	    }
	}
}
