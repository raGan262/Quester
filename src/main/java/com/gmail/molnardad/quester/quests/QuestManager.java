package com.gmail.molnardad.quester.quests;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.*;
import com.gmail.molnardad.quester.lang.LanguageManager;
import com.gmail.molnardad.quester.lang.QuesterLang;
import com.gmail.molnardad.quester.profiles.PlayerProfile;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.profiles.QuestProgress;
import com.gmail.molnardad.quester.storage.ConfigStorage;
import com.gmail.molnardad.quester.storage.Storage;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

public class QuestManager {
	
	private LanguageManager langMan = null;
	private ProfileManager profMan = null;
	private Quester plugin = null;
	private Storage questStorage = null;
	
	private final Map<Integer, Quest> quests = new TreeMap<Integer, Quest>();
	private final Map<String, Integer> questNames = new HashMap<String, Integer>();
	public Map<Integer, Location> questLocations = new HashMap<Integer, Location>();
	
	private int questID = -1;
	
	public QuestManager(final Quester plugin) {
		langMan = plugin.getLanguageManager();
		this.plugin = plugin;
		final File file = new File(plugin.getDataFolder(), "quests.yml");
		questStorage = new ConfigStorage(file, Quester.log, null);
	}
	
	public void setProfileManager(final ProfileManager profMan) {
		this.profMan = profMan;
	}
	
	// QUEST ID MANIPULATION
	
	public int getLastQuestID() {
		return questID;
	}
	
	public void assignQuestID(final Quest qst) {
		questID++;
		qst.setID(questID);
	}
	
	public void adjustQuestID() {
		int newID = -1;
		for(final int i : quests.keySet()) {
			if(i > newID) {
				newID = i;
			}
		}
		questID = newID;
	}
	
	// QUEST MANIPULATION
	
	public void modifyCheck(final Quest quest, final QuesterLang lang) throws QuesterException {
		if(quest == null) {
			throw new QuestException(lang.ERROR_Q_NOT_SELECTED);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestException(lang.ERROR_Q_CANT_MODIFY);
		}
	}
	
	public Set<Integer> getQuestIds() {
		return quests.keySet();
	}
	
	public Collection<Quest> getQuests() {
		return quests.values();
	}
	
	public Quest getQuest(final String questName) {
		final Integer id = questNames.get(questName.toLowerCase());
		if(id == null) {
			return null;
		}
		return quests.get(id);
	}
	
	public Quest getQuest(final Integer questID) {
		return quests.get(questID);
	}
	
	public String getQuestName(final int id) {
		final Quest q = getQuest(id);
		if(q == null) {
			return "non-existant";
		}
		else {
			return q.getName();
		}
	}
	
	public boolean isQuest(final int questID) {
		
		return quests.containsKey(questID);
	}
	
	public boolean isQuest(final String questName) {
		
		return questNames.containsKey(questName.toLowerCase());
	}
	
	public boolean isQuestActive(final CommandSender sender) {
		
		return isQuestActive(profMan.getSelectedQuest(sender.getName()));
	}
	
	public boolean isQuestActive(final String questName) {
		
		return isQuestActive(getQuest(questName));
	}
	
	public boolean isQuestActive(final int questID) {
		
		return isQuestActive(getQuest(questID));
	}
	
	public boolean isQuestActive(final Quest q) {
		
		if(q == null) {
			return false;
		}
		return q.hasFlag(QuestFlag.ACTIVE);
	}
	
	public Quest createQuest(final String issuer, final String questName, final QuesterLang lang) throws QuesterException {
		if(isQuest(questName)) {
			throw new QuestException(lang.ERROR_Q_EXIST);
		}
		final Quest quest = new Quest(questName);
		assignQuestID(quest);
		quests.put(quest.getID(), quest);
		questNames.put(questName.toLowerCase(), quest.getID());
		profMan.selectQuest(issuer, quest);
		return quest;
	}
	
	public Quest removeQuest(final String issuer, final int questID, final QuesterLang lang) throws QuesterException {
		final Quest q = getQuest(questID);
		modifyCheck(q, lang);
		questNames.remove(q.getName().toLowerCase());
		questLocations.remove(q.getID());
		quests.remove(q.getID());
		questStorage.getKey(q.getName().toLowerCase()).removeKey("");
		adjustQuestID();
		return q;
	}
	
	public void activateQuest(final Quest q) {
		q.addFlag(QuestFlag.ACTIVE);
	}
	
	public void deactivateQuest(final Quest quest) {
		quest.removeFlag(QuestFlag.ACTIVE);
		for(final PlayerProfile prof : profMan.getProfiles()) {
			if(prof.hasQuest(quest)) {
				profMan.unassignQuest(prof.getName(), prof.getQuestProgressIndex(quest));
				final Player player = Bukkit.getServer().getPlayerExact(prof.getName());
				if(player != null) {
					player.sendMessage(Quester.LABEL + langMan.getPlayerLang(player.getName()).MSG_Q_DEACTIVATED);
				}
			}
		}
		profMan.saveProfiles();
	}
	
	public boolean toggleQuest(final CommandSender issuer, final QuesterLang lang) throws QuesterException {
		return toggleQuest(profMan.getSelectedQuest(issuer.getName()), lang);
	}
	
	public boolean toggleQuest(final int questID, final QuesterLang lang) throws QuesterException {
		return toggleQuest(getQuest(questID), lang);
	}
	
	public boolean toggleQuest(final Quest q, final QuesterLang lang) throws QuesterException {
		if(q == null) {
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		if(q.hasFlag(QuestFlag.ACTIVE)) {
			deactivateQuest(q);
			return false;
		}
		else {
			activateQuest(q);
			return true;
		}
	}
	
	public void changeQuestName(final String issuer, final String newName, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		if(isQuest(newName)) {
			throw new QuestException(lang.ERROR_Q_EXIST);
		}
		modifyCheck(quest, lang);
		questNames.remove(quest.getName().toLowerCase());
		quest.setName(newName);
		questNames.put(newName.toLowerCase(), quest.getID());
	}
	
	public void setQuestDescription(final String issuer, final String newDesc, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		quest.setDescription(newDesc);
	}
	
	public void addQuestDescription(final String issuer, final String descToAdd, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		quest.addDescription(descToAdd);
	}
	
	public void setQuestLocation(final String issuer, final Location loc, final int range, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		quest.setLocation(loc);
		quest.setRange(range);
		questLocations.put(quest.getID(), loc);
	}
	
	public void removeQuestLocation(final String issuer, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		quest.setLocation(null);
		quest.setRange(1);
		questLocations.remove(quest.getID());
	}
	
	public void addQuestWorld(final String issuer, final String worldName, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		quest.addWorld(worldName);
	}
	
	public boolean removeQuestWorld(final String issuer, final String worldName, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		final boolean result = quest.removeWorld(worldName);
		return result;
	}
	
	public void addQuestFlag(final String issuer, final QuestFlag[] flags, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		for(final QuestFlag f : flags) {
			quest.addFlag(f);
		}
	}
	
	public void removeQuestFlag(final String issuer, final QuestFlag[] flags, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		for(final QuestFlag f : flags) {
			quest.removeFlag(f);
		}
	}
	
	public void addQuestObjective(final String issuer, final Objective newObjective, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		quest.addObjective(newObjective);
	}
	
	public void setQuestObjective(final String issuer, final int id, final Objective newObjective, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		final Objective oldObj = quest.getObjective(id);
		if(oldObj == null) {
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
		quest.setObjective(id, newObjective);
		for(final int prerequisity : oldObj.getPrerequisites()) {
			newObjective.addPrerequisity(prerequisity);
		}
	}
	
	public void removeQuestObjective(final String issuer, final int id, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		if(!quest.removeObjective(id)) {
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
	}
	
	public void addObjectiveDescription(final String issuer, final int id, final String desc, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		final List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
		objs.get(id).addDescription(desc);
	}
	
	public void removeObjectiveDescription(final String issuer, final int id, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		final List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
		objs.get(id).removeDescription();
	}
	
	public void swapQuestObjectives(final String issuer, final int first, final int second, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		if(first == second) {
			throw new CustomException(lang.ERROR_WHY);
		}
		modifyCheck(quest, lang);
		
		if(quest.getObjective(first) == null || quest.getObjective(second) == null) {
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
		final List<Objective> objs = quest.getObjectives();
		final Objective obj = objs.get(first);
		objs.set(first, objs.get(second));
		objs.set(second, obj);
		final List<Qevent> evts = quest.getQevents();
		for(final Qevent e : evts) {
			if(e.getOccasion() == first) {
				e.setOccasion(second);
			}
			else if(e.getOccasion() == second) {
				e.setOccasion(first);
			}
		}
	}
	
	public void moveQuestObjective(final String issuer, final int which, final int where, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		if(which == where) {
			throw new CustomException(lang.ERROR_WHY);
		}
		modifyCheck(quest, lang);
		
		if(quest.getObjective(which) == null || quest.getObjective(where) == null) {
			throw new CustomException(lang.ERROR_CMD_ID_OUT_OF_BOUNDS);
		}
		Util.moveListUnit(quest.getObjectives(), which, where);
		final List<Qevent> evts = quest.getQevents();
		for(final Qevent e : evts) {
			final int occ = e.getOccasion();
			if(occ == which) {
				e.setOccasion(where);
			}
			else if(which < where) {
				if(occ > which && occ <= where) {
					e.setOccasion(occ - 1);
				}
			}
			else {
				if(occ < which && occ >= where) {
					e.setOccasion(occ + 1);
				}
			}
		}
	}
	
	public void addObjectivePrerequisites(final String issuer, final int id, final Set<Integer> prereq, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		final List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
		for(final int i : prereq) {
			if(i >= objs.size() || i < 0 || i != id) {
				objs.get(id).addPrerequisity(i);
			}
		}
	}
	
	public void removeObjectivePrerequisites(final String issuer, final int id, final Set<Integer> prereq, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		final List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
		for(final int i : prereq) {
			objs.get(id).removePrerequisity(i);
		}
	}
	
	public void addQuestCondition(final String issuer, final Condition newCondition, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		quest.addCondition(newCondition);
	}
	
	public void setQuestCondition(final String issuer, final int id, final Condition newCondition, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		if(quest.getCondition(id) == null) {
			throw new ConditionException(lang.ERROR_CON_NOT_EXIST);
		}
		quest.setCondition(id, newCondition);
	}
	
	public void removeQuestCondition(final String issuer, final int id, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		if(!quest.removeCondition(id)) {
			throw new ConditionException(lang.ERROR_CON_NOT_EXIST);
		}
	}
	
	public void addConditionDescription(final String issuer, final int id, final String desc, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		final List<Condition> cons = quest.getConditions();
		if(id >= cons.size() || id < 0) {
			throw new ConditionException(lang.ERROR_CON_NOT_EXIST);
		}
		cons.get(id).addDescription(desc);
	}
	
	public void removeConditionDescription(final String issuer, final int id, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		final List<Condition> cons = quest.getConditions();
		if(id >= cons.size() || id < 0) {
			throw new ConditionException(lang.ERROR_CON_NOT_EXIST);
		}
		cons.get(id).removeDescription();
	}
	
	public void addQuestQevent(final String issuer, final Qevent newQevent, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		final int occasion = newQevent.getOccasion();
		if(occasion < -3 || occasion >= quest.getObjectives().size()) {
			throw new ConditionException(lang.ERROR_OCC_NOT_EXIST);
		}
		quest.addQevent(newQevent);
	}
	
	public void setQuestQevent(final String issuer, final int qeventID, final Qevent newQevent, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		final int occasion = newQevent.getOccasion();
		if(occasion < -3 || occasion >= quest.getObjectives().size()) {
			throw new ConditionException(lang.ERROR_OCC_NOT_EXIST);
		}
		quest.setQevent(qeventID, newQevent);
	}
	
	public void removeQuestQevent(final String issuer, final int id, final QuesterLang lang) throws QuesterException {
		final Quest quest = profMan.getSelectedQuest(issuer);
		modifyCheck(quest, lang);
		if(!quest.removeQevent(id)) {
			throw new QeventException(lang.ERROR_EVT_NOT_EXIST);
		}
	}
	
	public boolean areConditionsMet(final Player player, final String questName, final QuesterLang lang) throws QuesterException {
		return areConditionsMet(player, getQuest(questName), lang);
	}
	
	public boolean areConditionsMet(final Player player, final Quest quest, final QuesterLang lang) throws QuesterException {
		if(quest == null) {
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		for(final Condition c : quest.getConditions()) {
			if(!c.isMet(player, plugin)) {
				return false;
			}
		}
		
		return true;
	}
	
	// MESSENGER METHODS
	
	public void showQuest(final CommandSender sender, final String questName, final QuesterLang lang) throws QuesterException {
		Quest qst = null;
		if(questName.isEmpty()) {
			final QuestProgress prog = profMan.getProfile(sender.getName()).getProgress();
			if(prog != null) {
				qst = prog.getQuest();
			}
		}
		else {
			qst = getQuest(questName);
		}
		
		if(qst == null) {
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		if(!qst.hasFlag(QuestFlag.ACTIVE) || qst.hasFlag(QuestFlag.HIDDEN)) {
			if(!Util.permCheck(sender, QConfiguration.PERM_MODIFY, false, null)) {
				throw new QuestException(lang.ERROR_Q_NOT_EXIST);
			}
		}
		Player player = null;
		if(sender instanceof Player) {
			player = (Player) sender;
		}
		sender.sendMessage(ChatColor.BLUE + lang.INFO_NAME + ": " + ChatColor.GOLD + qst.getName());
		final String string = qst.getDescription(sender.getName());
		if(!string.isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_DESCRIPTION + ": " + ChatColor.WHITE + string);
		}
		final List<Condition> cons = qst.getConditions();
		if(!cons.isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_CONDITIONS + ":");
		}
		ChatColor color = ChatColor.WHITE;
		for(int i = 0; i < cons.size(); i++) {
			if(player != null) {
				color = cons.get(i).isMet(player, plugin) ? ChatColor.GREEN : ChatColor.RED;
			}
			sender.sendMessage(color + " - " + cons.get(i).inShow());
		}
		if(!qst.hasFlag(QuestFlag.HIDDENOBJS)) {
			final List<Objective> objs = qst.getObjectives();
			sender.sendMessage(ChatColor.BLUE + lang.INFO_OBJECTIVES + ":");
			for(int i = 0; i < objs.size(); i++) {
				if(!objs.get(i).isHidden() && (objs.get(i).getPrerequisites().isEmpty() || !QConfiguration.ordOnlyCurrent)) {
					sender.sendMessage(ChatColor.WHITE + " - " + objs.get(i).inShow(0));
				}
			}
		}
	}
	
	public void showQuestInfo(final CommandSender sender, final QuesterLang lang) throws QuesterException {
		showQuestInfo(sender, profMan.getSelectedQuest(sender.getName()), lang);
	}
	
	public void showQuestInfo(final CommandSender sender, final int id, final QuesterLang lang) throws QuesterException {
		showQuestInfo(sender, getQuest(id), lang);
	}
	
	public void showQuestInfo(final CommandSender sender, final String questName, final QuesterLang lang) throws QuesterException {
		showQuestInfo(sender, getQuest(questName), lang);
	}
	
	public void showQuestInfo(final CommandSender sender, final Quest quest, final QuesterLang lang) throws QuesterException {
		if(quest == null) {
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.INFO_QUEST_INFO, ChatColor.GOLD));
		
		sender.sendMessage(ChatColor.BLUE + lang.INFO_NAME + ": " + "[" + quest.getID() + "]" + ChatColor.GOLD + quest
				.getName());
		String string = quest.getDescription(sender.getName());
		if(!string.isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_DESCRIPTION + ": " + ChatColor.WHITE + string);
		}
		if(quest.hasLocation()) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_LOCATION + ": " + ChatColor.WHITE + Util
					.displayLocation(quest.getLocation()));
		}
		string = QuestFlag.stringize(quest.getFlags());
		if(!string.isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_FLAGS + ": " + ChatColor.WHITE + string);
		}
		if(!quest.getWorlds().isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_WORLDS + ": " + ChatColor.WHITE + quest
					.getWorldNames());
		}
		int i;
		final Map<Integer, Map<Integer, Qevent>> qmap = quest.getQeventMap();
		sender.sendMessage(ChatColor.BLUE + lang.INFO_EVENTS + ":");
		for(i = -1; i > -4; i--) {
			if(qmap.get(i) != null) {
				sender.sendMessage(ChatColor.GOLD + " " + Qevent.parseOccasion(i) + ":");
				for(final int j : qmap.get(i).keySet()) {
					sender.sendMessage("  <" + j + "> " + qmap.get(i).get(j).inInfo());
				}
			}
		}
		sender.sendMessage(ChatColor.BLUE + lang.INFO_CONDITIONS + ":");
		i = 0;
		for(final Condition c : quest.getConditions()) {
			sender.sendMessage(" [" + i + "] " + c.inInfo());
			i++;
			
		}
		sender.sendMessage(ChatColor.BLUE + lang.INFO_OBJECTIVES + ":");
		i = 0;
		for(final Objective o : quest.getObjectives()) {
			final String color = o.isHidden() ? ChatColor.YELLOW + "" : "";
			sender.sendMessage(color + " [" + i + "] " + o.inInfo());
			if(qmap.get(i) != null) {
				for(final int j : qmap.get(i).keySet()) {
					sender.sendMessage("  <" + j + "> " + qmap.get(i).get(j).inInfo());
				}
			}
			i++;
		}
	}
	
	public void showQuestList(final CommandSender sender, final QuesterLang lang) {
		Player player = null;
		if(sender instanceof Player) {
			player = (Player) sender;
		}
		else {
			showFullQuestList(sender, lang);
			return;
		}
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.INFO_QUEST_LIST, ChatColor.GOLD));
		ChatColor color = ChatColor.RED;
		final PlayerProfile prof = profMan.getProfile(player.getName());
		Quest quest = null;
		for(final int i : getQuestIds()) {
			quest = getQuest(i);
			if(quest.hasFlag(QuestFlag.ACTIVE) && !quest.hasFlag(QuestFlag.HIDDEN)) {
				try {
					if(prof.hasQuest(quest)) {
						color = ChatColor.YELLOW;
					}
					else if(prof.isCompleted(quest.getName()) && !quest
							.hasFlag(QuestFlag.REPEATABLE)) {
						color = ChatColor.GREEN;
					}
					else if(areConditionsMet(player, quest, lang)) {
						color = ChatColor.BLUE;
					}
					else {
						color = ChatColor.RED;
					}
				}
				catch (final Exception e) {
					e.printStackTrace();
				}
				sender.sendMessage(ChatColor.GOLD + "* " + color + quest.getName());
			}
		}
	}
	
	public void showFullQuestList(final CommandSender sender, final QuesterLang lang) {
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.INFO_QUEST_LIST, ChatColor.GOLD));
		Quest q = null;
		for(final int i : getQuestIds()) {
			q = getQuest(i);
			final ChatColor color = q.hasFlag(QuestFlag.ACTIVE) ? ChatColor.GREEN : ChatColor.RED;
			final ChatColor color2 = q.hasFlag(QuestFlag.HIDDEN) ? ChatColor.YELLOW : ChatColor.BLUE;
			sender.sendMessage(color2 + "[" + q.getID() + "]" + color + q.getName());
		}
	}
	
	public void saveQuests() {
		for(final StorageKey subKey : questStorage.getKey("").getSubKeys()) {
			subKey.removeKey("");
		}
		for(final Quest q : quests.values()) {
			final StorageKey key = questStorage.getKey(String.valueOf(q.getID()));
			q.serialize(key);
		}
		questStorage.save();
	}
	
	public boolean loadQuests() {
		return loadQuests(null);
	}
	
	public boolean loadQuests(final String fileName) {
		Storage storage = questStorage;
		if(fileName != null) {
			final File storageFile = new File(plugin.getDataFolder(), fileName);
			if(storageFile.exists()) {
				storage = new ConfigStorage(storageFile, plugin.getLogger(), null);
			}
			else {
				return false;
			}
		}
		if(!storage.load()) {
			return false;
		}
		
		if(fileName == null) {
			quests.clear();
			questNames.clear();
			questLocations.clear();
		}
		final List<Quest> onHold = new ArrayList<Quest>();
		int lastGeneric = 0;
		for(final StorageKey questKey : storage.getKey("").getSubKeys()) {
			if(questKey.hasSubKeys()) {
				if(QConfiguration.debug) {
					Quester.log.info("Deserializing quest " + questKey.getName() + ".");
				}
				final Quest quest = Quest.deserialize(questKey);
				if(quest == null) {
					Quester.log.severe("Quest " + questKey.getName() + " corrupted.");
					continue;
				}
				if(questNames.containsKey(quest.getName().toLowerCase())) { // duplicate name,
																			// generating new one
					quest.setName("");
					String name = "";
					while(questNames.containsKey(quest.getName()) || quest.getName().isEmpty()) {
						name = "generic" + lastGeneric;
						quest.setName(name);
						lastGeneric++;
					}
					Quester.log
							.severe("Duplicate quest name in quest " + questKey.getName() + " detected, generated new name '" + name + "'.");
				}
				if(quest.hasID()) {
					if(quests.get(quest.getID()) != null) { // duplicate ID
						Quester.log
								.severe("Duplicate quest ID in quest " + questKey.getName() + " detected, new ID will be assigned.");
						quest.setID(-1);
						onHold.add(quest);
					}
					else { // everything all right
						quests.put(quest.getID(), quest);
						questNames.put(quest.getName().toLowerCase(), quest.getID());
						if(quest.hasLocation()) {
							questLocations.put(quest.getID(), quest.getLocation());
						}
					}
				}
				else { // quest has default ID, new needs to be generated
					onHold.add(quest);
				}
				for(int i = 0; i < quest.getObjectives().size(); i++) {
					if(quest.getObjective(i) == null) {
						Quester.log.info("Objective " + i + " is invalid.");
						quest.removeObjective(i);
						quest.removeFlag(QuestFlag.ACTIVE);
					}
				}
				for(int i = 0; i < quest.getConditions().size(); i++) {
					if(quest.getCondition(i) == null) {
						Quester.log.info("Condition " + i + " is invalid.");
						quest.removeCondition(i);
					}
				}
				for(int i = 0; i < quest.getQevents().size(); i++) {
					if(quest.getQevent(i) == null) {
						Quester.log.info("Event " + i + " is invalid.");
						quest.removeQevent(i);
					}
				}
			}
		}
		adjustQuestID(); // get ID ready
		for(final Quest q : onHold) {
			assignQuestID(q);
			quests.put(q.getID(), q);
			questNames.put(q.getName().toLowerCase(), q.getID());
			if(q.hasLocation()) {
				questLocations.put(q.getID(), q.getLocation());
			}
		}
		if(QConfiguration.verbose) {
			Quester.log.info(quests.size() + " quests loaded.");
		}
		return true;
	}
}
