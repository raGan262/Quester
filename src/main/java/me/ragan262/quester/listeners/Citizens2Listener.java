package me.ragan262.quester.listeners;

import java.util.List;

import me.ragan262.quester.ActionSource;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.exceptions.HolderException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.holder.QuestHolder;
import me.ragan262.quester.holder.QuestHolderManager;
import me.ragan262.quester.holder.QuesterTrait;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.Messenger;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.objectives.NpcKillObjective;
import me.ragan262.quester.objectives.NpcObjective;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.utils.Util;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class Citizens2Listener implements Listener {
	
	private QuestManager qMan = null;
	private QuestHolderManager holMan = null;
	private LanguageManager langMan = null;
	private ProfileManager profMan = null;
	private Messenger messenger = null;
	
	public Citizens2Listener(final Quester plugin) {
		qMan = plugin.getQuestManager();
		langMan = plugin.getLanguageManager();
		holMan = plugin.getHolderManager();
		profMan = plugin.getProfileManager();
		messenger = plugin.getMessenger();
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeftClick(final NPCLeftClickEvent event) {
		if(event.getNPC().hasTrait(QuesterTrait.class)) {
			final QuestHolder qh =
					holMan.getHolder(event.getNPC().getTrait(QuesterTrait.class).getHolderID());
			final Player player = event.getClicker();
			final QuesterLang lang = langMan.getPlayerLang(player.getName());
			if(!Util.permCheck(player, QConfiguration.PERM_USE_NPC, true, lang)) {
				return;
			}
			// If player has perms and holds blaze rod
			final boolean isOp = Util.permCheck(player, QConfiguration.PERM_MODIFY, false, null);
			if(isOp) {
				if(player.getItemInHand().getTypeId() == 369) {
					event.getNPC().getTrait(QuesterTrait.class).setHolderID(-1);
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
				return;
			}
			qh.interact(player.getName());
			
			final Quest quest = qMan.getQuest(holMan.getOne(qh));
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
				holMan.selectNext(player.getName(), qh, lang);
			}
			catch (final HolderException e) {
				player.sendMessage(e.getMessage());
				if(!isOp) {
					return;
				}
				
			}
			
			player.sendMessage(Util.line(ChatColor.BLUE, event.getNPC().getName() + "'s quests",
					ChatColor.GOLD));
			if(isOp) {
				messenger.showHolderQuestsModify(qh, player, qMan);
			}
			else {
				messenger.showHolderQuestsUse(qh, player, qMan);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRightClick(final NPCRightClickEvent event) {
		if(event.getNPC().hasTrait(QuesterTrait.class)) {
			final QuestHolder qh =
					holMan.getHolder(event.getNPC().getTrait(QuesterTrait.class).getHolderID());
			final Player player = event.getClicker();
			final QuesterLang lang = langMan.getPlayerLang(player.getName());
			if(!Util.permCheck(player, QConfiguration.PERM_USE_NPC, true, lang)) {
				return;
			}
			final boolean isOP = Util.permCheck(player, QConfiguration.PERM_MODIFY, false, null);
			// If player has perms and holds blaze rod
			if(isOP) {
				if(player.getItemInHand().getTypeId() == 369) {
					final int sel = profMan.getProfile(player.getName()).getHolderID();
					if(sel < 0) {
						player.sendMessage(ChatColor.RED + lang.get("ERROR_HOL_NOT_SELECTED"));
					}
					else {
						event.getNPC().getTrait(QuesterTrait.class).setHolderID(sel);
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
			
			final PlayerProfile prof = profMan.getProfile(player.getName());
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
						profMan.complete(player, ActionSource.holderSource(qh), lang);
					}
					catch (final QuesterException e) {
						try {
							messenger.showProgress(player, prof);
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
			if(qMan.isQuestActive(selected)) {
				try {
					profMan.startQuest(player, qMan.getQuest(selected),
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
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAnyClick(final NPCRightClickEvent event) {
		final Player player = event.getClicker();
		final PlayerProfile prof = profMan.getProfile(player.getName());
		final Quest quest = prof.getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				if(objs.get(i).getType().equalsIgnoreCase("NPC")) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final NpcObjective obj = (NpcObjective) objs.get(i);
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
	public void onNpcDeath(final NPCDeathEvent event) {
		final Player player = event.getNPC().getBukkitEntity().getKiller();
		if(player == null) {
			return;
		}
		final PlayerProfile prof = profMan.getProfile(player.getName());
		final Quest quest = prof.getQuest();
		if(quest != null) {
			if(!quest.allowedWorld(player.getWorld().getName().toLowerCase())) {
				return;
			}
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				if(objs.get(i).getType().equalsIgnoreCase("NPCKILL")) {
					if(!profMan.isObjectiveActive(prof, i)) {
						continue;
					}
					final NpcKillObjective obj = (NpcKillObjective) objs.get(i);
					if(obj.checkNpc(event.getNPC().getName())) {
						profMan.incProgress(player, ActionSource.listenerSource(event), i);
						return;
					}
				}
			}
		}
	}
}
