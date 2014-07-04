package me.ragan262.quester.holder;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.ragan262.quester.ActionSource;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.exceptions.HolderException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.Messenger;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.utils.Util;

public abstract class QuestHolderActionHandler<T> {
	
	protected final QuestManager qMan;
	protected final QuestHolderManager holMan;
	protected final LanguageManager langMan;
	protected final ProfileManager profMan;
	protected final Messenger messenger;
	
	public QuestHolderActionHandler(final Quester plugin) {
		qMan = plugin.getQuestManager();
		langMan = plugin.getLanguageManager();
		holMan = plugin.getHolderManager();
		profMan = plugin.getProfileManager();
		messenger = plugin.getMessenger();
	}
	
	public abstract String getUsePermission();
	
	public boolean isQuestHolderItem(final ItemStack item) {
		return item.getType() == Material.BLAZE_ROD;
	}
	
	public abstract void assignHolder(final QuestHolder qHolder, T data);
	
	public abstract void unassignHolder(final QuestHolder qHolder, T data);
	
	public String getHeaderText(final Player player, final QuestHolder qHolder, final T data) {
		return qHolder.getName() + "'s quests";
	}
	
	public void onLeftClick(final Player player, final QuestHolder qHolder, final T data) {
		final QuesterLang lang = langMan.getPlayerLang(player.getName());
		if(!Util.permCheck(player, getUsePermission(), true, lang)) {
			return;
		}
		final boolean isOp = Util.permCheck(player, QConfiguration.PERM_MODIFY, false, null);
		if(isOp && isQuestHolderItem(player.getItemInHand())) {
			unassignHolder(qHolder, data);
			player.sendMessage(ChatColor.GREEN + lang.get("HOL_UNASSIGNED"));
			return;
		}
		if(qHolder == null) {
			player.sendMessage(ChatColor.RED + lang.get("ERROR_HOL_NOT_ASSIGNED"));
			return;
		}
		if(!qHolder.canInteract(player.getName())) {
			player.sendMessage(ChatColor.RED + lang.get("ERROR_HOL_INTERACT"));
			return;
		}
		qHolder.interact(player.getName());
		
		final Quest quest = qMan.getQuest(holMan.getOne(qHolder));
		if(quest != null) {
			if(profMan.getProfile(player.getName()).hasQuest(quest)) {
				return;
			}
			else {
				try {
					messenger.showQuest(player, quest);
					return;
				}
				catch (final QuesterException ignore) {}
			}
		}
		
		try {
			holMan.selectNext(player.getName(), qHolder, lang);
		}
		catch (final HolderException e) {
			player.sendMessage(e.getMessage());
			if(!isOp) {
				return;
			}
			
		}
		
		player.sendMessage(Util.line(ChatColor.BLUE, getHeaderText(player, qHolder, data),
				ChatColor.GOLD));
		if(isOp) {
			messenger.showHolderQuestsModify(qHolder, player, qMan);
		}
		else {
			messenger.showHolderQuestsUse(qHolder, player, qMan);
		}
	}
	
	public void onRightClick(final Player player, final QuestHolder qHolder, final T data) {
		final QuesterLang lang = langMan.getPlayerLang(player.getName());
		if(!Util.permCheck(player, getUsePermission(), true, lang)) {
			return;
		}
		final PlayerProfile prof = profMan.getProfile(player.getName());
		final boolean isOP = Util.permCheck(player, QConfiguration.PERM_MODIFY, false, null);
		// If player has perms and holds blaze rod
		if(isOP && isQuestHolderItem(player.getItemInHand())) {
			final QuestHolder selected = holMan.getHolder(prof.getHolderID());
			if(selected == null) {
				player.sendMessage(ChatColor.RED + lang.get("ERROR_HOL_NOT_SELECTED"));
			}
			else {
				assignHolder(qHolder, data);
				player.sendMessage(ChatColor.GREEN + lang.get("HOL_ASSIGNED"));
			}
			return;
		}
		if(qHolder == null) {
			player.sendMessage(ChatColor.RED + lang.get("ERROR_HOL_NOT_ASSIGNED"));
			return;
		}
		if(!qHolder.canInteract(player.getName())) {
			player.sendMessage(ChatColor.RED + lang.get("ERROR_HOL_INTERACT"));
			return;
		}
		qHolder.interact(player.getName());
		final List<Integer> qsts = qHolder.getQuests();
		
		final Quest currentQuest = prof.getQuest();
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
					profMan.complete(player, ActionSource.holderSource(qHolder), lang);
				}
				catch (final QuesterException e) {
					try {
						messenger.showProgress(player, prof);
					}
					catch (final QuesterException f) {
						player.sendMessage(ChatColor.DARK_PURPLE + lang.get("ERROR_INTERESTING"));
					}
				}
				return;
			}
		}
		int selected = holMan.getOne(qHolder);
		if(selected < 0) {
			selected = qHolder.getSelectedId(player.getName());
		}
		// player doesn't have quest
		if(qMan.isQuestActive(selected)) {
			try {
				profMan.startQuest(player, qMan.getQuest(selected),
						ActionSource.holderSource(qHolder), lang);
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
