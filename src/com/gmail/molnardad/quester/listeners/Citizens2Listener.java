package com.gmail.molnardad.quester.listeners;

import java.util.List;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.QuestHolder;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.QuesterTrait;
import com.gmail.molnardad.quester.exceptions.ExceptionType;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public class Citizens2Listener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeftClick(NPCLeftClickEvent event) {
		if(event.getNPC().hasTrait(QuesterTrait.class)) {
			QuestHolder qh = event.getNPC().getTrait(QuesterTrait.class).getHolder();
			QuestManager qm = Quester.qMan;
			Player player = event.getClicker();
			if(!Util.permCheck(player, QuestData.PERM_USE_NPC, true)) {
				return;
			}
			// If player has perms and holds blaze rod
			boolean isOp = Util.permCheck(player, QuestData.MODIFY_PERM, false);
			if(isOp) {
				if(player.getItemInHand().getTypeId() == 369) {
					int sel = qm.getSelectedID(player.getName());
					if(sel < 0){
						player.sendMessage(ChatColor.RED + "No quest selected.");
					} else {
						qh.removeQuest(sel);
						player.sendMessage(ChatColor.GREEN + "Quest removed from NPC.");
					}
				    return;
				}
			}
			
			List<Integer> qsts = qh.getQuests();
			try {
				qh.selectNext();
			} catch (QuesterException e) {
				if(isOp && e.type() == ExceptionType.Q_NONE_ACTIVE) {
				} else {
					player.sendMessage(e.message());
					return;
				}
				
			}
			int selectedID = qh.getSelectedIndex();
			
			player.sendMessage(Util.line(ChatColor.BLUE, event.getNPC().getName() + "'s quests", ChatColor.GOLD));
			if(isOp) {
				for(int i=0; i<qsts.size(); i++) {
					ChatColor col = qm.isQuestActive(qsts.get(i)) ? ChatColor.BLUE : ChatColor.RED;
					player.sendMessage((i == selectedID ? ChatColor.GREEN : ChatColor.BLUE) + "[" + qsts.get(i) + "]" + col + qm.getQuestNameByID(qsts.get(i)));
				}
			} else {
				for(int i=0; i<qsts.size(); i++) {
					if(qm.isQuestActive(qsts.get(i))) {
						player.sendMessage((i == selectedID ? ChatColor.GREEN : ChatColor.BLUE) + " - " + qm.getQuestNameByID(qsts.get(i)));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRightClick(NPCRightClickEvent event) {
		if(event.getNPC().hasTrait(QuesterTrait.class)) {
			QuestHolder qh = event.getNPC().getTrait(QuesterTrait.class).getHolder();
			QuestManager qm = Quester.qMan;
			Player player = event.getClicker();
			if(!Util.permCheck(player, QuestData.PERM_USE_NPC, true)) {
				return;
			}
			boolean isOP = Util.permCheck(player, QuestData.MODIFY_PERM, false);
			// If player has perms and holds blaze rod
			if(isOP) {
				if(player.getItemInHand().getTypeId() == 369) {
					int sel = qm.getSelectedID(player.getName());
					if(sel < 0){
						player.sendMessage(ChatColor.RED + "No quest selected.");
					} else {
						qh.addQuest(sel);
						player.sendMessage(ChatColor.GREEN + "Quest added to NPC.");
					}
				    return;
				}
			}
			int selected = qh.getSelected();
			List<Integer> qsts = qh.getQuests();
			int questID = qm.getPlayerQuest(player.getName()) == null ? -1 : qm.getPlayerQuest(player.getName()).getID();
			// player has quest and quest giver does not accept this quest
			if(questID >= 0 && !qsts.contains(questID)) {
				player.sendMessage(ChatColor.RED + "You can't complete your quest here.");
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
						player.sendMessage(ChatColor.DARK_PURPLE + "Interesting error, you don't have and have quest at once !");
					}
				}
				return;
			}
			// player doesn't have quest
			if(qm.isQuestActive(selected)) {
				try {
					qm.startQuest(player, qm.getQuestNameByID(selected), false);
				} catch (QuesterException e) {
					player.sendMessage(e.message());
				}
			} else {
				player.sendMessage(ChatColor.RED + "No quest selected.");
			}
		}
	}
}
