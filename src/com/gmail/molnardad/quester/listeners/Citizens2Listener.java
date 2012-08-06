package com.gmail.molnardad.quester.listeners;

import java.util.ArrayList;

import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.QuesterTrait;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public class Citizens2Listener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeftClick(NPCLeftClickEvent event) {
		if(event.getNPC().hasTrait(QuesterTrait.class)) {
			event.getNPC().getTrait(QuesterTrait.class).check();
			QuestManager qm = Quester.qMan;
			Player player = event.getClicker();
			// If player has perms and holds blaze rod
			boolean isOp = Util.permCheck(player, QuestData.MODIFY_PERM, false);
			if(isOp) {
				if(player.getItemInHand().getTypeId() == 369) {
					String sel = qm.getSelectedName(player.getName());
					if(sel == null || sel.equals("")){
						player.sendMessage(ChatColor.RED + "No quest selected.");
					} else {
						event.getNPC().getTrait(QuesterTrait.class).removeQuest(sel);
						player.sendMessage(ChatColor.GREEN + "Quest removed from NPC.");
					}
				    return;
				}
			}
			ArrayList<String> qsts = event.getNPC().getTrait(QuesterTrait.class).getQuests();
			int neww = -1;
			if(hasActive(qsts)) {
				int curr = event.getNPC().getTrait(QuesterTrait.class).getSelected();
				int i = curr;
				while(neww < 0) {
					if(i < qsts.size()-1)
						i++;
					else
						i = 0;
					if(qm.isQuestActive(qsts.get(i))) {
						neww = i;
					}
				}
				event.getNPC().getTrait(QuesterTrait.class).setSelected(neww);
			} else {
				if(!isOp){
					player.sendMessage(Quester.LABEL + event.getNPC().getName() +" doesn't have active quests.");
					return;
				}
			}
			player.sendMessage(Util.line(ChatColor.BLUE, event.getNPC().getName() + "'s quests", ChatColor.GOLD));
			if(isOp) {
				for(int i=0; i<qsts.size(); i++) {
					ChatColor col = qm.isQuestActive(qsts.get(i)) ? ChatColor.BLUE : ChatColor.RED;
					player.sendMessage((i == neww ? ChatColor.GREEN : ChatColor.BLUE) + "[" + i + "]" + col + qsts.get(i));
				}
			} else {
				for(int i=0; i<qsts.size(); i++) {
					if(qm.isQuestActive(qsts.get(i))) {
						player.sendMessage((i == neww ? ChatColor.GREEN : ChatColor.BLUE) + " - " + qsts.get(i));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRightClick(NPCRightClickEvent event) {
		if(event.getNPC().hasTrait(QuesterTrait.class)) {
			event.getNPC().getTrait(QuesterTrait.class).check();
			QuestManager qm = Quester.qMan;
			Player player = event.getClicker();
			boolean isOP = Util.permCheck(player, QuestData.MODIFY_PERM, false);
			if(!Util.permCheck(player, QuestData.USE_PERM, true)) {
				return;
			}
			// If player has perms and holds blaze rod
			if(isOP) {
				if(player.getItemInHand().getTypeId() == 369) {
					String sel = qm.getSelectedName(player.getName());
					if(sel == null || sel.equals("")){
						player.sendMessage(ChatColor.RED + "No quest selected.");
					} else {
						event.getNPC().getTrait(QuesterTrait.class).addQuest(sel);
						player.sendMessage(ChatColor.GREEN + "Quest added to NPC.");
					}
				    return;
				}
			}
			String selected = event.getNPC().getTrait(QuesterTrait.class).getSelectedName();
			ArrayList<String> qsts = event.getNPC().getTrait(QuesterTrait.class).getQuests();
			String quest = qm.getPlayerQuest(player.getName()) == null ? "" : qm.getPlayerQuest(player.getName()).getName();
			// player has quest and quest giver does not accept this quest
			if(quest != "" && !qsts.contains(quest.toLowerCase())) {
				player.sendMessage(ChatColor.RED + "You can't complete your quest here.");
				return;
			}
			// player has quest and quest giver accepts this quest
			if(quest != "" && qsts.contains(quest.toLowerCase())) {
				try {
					qm.complete(player);
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
					qm.startQuest(player, selected);
				} catch (QuesterException e) {
					player.sendMessage(e.message());
				}
			} else {
				player.sendMessage(ChatColor.RED + "No quest selected.");
			}
		}
	}
	
	private boolean hasActive(ArrayList<String> qsts) {
		for(String q : qsts) {
			if(Quester.qMan.isQuestActive(q)) {
				return true;
			}
		}
		return false;
	}
}
