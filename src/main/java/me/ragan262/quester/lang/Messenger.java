package me.ragan262.quester.lang;

import java.util.List;
import java.util.Map;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.Quester;
import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.exceptions.QuestException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.holder.QuestHolder;
import me.ragan262.quester.holder.QuestHolderManager;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.profiles.QuestProgress;
import me.ragan262.quester.profiles.QuestProgress.ObjectiveStatus;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestFlag;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.utils.SerUtils;
import me.ragan262.quester.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messenger {
	
	private LanguageManager langMan;
	
	public Messenger(final LanguageManager langMan) {
		this.langMan = langMan;
	}
	
	public void setLanguageManager(final LanguageManager langMan) {
		if(langMan == null) {
			throw new IllegalArgumentException("LanguageManager can't be null.");
		}
		this.langMan = langMan;
	}
	
	public LanguageManager getlanguageManager() {
		return langMan;
	}
	
	// QUEST RELATED MESSAGES
	
	public void showQuest(final CommandSender sender, final Quest quest, final QuesterLang lang) throws QuesterException {
		
		if(quest == null) {
			throw new QuestException(lang.get("ERROR_Q_NOT_EXIST"));
		}
		if(!quest.hasFlag(QuestFlag.ACTIVE) || quest.hasFlag(QuestFlag.HIDDEN)) {
			if(!Util.permCheck(sender, QConfiguration.PERM_MODIFY, false, null)) {
				throw new QuestException(lang.get("ERROR_Q_NOT_EXIST"));
			}
		}
		Player player = null;
		if(sender instanceof Player) {
			player = (Player)sender;
		}
		sender.sendMessage(ChatColor.BLUE + lang.get("INFO_NAME") + ": " + ChatColor.GOLD
				+ quest.getName());
		final String string = quest.getDescription(sender.getName(), lang);
		if(!string.isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.get("INFO_DESCRIPTION") + ": "
					+ ChatColor.WHITE + string);
		}
		final List<Condition> cons = quest.getConditions();
		if(!cons.isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.get("INFO_CONDITIONS") + ":");
		}
		ChatColor color = ChatColor.WHITE;
		for(Condition con : cons) {
			if(player != null) {
				color = con.isMet(player) ? ChatColor.GREEN : ChatColor.RED;
			}
			sender.sendMessage(color + " - " + con.inShow(player, lang));
		}
		if(!quest.hasFlag(QuestFlag.HIDDENOBJS)) {
			final List<Objective> objs = quest.getObjectives();
			sender.sendMessage(ChatColor.BLUE + lang.get("INFO_OBJECTIVES") + ":");
			for(Objective obj : objs) {
				if(!obj.isHidden() && (obj.getPrerequisites().isEmpty() || !QConfiguration.ordOnlyCurrent)) {
					sender.sendMessage(ChatColor.WHITE + " - " + obj.inShow(0, lang));
				}
			}
		}
	}
	
	public void showQuestInfo(final CommandSender sender, final Quest quest, final QuesterLang lang) throws QuesterException {
		if(quest == null) {
			throw new QuestException(lang.get("ERROR_Q_NOT_EXIST"));
		}
		
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.get("INFO_QUEST_INFO"), ChatColor.GOLD));
		
		sender.sendMessage(ChatColor.BLUE + lang.get("INFO_NAME") + ": " + "[" + quest.getID()
				+ "]" + ChatColor.GOLD + quest.getName());
		String msgString = quest.getRawDescription();
		if(!msgString.isEmpty()) {
			final ChatColor color = langMan.customMessageExists(LanguageManager.getCustomMessageKey(msgString))
					? ChatColor.GREEN : ChatColor.RED;
			sender.sendMessage(ChatColor.BLUE + lang.get("INFO_DESCRIPTION") + ": " + color
					+ msgString);
		}
		if(quest.hasLocation()) {
			sender.sendMessage(ChatColor.BLUE + lang.get("INFO_LOCATION") + ": " + ChatColor.WHITE
					+ SerUtils.displayLocation(quest.getLocation()));
		}
		msgString = QuestFlag.stringize(quest.getFlags());
		if(!msgString.isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.get("INFO_FLAGS") + ": " + ChatColor.WHITE
					+ msgString);
		}
		if(!quest.getWorlds().isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.get("INFO_WORLDS") + ": " + ChatColor.WHITE
					+ quest.getWorldNames());
		}
		int i;
		final Map<Integer, Map<Integer, Qevent>> qmap = quest.getQeventMap();
		sender.sendMessage(ChatColor.BLUE + lang.get("INFO_EVENTS") + ":");
		for(i = -1; i > -4; i--) {
			if(qmap.get(i) != null) {
				sender.sendMessage(ChatColor.GOLD + " " + Qevent.parseOccasion(i) + ":");
				for(final int j : qmap.get(i).keySet()) {
					sender.sendMessage("  <" + j + "> " + qmap.get(i).get(j).inInfo());
				}
			}
		}
		sender.sendMessage(ChatColor.BLUE + lang.get("INFO_CONDITIONS") + ":");
		i = 0;
		for(final Condition c : quest.getConditions()) {
			sender.sendMessage(" [" + i + "] " + c.inInfo(langMan));
			i++;
			
		}
		sender.sendMessage(ChatColor.BLUE + lang.get("INFO_OBJECTIVES") + ":");
		i = 0;
		for(final Objective o : quest.getObjectives()) {
			final String color = o.isHidden() ? ChatColor.YELLOW + "" : "";
			sender.sendMessage(color + " [" + i + "] " + o.inInfo(langMan));
			if(qmap.get(i) != null) {
				for(final int j : qmap.get(i).keySet()) {
					sender.sendMessage("  <" + j + "> " + qmap.get(i).get(j).inInfo());
				}
			}
			i++;
		}
	}
	
	public void showQuestList(final CommandSender sender, final QuestManager qMan, final ProfileManager profMan, final QuesterLang lang) {
		Player player = null;
		final PlayerProfile prof = profMan.getSenderProfile(sender);
		if(sender instanceof Player) {
			player = (Player)sender;
		}
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.get("INFO_QUEST_LIST"), ChatColor.GOLD));
		ChatColor color = ChatColor.RED;
		for(final Quest quest : qMan.getQuests()) {
			if(quest.hasFlag(QuestFlag.ACTIVE) && !quest.hasFlag(QuestFlag.HIDDEN)) {
				try {
					if(prof.hasQuest(quest)) {
						color = ChatColor.YELLOW;
					}
					else if(prof.isCompleted(quest.getName())
							&& !quest.hasFlag(QuestFlag.REPEATABLE)) {
						color = ChatColor.GREEN;
					}
					else if(player == null || qMan.areConditionsMet(player, quest, lang)) {
						color = ChatColor.BLUE;
					}
					else {
						color = ChatColor.RED;
					}
				}
				catch(final Exception e) {
					e.printStackTrace();
				}
				sender.sendMessage(ChatColor.GOLD + "* " + color + quest.getName());
			}
		}
	}
	
	public void showFullQuestList(final CommandSender sender, final QuestManager qMan, final QuesterLang lang) {
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.get("INFO_QUEST_LIST"), ChatColor.GOLD));
		for(final Quest quest : qMan.getQuests()) {
			final ChatColor color = quest.hasFlag(QuestFlag.ACTIVE) ? ChatColor.GREEN
					: ChatColor.RED;
			final ChatColor color2 = quest.hasFlag(QuestFlag.HIDDEN) ? ChatColor.YELLOW
					: ChatColor.BLUE;
			sender.sendMessage(color2 + "[" + quest.getID() + "]" + color + quest.getName());
		}
	}
	
	// PROFILE RELATED MESSAGES
	
	public void showProfile(final CommandSender sender, final PlayerProfile prof, final QuesterLang lang) {
		sender.sendMessage(ChatColor.BLUE + lang.get("INFO_NAME") + ": " + ChatColor.GOLD
				+ prof.getName());
		sender.sendMessage(ChatColor.BLUE + lang.get("INFO_PROFILE_POINTS") + ": "
				+ ChatColor.WHITE + prof.getPoints());
		if(QConfiguration.useRank) {
			sender.sendMessage(ChatColor.BLUE + lang.get("INFO_PROFILE_RANK") + ": "
					+ ChatColor.GOLD + prof.getRank());
		}
		
	}
	
	public void showProgress(final Player player, final PlayerProfile prof, final QuesterLang lang) throws QuesterException {
		showProgress(player, prof, -1, lang);
	}
	
	public void showProgress(final Player player, final PlayerProfile prof, final int index, final QuesterLang lang) throws QuesterException {
		Quest quest = null;
		QuestProgress progress = null;
		if(index < 0) {
			progress = prof.getProgress();
		}
		else {
			progress = prof.getProgress(index);
		}
		if(progress == null) {
			throw new QuestException(lang.get("ERROR_Q_NOT_ASSIGNED"));
		}
		quest = progress.getQuest();
		
		if(!quest.hasFlag(QuestFlag.HIDDENOBJS)) {
			player.sendMessage(lang.get("INFO_PROGRESS").replaceAll("%q", ChatColor.GOLD
					+ quest.getName() + ChatColor.BLUE));
			final List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				if(!objs.get(i).isHidden()) {
					if(progress.getObjectiveStatus(i) == ObjectiveStatus.COMPLETED) {
						player.sendMessage(ChatColor.GREEN + " - "
								+ lang.get("INFO_PROGRESS_COMPLETED"));
					}
					else {
						final boolean active = progress.getObjectiveStatus(i) == ObjectiveStatus.ACTIVE;
						if(active || !QConfiguration.ordOnlyCurrent) {
							final ChatColor col = active ? ChatColor.YELLOW : ChatColor.RED;
							player.sendMessage(col + " - "
									+ objs.get(i).inShow(progress.getProgress().get(i), lang));
						}
					}
				}
			}
		}
		else {
			player.sendMessage(Quester.LABEL + lang.get("INFO_PROGRESS_HIDDEN"));
		}
	}
	
	public void showTakenQuests(final CommandSender sender, final PlayerProfile prof, final QuesterLang lang) {
		sender.sendMessage(ChatColor.BLUE
				+ (sender.getName().equalsIgnoreCase(prof.getName()) ? lang.get("INFO_QUESTS")
						+ ": " : lang.get("INFO_QUESTS_OTHER").replaceAll("%p", prof.getName())
						+ ": ") + "(" + lang.get("INFO_LIMIT") + ": " + QConfiguration.maxQuests
				+ ")");
		final int current = prof.getQuestProgressIndex();
		for(int i = 0; i < prof.getQuestAmount(); i++) {
			sender.sendMessage("[" + i + "] " + (current == i ? ChatColor.GREEN : ChatColor.YELLOW)
					+ prof.getProgress(i).getQuest().getName());
		}
		
	}
	
	// HOLDER RELATED MESSAGES
	
	public void showHolderList(final CommandSender sender, final QuestHolderManager holMan, final QuesterLang lang) {
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.get("INFO_HOLDER_LIST"), ChatColor.GOLD));
		for(final int id : holMan.getHolders().keySet()) {
			sender.sendMessage(ChatColor.BLUE + "[" + id + "]" + ChatColor.GOLD + " "
					+ holMan.getHolder(id).getName());
		}
	}
	
	public boolean showHolderQuestsUse(final QuestHolder holder, final CommandSender sender, final QuestManager qMan) {
		if(holder == null) {
			return false;
		}
		final List<Integer> heldQuests = holder.getQuests();
		final int selected = holder.getSelected(sender.getName());
		for(int i = 0; i < heldQuests.size(); i++) {
			if(qMan.isQuestActive(heldQuests.get(i))) {
				sender.sendMessage((i == selected ? ChatColor.GREEN : ChatColor.BLUE) + " - "
						+ qMan.getQuestName(heldQuests.get(i)));
			}
		}
		return true;
	}
	
	public boolean showHolderQuestsModify(final QuestHolder holder, final CommandSender sender, final QuestManager qMan) {
		if(holder == null) {
			return false;
		}
		sender.sendMessage(ChatColor.GOLD + "Holder name: " + ChatColor.RESET + holder.getName());
		final List<Integer> heldQuests = holder.getQuests();
		final int selected = holder.getSelected(sender.getName());
		for(int i = 0; i < heldQuests.size(); i++) {
			final ChatColor col = qMan.isQuestActive(heldQuests.get(i)) ? ChatColor.BLUE
					: ChatColor.RED;
			
			sender.sendMessage(i + ". " + (i == selected ? ChatColor.GREEN : ChatColor.BLUE) + "["
					+ heldQuests.get(i) + "] " + col + qMan.getQuestName(heldQuests.get(i)));
		}
		return true;
	}
}
