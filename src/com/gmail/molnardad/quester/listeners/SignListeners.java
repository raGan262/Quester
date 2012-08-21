package com.gmail.molnardad.quester.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.QuestHolder;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.QuesterSign;
import com.gmail.molnardad.quester.exceptions.ExceptionType;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public class SignListeners implements Listener {

	QuestManager qm = Quester.qMan;
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			QuesterSign qs = QuestData.signs.get(block.getLocation().getWorld().getName() + block.getLocation().getBlockX() + block.getLocation().getBlockY() + block.getLocation().getBlockZ());
			if(qs == null) {
				return;
			}
			if(block.getType().getId() != 63 && block.getType().getId() != 68) {
				QuestData.signs.remove(block.getLocation().getWorld().getName() + block.getLocation().getBlockX() + block.getLocation().getBlockY() + block.getLocation().getBlockZ());
				player.sendMessage(Quester.LABEL + "Sign unregistered.");
				return;
			} else { 
				Sign sign = (Sign) block.getState();
				if(!sign.getLine(0).equals(ChatColor.BLUE + "[Quester]")) {
					block.breakNaturally();
					QuestData.signs.remove(block.getLocation().getWorld().getName() + block.getLocation().getBlockX() + block.getLocation().getBlockY() + block.getLocation().getBlockZ());
					player.sendMessage(Quester.LABEL + "Sign unregistered.");
					return;
				}
			}
			
			if(!Util.permCheck(player, QuestData.PERM_USE_NPC, true)) {
				return;
			}
			if(player.isSneaking()) {
				return;
			}
			boolean isOp = Util.permCheck(player, QuestData.MODIFY_PERM, false);
			QuestHolder qh = qs.getHolder();

			event.setCancelled(true);
			if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				
				if(isOp) {
					if(player.getItemInHand().getTypeId() == 369) {
						int sel = qm.getSelectedID(player.getName());
						if(sel < 0){
							player.sendMessage(ChatColor.RED + "No quest selected.");
						} else {
							qh.removeQuest(sel);
							player.sendMessage(ChatColor.GREEN + "Quest removed from sign.");
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
				
				player.sendMessage(Util.line(ChatColor.BLUE, "Sign quests", ChatColor.GOLD));
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
				
			} else {
				
				if(isOp) {
					if(player.getItemInHand().getTypeId() == 369) {
						int sel = qm.getSelectedID(player.getName());
						if(sel < 0){
							player.sendMessage(ChatColor.RED + "No quest selected.");
						} else {
							qh.addQuest(sel);
							player.sendMessage(ChatColor.GREEN + "Quest added to sign.");
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
						qm.startQuest(player, qm.getQuestNameByID(selected));
					} catch (QuesterException e) {
						player.sendMessage(e.message());
					}
				} else {
					player.sendMessage(ChatColor.RED + "No quest selected.");
				}
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if(block.getType().getId() == 63 || block.getType().getId() == 68) {
			Sign sign = (Sign) block.getState();
			if(QuestData.signs.get(sign.getLocation().getWorld().getName() + sign.getLocation().getBlockX() + sign.getLocation().getBlockY() + sign.getLocation().getBlockZ()) != null) {
				if(!event.getPlayer().isSneaking()) {
					event.setCancelled(true);
					return;
				}
				QuestData.signs.remove(sign.getLocation().getWorld().getName() + sign.getLocation().getBlockX() + sign.getLocation().getBlockY() + sign.getLocation().getBlockZ());
				event.getPlayer().sendMessage(Quester.LABEL + "Sign unregistered.");;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		Block block = event.getBlock();
		if(event.getLine(0).equals("[Quester]")) {
			event.setLine(0, ChatColor.BLUE + "[Quester]");
			QuesterSign sign = new QuesterSign(block.getLocation(), new QuestHolder());
			QuestData.signs.put(sign.getLocation().getWorld().getName() + sign.getLocation().getBlockX() + sign.getLocation().getBlockY() + sign.getLocation().getBlockZ(), sign);
			event.getPlayer().sendMessage(Quester.LABEL + "Sign registered.");;
		}
	}
}
