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
import com.gmail.molnardad.quester.LanguageManager;
import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.HolderException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.holder.QuestHolder;
import com.gmail.molnardad.quester.holder.QuestHolderManager;
import com.gmail.molnardad.quester.holder.QuesterSign;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.strings.QuesterLang;
import com.gmail.molnardad.quester.utils.Util;

public class SignListeners implements Listener {

	private QuestManager qm = null;
	private QuestHolderManager holMan = null;
	private LanguageManager langMan = null;
	private ProfileManager profMan = null;
	
	public SignListeners(Quester plugin) {
		this.qm = plugin.getQuestManager();
		this.langMan = plugin.getLanguageManager();
		this.holMan = plugin.getHolderManager();
		this.profMan = plugin.getProfileManager();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			QuesterSign qs = holMan.getSign(block.getLocation());
			if(qs == null) {
				return;
			}
			QuesterLang lang = langMan.getPlayerLang(player.getName());
			if(block.getType().getId() != 63 && block.getType().getId() != 68) {
				holMan.removeSign(block.getLocation());
				player.sendMessage(Quester.LABEL + lang.SIGN_UNREGISTERED);
				return;
			} else { 
				Sign sign = (Sign) block.getState();
				if(!sign.getLine(0).equals(ChatColor.BLUE + "[Quester]")) {
					block.breakNaturally();
					holMan.removeSign(block.getLocation());
					player.sendMessage(Quester.LABEL + lang.SIGN_UNREGISTERED);
					return;
				}
			}
			
			if(!Util.permCheck(player, QConfiguration.PERM_USE_SIGN, true, lang)) {
				return;
			}
			if(player.isSneaking()) {
				return;
			}
			boolean isOp = Util.permCheck(player, QConfiguration.PERM_MODIFY, false, null);

			event.setCancelled(true);
			QuestHolder qh = holMan.getHolder(qs.getHolderID());
			if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				
				if(isOp) {
					if(player.getItemInHand().getTypeId() == 369) {
						qs.setHolderID(-1);
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
				
				player.sendMessage(Util.line(ChatColor.BLUE, lang.SIGN_HEADER, ChatColor.GOLD));
				if(isOp) {
					holMan.showQuestsModify(qh, player);
				} else {
					holMan.showQuestsUse(qh, player);
				}
				
			} else {
				
				if(isOp) {
					if(player.getItemInHand().getTypeId() == 369) {
						int sel = profMan.getProfile(player.getName()).getHolderID();
						if(sel < 0){
							player.sendMessage(ChatColor.RED + lang.ERROR_HOL_NOT_ASSIGNED);
						} else {
							qs.setHolderID(sel);
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
						profMan.startQuest(player, qm.getQuestName(selected), ActionSource.holderSource(qh), lang);
					} catch (QuesterException e) {
						player.sendMessage(e.getMessage());
					}
				} else {
					player.sendMessage(ChatColor.RED + lang.ERROR_Q_NOT_SELECTED);
				}
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if(block.getType().getId() == 63 || block.getType().getId() == 68) {
			Sign sign = (Sign) block.getState();
			if(holMan.getSign(sign.getLocation()) != null) {
				QuesterLang lang = langMan.getPlayerLang(event.getPlayer().getName());
				if(!event.getPlayer().isSneaking() || !Util.permCheck(event.getPlayer(), QConfiguration.PERM_MODIFY, false, null)) {
					event.setCancelled(true);
					return;
				}
				holMan.removeSign(sign.getLocation());
				event.getPlayer().sendMessage(Quester.LABEL + lang.SIGN_UNREGISTERED);;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		Block block = event.getBlock();
		if(event.getLine(0).equals("[Quester]")) {
			QuesterLang lang = langMan.getPlayerLang(event.getPlayer().getName());
			if(!Util.permCheck(event.getPlayer(), QConfiguration.PERM_MODIFY, true, lang)) {
				block.breakNaturally();
			}
			event.setLine(0, ChatColor.BLUE + "[Quester]");
			QuesterSign sign = new QuesterSign(block.getLocation());
			holMan.addSign(sign);
			event.getPlayer().sendMessage(Quester.LABEL + lang.SIGN_REGISTERED);;
		}
	}
}
