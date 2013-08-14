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

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.HolderException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.holder.QuestHolder;
import com.gmail.molnardad.quester.holder.QuestHolderManager;
import com.gmail.molnardad.quester.holder.QuesterSign;
import com.gmail.molnardad.quester.lang.LanguageManager;
import com.gmail.molnardad.quester.lang.QuesterLang;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.utils.Util;

public class SignListeners implements Listener {
	
	private QuestManager qm = null;
	private QuestHolderManager holMan = null;
	private LanguageManager langMan = null;
	private ProfileManager profMan = null;
	
	public SignListeners(final Quester plugin) {
		qm = plugin.getQuestManager();
		langMan = plugin.getLanguageManager();
		holMan = plugin.getHolderManager();
		profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInteract(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if(event.getAction() == Action.LEFT_CLICK_BLOCK
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			final Block block = event.getClickedBlock();
			final QuesterSign qs = holMan.getSign(block.getLocation());
			if(qs == null) {
				return;
			}
			final QuesterLang lang = langMan.getPlayerLang(player.getName());
			if(block.getType().getId() != 63 && block.getType().getId() != 68) {
				holMan.removeSign(block.getLocation());
				player.sendMessage(Quester.LABEL + lang.get("SIGN_UNREGISTERED"));
				return;
			}
			else {
				final Sign sign = (Sign) block.getState();
				if(!sign.getLine(0).equals(ChatColor.BLUE + "[Quester]")) {
					block.breakNaturally();
					holMan.removeSign(block.getLocation());
					player.sendMessage(Quester.LABEL + lang.get("SIGN_UNREGISTERED"));
					return;
				}
			}
			
			if(!Util.permCheck(player, QConfiguration.PERM_USE_SIGN, true, lang)) {
				return;
			}
			if(player.isSneaking()) {
				return;
			}
			final boolean isOp = Util.permCheck(player, QConfiguration.PERM_MODIFY, false, null);
			
			event.setCancelled(true);
			final QuestHolder qh = holMan.getHolder(qs.getHolderID());
			if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				
				if(isOp) {
					if(player.getItemInHand().getTypeId() == 369) {
						qs.setHolderID(-1);
						player.sendMessage(ChatColor.GREEN + lang.get("HOL_UNASSIGNED"));
						return;
					}
				}
				
				if(qh == null) {
					player.sendMessage(ChatColor.RED + lang.get("ERROR_HOL_NOT_ASSIGNED"));
					return;
				}
				
				if(!qh.canInteract(player.getName())) {
					player.sendMessage(ChatColor.RED + lang.get("ERROR_HOL_INTERACT"));
				}
				qh.interact(player.getName());
				
				final Quest quest = qm.getQuest(holMan.getOne(qh));
				if(quest != null) {
					if(profMan.getProfile(player.getName()).hasQuest(quest)) {
						return;
					}
					else {
						try {
							qm.showQuest(player, quest.getName(), lang);
							return;
						}
						catch (final QuesterException ignore) {}
					}
				}
				
				try {
					holMan.selectNext(player.getName(), qh, lang);
				}
				catch (final HolderException e) {
					player.sendMessage(e.getMessage());
					if(!isOp) {
						return;
					}
					
				}
				
				player.sendMessage(Util.line(ChatColor.BLUE, lang.get("SIGN_HEADER"),
						ChatColor.GOLD));
				if(isOp) {
					holMan.showQuestsModify(qh, player);
				}
				else {
					holMan.showQuestsUse(qh, player);
				}
				
			}
			else {
				
				if(isOp) {
					if(player.getItemInHand().getTypeId() == 369) {
						final int sel = profMan.getProfile(player.getName()).getHolderID();
						if(sel < 0) {
							player.sendMessage(ChatColor.RED + lang.get("ERROR_HOL_NOT_ASSIGNED"));
						}
						else {
							qs.setHolderID(sel);
							player.sendMessage(ChatColor.GREEN + lang.get("HOL_ASSIGNED"));
						}
						return;
					}
				}
				if(qh == null) {
					player.sendMessage(ChatColor.RED + lang.get("ERROR_HOL_NOT_ASSIGNED"));
					return;
				}
				if(!qh.canInteract(player.getName())) {
					player.sendMessage(ChatColor.RED + lang.get("ERROR_HOL_INTERACT"));
					return;
				}
				qh.interact(player.getName());
				final List<Integer> qsts = qh.getQuests();
				
				final Quest currentQuest = profMan.getProfile(player.getName()).getQuest();
				if(!player.isSneaking()) {
					final int questID = currentQuest == null ? -1 : currentQuest.getID();
					// player has quest and quest giver does not accept this quest
					if(questID >= 0 && !qsts.contains(questID)) {
						player.sendMessage(ChatColor.RED + lang.get("ERROR_Q_NOT_HERE"));
						return;
					}
					// player has quest and quest giver accepts this quest
					if(questID >= 0 && qsts.contains(questID)) {
						try {
							profMan.complete(player, ActionSource.holderSource(qh), lang);
						}
						catch (final QuesterException e) {
							try {
								profMan.showProgress(player, lang);
							}
							catch (final QuesterException f) {
								player.sendMessage(ChatColor.DARK_PURPLE
										+ lang.get("ERROR_INTERESTING"));
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
						profMan.startQuest(player, qm.getQuestName(selected),
								ActionSource.holderSource(qh), lang);
					}
					catch (final QuesterException e) {
						player.sendMessage(e.getMessage());
					}
				}
				else {
					player.sendMessage(ChatColor.RED + lang.get("ERROR_Q_NOT_SELECTED"));
				}
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignBreak(final BlockBreakEvent event) {
		final Block block = event.getBlock();
		if(block.getType().getId() == 63 || block.getType().getId() == 68) {
			final Sign sign = (Sign) block.getState();
			if(holMan.getSign(sign.getLocation()) != null) {
				final QuesterLang lang = langMan.getPlayerLang(event.getPlayer().getName());
				if(!event.getPlayer().isSneaking()
						|| !Util.permCheck(event.getPlayer(), QConfiguration.PERM_MODIFY, false,
								null)) {
					event.setCancelled(true);
					return;
				}
				holMan.removeSign(sign.getLocation());
				event.getPlayer().sendMessage(Quester.LABEL + lang.get("SIGN_UNREGISTERED"));;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignChange(final SignChangeEvent event) {
		final Block block = event.getBlock();
		if(event.getLine(0).equals("[Quester]")) {
			final QuesterLang lang = langMan.getPlayerLang(event.getPlayer().getName());
			if(!Util.permCheck(event.getPlayer(), QConfiguration.PERM_MODIFY, true, lang)) {
				block.breakNaturally();
			}
			event.setLine(0, ChatColor.BLUE + "[Quester]");
			final QuesterSign sign = new QuesterSign(block.getLocation());
			holMan.addSign(sign);
			event.getPlayer().sendMessage(Quester.LABEL + lang.get("SIGN_REGISTERED"));;
		}
	}
}
