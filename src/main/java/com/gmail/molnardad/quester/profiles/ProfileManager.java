package com.gmail.molnardad.quester.profiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.management.InstanceNotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.LanguageManager;
import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.events.ObjectiveCompleteEvent;
import com.gmail.molnardad.quester.events.QuestCancelEvent;
import com.gmail.molnardad.quester.events.QuestCompleteEvent;
import com.gmail.molnardad.quester.events.QuestStartEvent;
import com.gmail.molnardad.quester.exceptions.ObjectiveException;
import com.gmail.molnardad.quester.exceptions.QuestException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.profiles.QuestProgress.ObjectiveStatus;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.quests.QuestFlag;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.storage.ConfigStorage;
import com.gmail.molnardad.quester.storage.Storage;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.strings.QuesterLang;
import com.gmail.molnardad.quester.utils.Util;

public class ProfileManager {

	private Storage profileStorage = null;
	private QuestManager qMan = null;
	private LanguageManager langMan = null;
	private Quester plugin = null;
	private Random randGen = new Random();

	private Map<String, PlayerProfile> profiles = new HashMap<String, PlayerProfile>();
	private Map<Integer, String> ranks = new HashMap<Integer, String>();
	private List<Integer> sortedRanks = new ArrayList<Integer>();
	
	
	public ProfileManager(Quester plugin) {
		this.plugin = plugin;
		qMan = plugin.getQuestManager();
		langMan = plugin.getLanguageManager();
		File file = new File(plugin.getDataFolder(), "profiles.yml");
		profileStorage = new ConfigStorage(file, Quester.log, null);
	}

	
	private PlayerProfile createProfile(String playerName) {
		PlayerProfile prof = new PlayerProfile(playerName);
		profiles.put(playerName.toLowerCase(), prof);
		return prof;
	}
	
	private void updateRank(PlayerProfile prof) {
		int pts = prof.getPoints();
		String lastRank = "";
		for(int i : sortedRanks) {
			if(pts >= i) {
				lastRank = ranks.get(i);
			} 
			else 
				break;
		}
		prof.setRank(lastRank);
	}


	public PlayerProfile[] getProfiles() {
		return profiles.values().toArray(new PlayerProfile[0]);
	}
	
	public PlayerProfile getProfile(String playerName) {
		if(playerName == null) {
			return null;
		}
		PlayerProfile prof = profiles.get(playerName.toLowerCase());
		if(prof == null) {
			prof = createProfile(playerName);
		}
		return prof;
	}
	
	public boolean hasProfile(String playerName) {
		return profiles.containsKey(playerName.toLowerCase());
	}

	public Map<Integer, String> getRanks() {
		return ranks;
	}
	
	public Quest getSelectedQuest(String playerName) {
		if(playerName == null) {
			return null;
		}
		return getProfile(playerName).getSelected();
	}
	
	public boolean setProgress(String playerName, int objective, int value) {
		return setProgress(playerName, getProfile(playerName).getQuestProgressIndex(), objective, value);
	}
	
	public boolean setProgress(String playerName, int index, int objective, int value) {
		QuestProgress prog = getProfile(playerName).getProgress(index);
		if(prog != null) {
			prog.setProgress(objective, value);
			return true;
		}
		return false;
	}
	
	public void assignQuest(String playerName, Quest quest) {
		getProfile(playerName).addQuest(quest);
	}
	
	public void unassignQuest(String playerName) {
		unassignQuest(playerName, -1);
	}
	
	public void unassignQuest(String playerName, int index) {
		PlayerProfile prof = getProfile(playerName);
		if(index < 0) {
			prof.unsetQuest();
		}
		else {
			prof.unsetQuest(index);
		}
		prof.refreshActive();
	}
	
	public void addCompletedQuest(String playerName, String questName) {
		PlayerProfile prof = getProfile(playerName);
		prof.addCompleted(questName, (int) (System.currentTimeMillis() / 1000));
	}
	
	public void selectQuest(String changer, Quest newSelected) throws QuesterException {
		getProfile(changer).setSelected(newSelected);
	}
	
	public void clearSelectedQuest(String playerName) {
		getProfile(playerName).setSelected(null);
	}
	
	public void selectHolder(String changer, int id) throws QuesterException {
		getProfile(changer).setHolderID(id);
	}
	
	public void clearSelectedHolder(String playerName) {
		getProfile(playerName).setHolderID(-1);
	}
	
	public boolean switchQuest(String playerName, int id) {
		return getProfile(playerName).setActiveQuest(id);
	}
	
	public int addPoints(String playerName, int amount) {
		return getProfile(playerName).addPoints(amount);
	}
	
	public boolean areObjectivesCompleted(Player player) {
		
		for(ObjectiveStatus status : getProfile(player.getName()).getProgress().getObjectiveStatuses()) {
			if(status != ObjectiveStatus.COMPLETED) {
				return false;
			}
		}
		return true;
	}
	
	public int getCurrentObjective(Player player) {
		
		return getProfile(player.getName()).getProgress().getCurrentObjectiveID();
	}
	
	public boolean isObjectiveActive(Player player, int id) {
		
		QuestProgress progress = getProfile(player.getName()).getProgress();
		return progress.getObjectiveStatus(id) == ObjectiveStatus.ACTIVE;
	}
	
	// QUEST PROGRESS METHODS

	public void startQuest(Player player, int questID, ActionSource as, QuesterLang lang) throws QuesterException {
		Quest qst = qMan.getQuest(questID);
		startQuest(player, qst, as, lang);
	}
	
	public void startQuest(Player player, String questName, ActionSource as, QuesterLang lang) throws QuesterException {
		Quest qst = qMan.getQuest(questName);
		startQuest(player, qst, as, lang);
	}
	
	public void startQuest(Player player, Quest quest, ActionSource as, QuesterLang lang) throws QuesterException {
		String playerName = player.getName();
		if(quest == null){
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		PlayerProfile prof = getProfile(playerName);
		if(prof.hasQuest(quest)) {
			throw new QuestException(lang.ERROR_Q_ASSIGNED);
		}
		if(prof.getQuestAmount() >= QConfiguration.maxQuests) {
			throw new QuestException(lang.ERROR_Q_MAX_AMOUNT);
		}
		if(!quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		if(as.is(ActionSource.COMMAND) && quest.hasFlag(QuestFlag.HIDDEN))
			throw new QuestException(lang.ERROR_Q_NOT_CMD);
		if (!Util.permCheck(player, QConfiguration.PERM_ADMIN, false, null)){
			for(Condition con : quest.getConditions()) {
				if(!con.isMet(player, plugin)) {
					player.sendMessage(ChatColor.RED + con.inShow());
					return;
				}
			}
		}
		/* QuestStartEvent */
		QuestStartEvent event = new QuestStartEvent(as, player, quest);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			if(QConfiguration.verbose) {
				Quester.log.info("QuestStart event cancelled. (" + player.getName() + "; '" + quest.getName() + "')");
			}
			return;
		}
		
		assignQuest(playerName, quest);
		if(QConfiguration.progMsgStart)
			player.sendMessage(Quester.LABEL + lang.MSG_Q_STARTED.replaceAll("%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
		String description = quest.getDescription(playerName);
		if(!description.isEmpty() && !quest.hasFlag(QuestFlag.NODESC))
			player.sendMessage(description);
		if(QConfiguration.verbose)
			Quester.log.info(playerName + " started quest '" + quest.getName() + "'.");
		for(Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -1)
				qv.execute(player, plugin);
		}
	}
	
	public void startRandomQuest(Player player, ActionSource as, QuesterLang lang) throws QuesterException {
		Collection<Quest> allQuests = qMan.getQuests();
		ArrayList<Quest> chosenQuests = new ArrayList<Quest>();
		for(Quest quest : allQuests) {
			if(quest.hasFlag(QuestFlag.ACTIVE) 
					&& !quest.hasFlag(QuestFlag.HIDDEN) 
					&& !getProfile(player.getName()).hasQuest(quest) 
					&& qMan.areConditionsMet(player, quest, lang)) {
				chosenQuests.add(quest);
			}
		}
		allQuests = null;
		if(chosenQuests.isEmpty()) {
			throw new QuestException(lang.ERROR_Q_NONE_ACTIVE);
		}
		int id = randGen.nextInt(chosenQuests.size());
		startQuest(player, chosenQuests.get(id).getName(), as, lang);
	}
	public void cancelQuest(Player player, ActionSource as, QuesterLang lang) throws QuesterException {
		cancelQuest(player, -1, as, lang);
	}
	public void cancelQuest(Player player, int index, ActionSource as, QuesterLang lang) throws QuesterException {
		Quest quest = null;
		PlayerProfile prof = getProfile(player.getName());
		if(index < 0) {
			if(prof.getProgress() != null) {
				quest = prof.getProgress().getQuest();
			}
		}
		else {
			if(prof.getProgress(index) != null) {
				quest = prof.getProgress(index).getQuest();
			}
		}
		if(quest == null) {
			throw new QuestException(lang.ERROR_Q_NOT_ASSIGNED);
		}
		if(as == null) {
			as = ActionSource.BLANKSOURCE;
		}
		if(as.is(ActionSource.COMMAND) && quest.hasFlag(QuestFlag.UNCANCELLABLE)) {
			throw new QuestException(lang.ERROR_Q_CANT_CANCEL);
		}
		/* QuestCancelEvent */
		QuestCancelEvent event = new QuestCancelEvent(as, player, quest);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		unassignQuest(player.getName(), index);
		if(QConfiguration.progMsgCancel)
			player.sendMessage(Quester.LABEL + lang.MSG_Q_CANCELLED.replaceAll("%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
		if(QConfiguration.verbose)
			Quester.log.info(player.getName() + " cancelled quest '" + quest.getName() + "'.");
		for(Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -2)
				qv.execute(player, plugin);
		}
	}
	
	public void complete(Player player, ActionSource as, QuesterLang lang) throws QuesterException {
		complete(player, as, lang, true);
	}
	
	public void complete(Player player, ActionSource as, QuesterLang lang, boolean checkObjs) throws QuesterException {
		Quest quest = getProfile(player.getName()).getQuest();
		if(quest == null)
			throw new QuestException(lang.ERROR_Q_NOT_ASSIGNED);
		if(as == null) {
			as = ActionSource.BLANKSOURCE;
		}
		
		if(as.is(ActionSource.COMMAND) && quest.hasFlag(QuestFlag.HIDDEN))
			throw new QuestException(lang.ERROR_Q_NOT_CMD);
		
    	if(!quest.allowedWorld(player.getWorld().getName()))
    		throw new QuestException(lang.ERROR_Q_BAD_WORLD);
    	
		boolean error = false;
		if(checkObjs) {
			error = ! completeObjective(player, as, lang);
		}
		
		if(areObjectivesCompleted(player)) {
			completeQuest(player, as, lang);
		}
		else if(error) {
			throw new ObjectiveException(lang.ERROR_OBJ_CANT_DO);
		}
	}
	
	private boolean completeObjective(Player player, ActionSource as, QuesterLang lang) throws QuesterException {
		Quest quest = getProfile(player.getName()).getQuest();
		List<Objective> objs = quest.getObjectives();
		
		int i = 0;
		boolean completed = false;
		while(i<objs.size() && !completed) {
			if(isObjectiveActive(player, i)) {
				if(objs.get(i).tryToComplete(player)) {
					incProgress(player, as, i, false);
					completed = true;
				} 
			}
			i++;
		}

		return (completed || i == 0);
	}
	
	public void completeQuest(Player player, ActionSource as,  QuesterLang lang) throws QuesterException {
		Quest quest = getProfile(player.getName()).getQuest();
		
		unassignQuest(player.getName());
		addCompletedQuest(player.getName(), quest.getName());
		if(QConfiguration.progMsgDone) {
			player.sendMessage(Quester.LABEL + lang.MSG_Q_COMPLETED.replaceAll("%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
		}
		if(QConfiguration.verbose) {
			Quester.log.info(player.getName() + " completed quest '" + quest.getName() + "'.");
		}
		/* QuestCompleteEvent */
		QuestCompleteEvent event = new QuestCompleteEvent(as, player, quest);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		for(Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -3)
				qv.execute(player, plugin);
		}
		if(quest.hasFlag(QuestFlag.ONLYFIRST)) {
			qMan.deactivateQuest(quest);
		}
	}
	
	public void incProgress(Player player, ActionSource as, int objectiveId) {
		incProgress(player, as, objectiveId, 1, true);
	}
	
	public void incProgress(Player player, ActionSource as, int objectiveId, boolean checkAll) {
		incProgress(player, as, objectiveId, 1, checkAll);
	}
	
	public void incProgress(Player player, ActionSource as, int objectiveId, int amount) {
		incProgress(player, as, objectiveId, amount, true);
	}
	
	public void incProgress(final Player player, ActionSource as, int objectiveId, int amount, boolean checkAll) {
		QuesterLang lang = langMan.getPlayerLang(player.getName());
		PlayerProfile prof = getProfile(player.getName());
		QuestProgress prog = prof.getProgress();
		if(prog == null || objectiveId < 0 || objectiveId >= prog.getSize()) {
			return;
		}
		int newValue = prog.getProgress()[objectiveId] + amount;
		Quest q = prof.getQuest();
		Objective obj = q.getObjectives().get(objectiveId);
		setProgress(prof.getName(), objectiveId, newValue);
		
		// TODO add progress update message
		if(obj.getTargetAmount() <= newValue) {
			if(QConfiguration.progMsgObj && !obj.isHidden()) {
				player.sendMessage(Quester.LABEL + lang.MSG_OBJ_COMPLETED);
			}
			/* ObjectiveCompleteEvent */
			ObjectiveCompleteEvent event = new ObjectiveCompleteEvent(as, player, q, objectiveId);
			Bukkit.getServer().getPluginManager().callEvent(event);
			
			
			for(Qevent qv : q.getQevents()) {
				if(qv.getOccasion() == objectiveId) {
					qv.execute(player, plugin);
				}
			}
			if(checkAll) {
				try{
					complete(player, as, lang, false);
				} catch (QuesterException ignore) {}
			}
		} 
	}
	
	public String[] validateProgress(PlayerProfile profile) {
		QuestProgress[] progs = profile.getProgresses();
		boolean localUnset = false;
		List<String> unset = new ArrayList<String>(QConfiguration.maxQuests);
		for(int i=progs.length-1; i >= 0; i--) {
			localUnset = false;
			Quest quest = progs[i].getQuest();
			if(!quest.equals(qMan.getQuest(quest.getID()))		// if ID or name changed
					|| progs[i].getSize() != quest.getObjectives().size()) {	// if number of objectives changed
				localUnset = true;
			}
			
			if(localUnset) {
				profile.unsetQuest(i);
				unset.add(quest.getName());
			}
		}
		return unset.toArray(new String[0]);
	}
	
	// DISPLAY METHODS
	
	public void showProfile(CommandSender sender) {
		showProfile(sender, sender.getName(), langMan.getPlayerLang(sender.getName()));
	}
	
	public void showProfile(CommandSender sender, String name, QuesterLang lang) {
		if(!hasProfile(name)) {
			sender.sendMessage(ChatColor.RED + lang.INFO_PROFILE_NOT_EXIST.replaceAll("%p", name));
			return;
		}
		PlayerProfile prof = getProfile(name);
		sender.sendMessage(ChatColor.BLUE + lang.INFO_NAME + ": " + ChatColor.GOLD + prof.getName());
		sender.sendMessage(ChatColor.BLUE + lang.INFO_PROFILE_POINTS + ": " + ChatColor.WHITE + prof.getPoints());
		if(QConfiguration.useRank) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_PROFILE_RANK + ": " + ChatColor.GOLD + prof.getRank());
		}
		sender.sendMessage(ChatColor.BLUE + lang.INFO_PROFILE_COMPLETED + ": " + ChatColor.WHITE + Util.implode(prof.getCompletedQuests(), ','));
		
	}
	
	public void showProgress(Player player, QuesterLang lang) throws QuesterException {
		showProgress(player, -1, lang);
	}
	
	public void showProgress(Player player, int index, QuesterLang lang) throws QuesterException {
		Quest quest = null;
		QuestProgress progress = null;
		PlayerProfile prof = getProfile(player.getName());
		if(index < 0) {
			progress = prof.getProgress();
		}
		else {
			progress = prof.getProgress(index);
		}
		if(progress == null) {
			throw new QuestException(lang.ERROR_Q_NOT_ASSIGNED);
		}
		quest = progress.getQuest();
		
		if(!quest.hasFlag(QuestFlag.HIDDENOBJS)) {
			player.sendMessage(lang.INFO_PROGRESS.replaceAll("%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
			List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				if(!objs.get(i).isHidden()) {
					if(progress.getObjectiveStatus(i) == ObjectiveStatus.COMPLETED) {
						player.sendMessage(ChatColor.GREEN + " - " + lang.INFO_PROGRESS_COMPLETED);
					} else {
						boolean active = progress.getObjectiveStatus(i) == ObjectiveStatus.ACTIVE;
						if((active || !QConfiguration.ordOnlyCurrent)) {
							ChatColor col = active ? ChatColor.YELLOW : ChatColor.RED;
							player.sendMessage(col + " - " + objs.get(i).inShow(progress.getProgress()[i]));
						}
					}
				}
			}
		} else {
			player.sendMessage(Quester.LABEL + lang.INFO_PROGRESS_HIDDEN);
		}
	}
	
	public void showTakenQuests(CommandSender sender) {
		showTakenQuests(sender, sender.getName(), langMan.getPlayerLang(sender.getName()));
	}
	
	public void showTakenQuests(CommandSender sender, String name, QuesterLang lang) {
		if(!hasProfile(name)) {
			sender.sendMessage(ChatColor.RED + lang.INFO_PROFILE_NOT_EXIST.replaceAll("%p", name));
			return;
		}
		PlayerProfile prof = getProfile(name);
		sender.sendMessage(ChatColor.BLUE + (sender.getName().equalsIgnoreCase(name) ? lang.INFO_QUESTS + ": " : lang.INFO_QUESTS_OTHER.replaceAll("%p", prof.getName()) + ": " ) 
				+ "(" + lang.INFO_LIMIT + ": " + QConfiguration.maxQuests + ")");
		int current = prof.getQuestProgressIndex();
		for(int i=0; i<prof.getQuestAmount(); i++) {
			sender.sendMessage("[" + i + "] " + (current == i ? ChatColor.GREEN : ChatColor.YELLOW) + prof.getProgress(i).getQuest().getName());
		}
		
	}
	
	// STORAGE

	public void loadRanks() {
		Map<Integer, String> rankMap = new HashMap<Integer, String>();
		List<Integer> sortedList = new ArrayList<Integer>();
		
		StorageKey rankKey = null;
		try {
			rankKey = QConfiguration.getConfigKey("ranks");
		}
		catch (InstanceNotFoundException e) {
			Quester.log.severe("DataManager instance exception occured while acessing ranks.");
		}
		if(rankKey != null) {
			for(StorageKey subKey : rankKey.getSubKeys()) {
				rankMap.put(subKey.getInt(""), subKey.getName().replace('-', ' '));
				sortedRanks.add(subKey.getInt(""));
			}
		}
		if(sortedRanks.size() == 0) {
			rankKey.setInt("Default-Rank", 0);
			rankMap.put(0, "Default-Rank");
			sortedList.add(0);
			Quester.log.info("No ranks found. Added default rank.");
			try {
				QConfiguration.saveData();
			}
			catch (InstanceNotFoundException ignore) { }
		}
		Collections.sort(sortedList);
		this.ranks = rankMap;
		this.sortedRanks = sortedList;
	}
	
	public void loadProfiles() {
		profileStorage.load();
		StorageKey mainKey = profileStorage.getKey("");
		PlayerProfile prof;
		for(StorageKey subKey : mainKey.getSubKeys()) {
			prof = PlayerProfile.deserialize(subKey, qMan);
			if(prof != null) {
				updateRank(prof);
				profiles.put(prof.getName().toLowerCase(), prof);
			} else {
				Quester.log.info("Invalid key in profiles.yml: " + subKey.getName());
			}
		}
		if(QConfiguration.verbose) {
			Quester.log.info(profiles.size() + " profiles loaded.");
		}
	}
	
	public void saveProfiles(){
		StorageKey pKey = profileStorage.getKey("");
		for(String p : profiles.keySet()) {
			profiles.get(p).serialize(pKey.getSubKey(p));
		}
		profileStorage.save();
	}
}
