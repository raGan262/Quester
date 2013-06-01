package com.gmail.molnardad.quester.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.PlayerProfile;
import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.QuestFlag;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.*;
import com.gmail.molnardad.quester.storage.ConfigStorage;
import com.gmail.molnardad.quester.storage.Storage;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.strings.QuesterLang;
import com.gmail.molnardad.quester.utils.Util;

public class QuestManager {

	private static final Random randGen = new Random();
	private LanguageManager langMan = null;
	private ProfileManager profMan = null;
	private Quester plugin = null;
	private Storage questStorage = null;
	
	private Map<Integer, Quest> quests = new HashMap<Integer, Quest>();
	private Map<String, Integer> questNames = new HashMap<String, Integer>();
	public Map<Integer, Location> questLocations = new HashMap<Integer, Location>();
	
	private int questID = -1;
	
	public QuestManager(Quester plugin) {
		this.langMan = plugin.getLanguageManager();
		this.plugin = plugin;
		File file = new File(plugin.getDataFolder(), "quests.yml");
		questStorage = new ConfigStorage(file, Quester.log, null);
	}

	public void setProfileManager(ProfileManager profMan) {
		this.profMan = profMan;
	}
	
	// QUEST ID MANIPULATION
	
	public int getLastQuestID(){
		return questID;
	}
	
	public void assignQuestID(Quest qst) {
		questID++;
		qst.setID(questID);
	}
	
	public void adjustQuestID() {
		int newID = -1;
		for(int i : quests.keySet()) {
			if(i > newID)
				newID = i;
		}
		questID = newID;
	}
	
	// QUEST MANIPULATION

	public void modifyCheck(Quest quest, QuesterLang lang) throws QuesterException {
		if(quest == null) {
			throw new QuestException(lang.ERROR_Q_NOT_SELECTED);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestException(lang.ERROR_Q_CANT_MODIFY);
		}
	}
	
	public Set<Integer> getQuestIds() {
		Set<Integer> result = new TreeSet<Integer>();
		for(int i : quests.keySet()) {
			result.add(i);
		}
		return result;
	}
	
	public Collection<Quest> getQuests() {
		return quests.values();
	}
	
	public Quest getQuest(String questName) {
		return quests.get(questNames.get(questName.toLowerCase()));
	}
	
	public Quest getQuest(Integer questID) {
		return quests.get(questID);
	}
	
	public String getQuestName(int id) {
		Quest q = getQuest(id);
		if(q == null)
			return "non-existant";
		else
			return q.getName();
	}

	public Quest getPlayerQuest(String playerName) {
		return getQuest(profMan.getProfile(playerName).getQuest());
	}
	
	public Quest getPlayerQuest(String playerName, int index) {
		if(index < 0) {
			return null;
		}
		else {
			return getQuest(profMan.getProfile(playerName).getQuest(index));
		}
	}
	
	public boolean areConditionsMet(Player player, String questName, QuesterLang lang) throws QuesterException {
		return areConditionsMet(player, getQuest(questName), lang);
	}
	
	public boolean areConditionsMet(Player player, Quest quest, QuesterLang lang) throws QuesterException {
		if(quest == null)
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		for(Condition c : quest.getConditions()) {
			if(!c.isMet(player, plugin))
				return false;
		}
		
		return true;
	}
	
	public boolean areObjectivesCompleted(Player player) {
		
		List<Objective> objs = getPlayerQuest(player.getName()).getObjectives();
		PlayerProfile prof = profMan.getProfile(player.getName());
		boolean all = true;
		
		for(int i = 0; i < objs.size(); i++) {
			if(objs.get(i).isComplete(prof.getProgress().get(i)))
				continue;
			all = false;
		}
		
		return all;
	}
	
	public int getObjectiveAmount(int id) {
		return getObjectiveAmount(getQuest(id));
	}
	
	public int getObjectiveAmount(String questName) {
		return getObjectiveAmount(getQuest(questName));
	}
	
	public int getObjectiveAmount(Quest quest) {
		if(quest == null)
			return -1;
		return quest.getObjectives().size();
	}
	
	public int getCurrentObjective(Player player) {
		List<Objective> objs = getPlayerQuest(player.getName()).getObjectives();
		PlayerProfile prof = profMan.getProfile(player.getName());
	
		for(int i=0; i<objs.size(); i++) {
			if(!objs.get(i).isComplete( prof.getProgress().get(i)))
				return i;
		}
		
		return -1;
	}
	
	public boolean isObjectiveActive(Player player, int id) {
		List<Objective> objs = getPlayerQuest(player.getName()).getObjectives();
		List<Integer> progress = profMan.getProgress(player.getName());
		Set<Integer> prereq = objs.get(id).getPrerequisites();
		
		if(objs.get(id).isComplete(progress.get(id))) {
			return false;
		}
		for(int i : prereq) {
			try {
				if(!objs.get(i).isComplete(progress.get(i))) {
					return false;
				}
			} catch (IndexOutOfBoundsException ignore) {
			}
		}
		return true;
	}
	
	public boolean isQuest(int questID) {
		return quests.containsKey(questID);
	}
	
	public boolean isQuest(String questName) {
		return questNames.containsKey(questName.toLowerCase());
	}
	
	public boolean isQuestActive(CommandSender sender) {
		return isQuestActive(profMan.getSelectedQuestID(sender.getName()));
	}
	
	public boolean isQuestActive(String questName) {
		return isQuestActive(getQuest(questName));
	}
	
	public boolean isQuestActive(int questID) {
		return isQuestActive(getQuest(questID));
	}
	
	public boolean isQuestActive(Quest q) {
		if(q == null)
			return false;
		return q.hasFlag(QuestFlag.ACTIVE);
	}
	
	public Quest createQuest(String issuer, String questName, QuesterLang lang) throws QuesterException {
		if(isQuest(questName)){
			throw new QuestException(lang.ERROR_Q_EXIST);
		}
		Quest q = new Quest(questName);
		assignQuestID(q);
		quests.put(q.getID(), q);
		questNames.put(questName.toLowerCase(), q.getID());
		profMan.selectQuest(issuer, q.getID());
		return q;
	}
	
	public Quest removeQuest(String issuer, int questID, QuesterLang lang) throws QuesterException {
		Quest q = getQuest(questID);
		modifyCheck(q, lang);
		questNames.remove(q.getName().toLowerCase());
		questLocations.remove(q.getID());
		quests.remove(q.getID());
		questStorage.getKey(q.getName().toLowerCase()).removeKey("");
		adjustQuestID();
		return q;
	}
	
	public void activateQuest(Quest q) {
		q.addFlag(QuestFlag.ACTIVE);
	}
	
	public void deactivateQuest(Quest q) {
		q.removeFlag(QuestFlag.ACTIVE);
		for(PlayerProfile prof: profMan.getProfiles()) {
			while(prof.hasQuest(q.getName())) {
				prof.unsetQuest(q.getName());
				Player player = Bukkit.getServer().getPlayerExact(prof.getName());
				if(player != null){
					player.sendMessage(Quester.LABEL + langMan.getPlayerLang(player.getName()).MSG_Q_DEACTIVATED);
				}
			}
			prof.refreshActive();
		}
		profMan.saveProfiles();
	}
	
	public boolean toggleQuest(CommandSender issuer, QuesterLang lang) throws QuesterException {
		return toggleQuest(profMan.getSelectedQuestID(issuer.getName()), lang);
	}
	
	public boolean toggleQuest(int questID, QuesterLang lang) throws QuesterException {
		return toggleQuest(getQuest(questID),lang);
	}
	
	public boolean toggleQuest(Quest q, QuesterLang lang) throws QuesterException {
		if(q == null){	
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		if(q.hasFlag(QuestFlag.ACTIVE)){
			deactivateQuest(q);
			return false;
		} else {
			activateQuest(q);
			return true;
		}
	}
	
	public void changeQuestName(String issuer, String newName, QuesterLang lang) throws QuesterException {
		int questId = profMan.getSelectedQuestID(issuer);
		Quest quest = getQuest(questId);
		if(isQuest(newName)) {
			throw new QuestException(lang.ERROR_Q_EXIST);
		}
		modifyCheck(quest, lang);
		questNames.remove(quest.getName().toLowerCase());
		quest.setName(newName);
		questNames.put(newName.toLowerCase(), questId);
	}
	
	public void setQuestDescription(String issuer, String newDesc, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		quest.setDescription(newDesc);
	}
	
	public void addQuestDescription(String issuer, String descToAdd, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		quest.addDescription(descToAdd);
	}
	
	public void setQuestLocation(String issuer, Location loc, int range, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		quest.setLocation(loc);
		quest.setRange(range);
		questLocations.put(quest.getID(), loc);
	}
	
	public void removeQuestLocation(String issuer, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		quest.setLocation(null);
		quest.setRange(1);
		questLocations.remove(quest.getID());
	}
	
	public void addQuestWorld(String issuer, String worldName, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		quest.addWorld(worldName);
	}
	
	public boolean removeQuestWorld(String issuer, String worldName, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		boolean result = quest.removeWorld(worldName);
		return result;
	}
	
	public void addQuestFlag(String issuer, QuestFlag[] flags, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		for(QuestFlag f : flags) {
			quest.addFlag(f);
		}
	}
	
	public void removeQuestFlag(String issuer, QuestFlag[] flags, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		for(QuestFlag f : flags) {
			quest.removeFlag(f);
		}
	}
	
	public void addQuestObjective(String issuer, Objective newObjective, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		quest.addObjective(newObjective);
	}
	
	public void removeQuestObjective(String issuer, int id, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		if(!quest.removeObjective(id)){
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
	}
	
	public void addObjectiveDescription(String issuer, int id, String desc, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
		objs.get(id).addDescription(desc);
	}
	
	public void removeObjectiveDescription(String issuer, int id, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
		objs.get(id).removeDescription();
	}
	
	public void swapQuestObjectives(String issuer, int first, int second, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		if(first == second) {
			throw new CustomException(lang.ERROR_WHY);
		}
		modifyCheck(quest, lang);
		
		if(quest.getObjective(first) == null || quest.getObjective(second) == null) {
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
		List<Objective> objs = quest.getObjectives();
		Objective obj = objs.get(first);
		objs.set(first, objs.get(second));
		objs.set(second, obj);
		List<Qevent> evts = quest.getQevents();
		for(Qevent e : evts) {
			if(e.getOccasion() == first) {
				e.setOccasion(second);
			}
			else if(e.getOccasion() == second) {
				e.setOccasion(first);
			}
		}
	}
	
	public void moveQuestObjective(String issuer, int which, int where, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		if(which == where) {
			throw new CustomException(lang.ERROR_WHY);
		}
		modifyCheck(quest, lang);
		
		if(quest.getObjective(which) == null || quest.getObjective(where) == null) {
			throw new CustomException(lang.ERROR_CMD_ID_OUT_OF_BOUNDS);
		}
		Util.moveListUnit(quest.getObjectives(), which, where);
		List<Qevent> evts = quest.getQevents();
		for(Qevent e : evts) {
			int occ = e.getOccasion();
			if(occ == which) {
				e.setOccasion(where);
			}
			else if(which < where) {
				if(occ > which && occ <= where) {
					e.setOccasion(occ-1);
				}
			}
			else {
				if(occ < which && occ >= where) {
					e.setOccasion(occ+1);
				}
			} 
		}
	}
	
	public void addObjectivePrerequisites(String issuer, int id, Set<Integer> prereq, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
		for(int i : prereq) {
			if(i >= objs.size() || i < 0 || i != id) {
				objs.get(id).addPrerequisity(i);
			}
		}
	}
	
	public void removeObjectivePrerequisites(String issuer, int id, Set<Integer> prereq, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveException(lang.ERROR_OBJ_NOT_EXIST);
		}
		for(int i : prereq) {
			objs.get(id).removePrerequisity(i);
		}
	}
	
	public void addQuestCondition(String issuer, Condition newCondition, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		quest.addCondition(newCondition);
	}
	
	public void removeQuestCondition(String issuer, int id, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		if(!quest.removeCondition(id)){
			throw new ConditionException(lang.ERROR_CON_NOT_EXIST);
		}
	}
	
	public void addConditionDescription(String issuer, int id, String desc, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		List<Condition> cons = quest.getConditions();
		if(id >= cons.size() || id < 0) {
			throw new ConditionException(lang.ERROR_CON_NOT_EXIST);
		}
		cons.get(id).addDescription(desc);
	}
	
	public void removeConditionDescription(String issuer, int id, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		List<Condition> cons = quest.getConditions();
		if(id >= cons.size() || id < 0) {
			throw new ConditionException(lang.ERROR_CON_NOT_EXIST);
		}
		cons.get(id).removeDescription();
	}
	
	public void addQevent(String issuer, Qevent newQevent, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		int occasion = newQevent.getOccasion();
		if(occasion < -3 || occasion >= quest.getObjectives().size() ) {
			throw new ConditionException(lang.ERROR_OCC_NOT_EXIST);
		}
		quest.addQevent(newQevent);
	}
	
	public void removeQevent(String issuer, int id, QuesterLang lang) throws QuesterException {
		Quest quest = getQuest(profMan.getSelectedQuestID(issuer));
		modifyCheck(quest, lang);
		if(!quest.removeQevent(id)){
			throw new EventException(lang.ERROR_EVT_NOT_EXIST);
		}
	}
	
	// QUEST PROGRESS METHODS

	public void startQuest(Player player, int questID, boolean command, QuesterLang lang) throws QuesterException {
		Quest qst = getQuest(questID);
		startQuest(player, qst, command, lang);
	}
	
	public void startQuest(Player player, String questName, boolean command, QuesterLang lang) throws QuesterException {
		Quest qst = getQuest(questName);
		startQuest(player, qst, command, lang);
	}
	
	public void startQuest(Player player, Quest qst, boolean command, QuesterLang lang) throws QuesterException {
		String playerName = player.getName();
		if(qst == null){
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		PlayerProfile prof = profMan.getProfile(playerName);
		if(prof.hasQuest(qst.getName())) {
			throw new QuestException(lang.ERROR_Q_ASSIGNED);
		}
		if(prof.getQuestAmount() >= DataManager.maxQuests) {
			throw new QuestException(lang.ERROR_Q_MAX_AMOUNT);
		}
		if(!qst.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		if(command && qst.hasFlag(QuestFlag.HIDDEN))
			throw new QuestException(lang.ERROR_Q_NOT_CMD);
		if (!Util.permCheck(player, DataManager.PERM_ADMIN, false, null)){
			for(Condition con : qst.getConditions()) {
				if(!con.isMet(player, plugin)) {
					player.sendMessage(ChatColor.RED + con.inShow());
					return;
				}
			}
		}
		profMan.assignQuest(playerName, qst);
		if(DataManager.progMsgStart)
			player.sendMessage(Quester.LABEL + lang.MSG_Q_STARTED.replaceAll("%q", ChatColor.GOLD + qst.getName() + ChatColor.BLUE));
		String description = qst.getDescription(playerName);
		if(!description.isEmpty() && !qst.hasFlag(QuestFlag.NODESC))
			player.sendMessage(description);
		if(DataManager.verbose)
			Quester.log.info(playerName + " started quest '" + qst.getName() + "'.");
		for(Qevent qv : qst.getQevents()) {
			if(qv.getOccasion() == -1)
				qv.execute(player, plugin);
		}
		profMan.saveProfiles();
	}
	
	public void startRandomQuest(Player player, QuesterLang lang) throws QuesterException {
		Collection<Quest> qsts = getQuests();
		ArrayList<Quest> aqsts = new ArrayList<Quest>();
		for(Quest q : qsts) {
			if(q.hasFlag(QuestFlag.ACTIVE) 
					&& !q.hasFlag(QuestFlag.HIDDEN) 
					&& !profMan.hasQuest(player.getName(), q.getName()) 
					&& areConditionsMet(player, q, lang)) {
				aqsts.add(q);
			}
		}
		qsts = null;
		if(aqsts.isEmpty()) {
			throw new QuestException(lang.ERROR_Q_NONE_ACTIVE);
		}
		int id = randGen.nextInt(aqsts.size());
		startQuest(player, aqsts.get(id).getName(), false, lang);
	}
	public void cancelQuest(Player player, boolean command, QuesterLang lang) throws QuesterException {
		cancelQuest(player, -1, command, lang);
	}
	public void cancelQuest(Player player, int index, boolean command, QuesterLang lang) throws QuesterException {
		Quest quest = null;
		if(index < 0) {
			quest = getPlayerQuest(player.getName());
		}
		else {
			quest = getPlayerQuest(player.getName(), index);
		}
		if(quest == null) {
			throw new QuestException(lang.ERROR_Q_NOT_ASSIGNED);
		}
		if(command && quest.hasFlag(QuestFlag.UNCANCELLABLE)) {
			throw new QuestException(lang.ERROR_Q_CANT_CANCEL);
		}
		profMan.unassignQuest(player.getName(), index);
		if(DataManager.progMsgCancel)
			player.sendMessage(Quester.LABEL + lang.MSG_Q_CANCELLED.replaceAll("%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
		if(DataManager.verbose)
			Quester.log.info(player.getName() + " cancelled quest '" + quest.getName() + "'.");
		for(Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -2)
				qv.execute(player, plugin);
		}
		profMan.saveProfiles();
	}
	
	public void complete(Player player, boolean command, QuesterLang lang) throws QuesterException {
		complete(player, command, lang, true);
	}
	
	public void complete(Player player, boolean command, QuesterLang lang, boolean checkObjs) throws QuesterException {
		Quest quest = getPlayerQuest(player.getName());
		if(quest == null)
			throw new QuestException(lang.ERROR_Q_NOT_ASSIGNED);
		if(command && quest.hasFlag(QuestFlag.HIDDEN))
			throw new QuestException(lang.ERROR_Q_NOT_CMD);
    	if(!quest.allowedWorld(player.getWorld().getName()))
    		throw new QuestException(lang.ERROR_Q_BAD_WORLD);
    	
		boolean error = false;
		if(checkObjs) {
			error = ! completeObjective(player, lang);
		}
		
		if(areObjectivesCompleted(player)) {
			completeQuest(player, lang);
		}
		else if(error) {
			throw new ObjectiveException(lang.ERROR_OBJ_CANT_DO);
		}
	}
	
	public boolean completeObjective(Player player, QuesterLang lang) throws QuesterException {
		Quest quest = getPlayerQuest(player.getName());
		List<Objective> objs = quest.getObjectives();
		
		int i = 0;
		boolean completed = false;
		while(i<objs.size() && !completed) {
			if(isObjectiveActive(player, i)) {
				if(objs.get(i).tryToComplete(player)) {
					incProgress(player, i, false);
					completed = true;
				} 
			}
			i++;
		}

		return (completed || i == 0);
	}
	
	public void completeQuest(Player player, QuesterLang lang) throws QuesterException {
		Quest quest = getPlayerQuest(player.getName());
		
		profMan.unassignQuest(player.getName());
		if(DataManager.progMsgDone)
			player.sendMessage(Quester.LABEL + lang.MSG_Q_COMPLETED.replaceAll("%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
		if(DataManager.verbose)
			Quester.log.info(player.getName() + " completed quest '" + quest.getName() + "'.");
		for(Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -3)
				qv.execute(player, plugin);
		}
		profMan.getProfile(player.getName()).addCompleted(quest.getName(), (int) (System.currentTimeMillis() / 1000));
		profMan.saveProfiles();
		if(quest.hasFlag(QuestFlag.ONLYFIRST)) {
			deactivateQuest(quest);
		}
	}
	
	public void incProgress(Player player, int id) {
		incProgress(player, id, 1, true);
	}
	
	public void incProgress(Player player, int id, boolean checkAll) {
		incProgress(player, id, 1, checkAll);
	}
	
	public void incProgress(Player player, int id, int amount) {
		incProgress(player, id, amount, true);
	}
	
	public void incProgress(final Player player, final int id, final int amount, final boolean checkAll) {
		QuesterLang lang = langMan.getPlayerLang(player.getName());
		PlayerProfile prof = profMan.getProfile(player.getName());
		int newValue = prof.getProgress().get(id) + amount;
		Quest q = getQuest(prof.getQuest());
		Objective obj = q.getObjectives().get(id);
		prof.getProgress().set(id, newValue);
		// TODO add progress update message
		if(obj.getTargetAmount() <= newValue) {
			if(DataManager.progMsgObj && !obj.isHidden()) {
				player.sendMessage(Quester.LABEL + lang.MSG_OBJ_COMPLETED);
			}
			for(Qevent qv : q.getQevents()) {
				if(qv.getOccasion() == id) {
					qv.execute(player, plugin);
				}
			}
			if(checkAll) {
				try{
					complete(player, false, lang, false);
				} catch (QuesterException ignore) {}
			}
			profMan.saveProfiles();
		} 
	}
	
	
	// MESSENGER METHODS
	
	public void showQuest(CommandSender sender, String questName, QuesterLang lang) throws QuesterException {
		Quest qst = null;
		if(questName.isEmpty()) {
			qst = getPlayerQuest(sender.getName());
		}
		else {
			qst = getQuest(questName);
		}
		
		if(qst == null) {
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		if(!qst.hasFlag(QuestFlag.ACTIVE) || qst.hasFlag(QuestFlag.HIDDEN)) {
			if(!Util.permCheck(sender, DataManager.PERM_MODIFY, false, null)) {
				throw new QuestException(lang.ERROR_Q_NOT_EXIST);
			}
		}
		Player player = null;
		if(sender instanceof Player) {
			player = (Player) sender;
		}
		sender.sendMessage(ChatColor.BLUE + lang.INFO_NAME + ": " + ChatColor.GOLD + qst.getName());
		String string = qst.getDescription(sender.getName());
		if(!string.isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_DESCRIPTION + ": " + ChatColor.WHITE + string);
		}
		List<Condition> cons = qst.getConditions();
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
			List<Objective> objs = qst.getObjectives();
			sender.sendMessage(ChatColor.BLUE + lang.INFO_OBJECTIVES + ":");
			for(int i = 0; i < objs.size(); i++) {
				if(!objs.get(i).isHidden() && (objs.get(i).getPrerequisites().isEmpty() || !DataManager.ordOnlyCurrent)) {
					sender.sendMessage(ChatColor.WHITE + " - " + objs.get(i).inShow(0));
				}
			}
		}
	}
	
	public void showQuestInfo(CommandSender sender, QuesterLang lang) throws QuesterException {
		showQuestInfo(sender, profMan.getSelectedQuestID(sender.getName()), lang);
	}
	
	public void showQuestInfo(CommandSender sender, int id, QuesterLang lang) throws QuesterException {
		showQuestInfo(sender, getQuest(id), lang);
	}
	
	public void showQuestInfo(CommandSender sender, String questName, QuesterLang lang) throws QuesterException {
		showQuestInfo(sender, getQuest(questName), lang);
	}
	
	public void showQuestInfo(CommandSender sender, Quest qst, QuesterLang lang) throws QuesterException {
		if(qst == null) {
			throw new QuestException(lang.ERROR_Q_NOT_EXIST);
		}
		
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.INFO_QUEST_INFO, ChatColor.GOLD));
		
		sender.sendMessage(ChatColor.BLUE + lang.INFO_NAME + ": " + "[" + qst.getID() + "]" + ChatColor.GOLD + qst.getName());
		String string = qst.getDescription(sender.getName());
		if(!string.isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_DESCRIPTION + ": " + ChatColor.WHITE + string);
		}
		string = qst.getLocationString();
		if(!string.isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_LOCATION + ": " + ChatColor.WHITE + string);
		}
		string = QuestFlag.stringize(qst.getFlags());
		if(!string.isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_FLAGS + ": " + ChatColor.WHITE + string);
		}
		if(!qst.getWorlds().isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_WORLDS + ": " + ChatColor.WHITE + qst.getWorldNames());
		}
		int i;
		Map<Integer, Map<Integer, Qevent>> qmap = qst.getQeventMap();
		sender.sendMessage(ChatColor.BLUE + lang.INFO_EVENTS + ":");
		for(i=-1; i > -4; i--){
			if(qmap.get(i) != null) {
				sender.sendMessage(ChatColor.GOLD + " " + Qevent.parseOccasion(i) + ":");
				for(int j : qmap.get(i).keySet()) {
					sender.sendMessage("  <" + j + "> " + qmap.get(i).get(j).inInfo());
				}
			}
		}
		sender.sendMessage(ChatColor.BLUE + lang.INFO_CONDITIONS + ":");
		i = 0;
		for(Condition c: qst.getConditions()){
			sender.sendMessage(" [" + i + "] " + c.inInfo());
			i++;
			
		}
		sender.sendMessage(ChatColor.BLUE + lang.INFO_OBJECTIVES + ":");
		i = 0;
		for(Objective o: qst.getObjectives()){
			String color = o.isHidden() ? ChatColor.YELLOW+"" : "";
			sender.sendMessage(color + " [" + i + "] " + o.inInfo());
			if(qmap.get(i) != null) {
				for(int j : qmap.get(i).keySet()) {
					sender.sendMessage("  <" + j + "> " + qmap.get(i).get(j).inInfo());
				}
			}
			i++;
		}
	}
	
	public void showQuestList(CommandSender sender, QuesterLang lang) {
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.INFO_QUEST_LIST, ChatColor.GOLD));
		Player player = null;
		if(sender instanceof Player)
			player = (Player) sender;
		ChatColor color = ChatColor.RED;
		PlayerProfile prof = profMan.getProfile(player.getName());
		Quest q = null;
		for(int i : getQuestIds()){
			q = getQuest(i);
			if(q.hasFlag(QuestFlag.ACTIVE) && !q.hasFlag(QuestFlag.HIDDEN)) {
				if(player != null)
					try {
						if(prof.hasQuest(q.getName())) {
							color = ChatColor.YELLOW;
						}
						else if(prof.isCompleted(q.getName()) && !q.hasFlag(QuestFlag.REPEATABLE)) {
							color = ChatColor.GREEN;
						}
						else if(areConditionsMet(player, q.getName(), lang)) {
							color = ChatColor.BLUE;
						}
					} catch (Exception e){}
				sender.sendMessage(ChatColor.GOLD + "* " + color + q.getName());
			}
		}
	}
	
	public void showFullQuestList(CommandSender sender, QuesterLang lang) {
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.INFO_QUEST_LIST, ChatColor.GOLD));
		Quest q = null;
		for(int i : getQuestIds()){
			q = getQuest(i);
			ChatColor color = q.hasFlag(QuestFlag.ACTIVE) ? ChatColor.GREEN : ChatColor.RED;
			ChatColor color2 = q.hasFlag(QuestFlag.HIDDEN) ? ChatColor.YELLOW : ChatColor.BLUE;
			sender.sendMessage(color2 + "[" + q.getID() + "]" + color + q.getName());
		}
	}
	
	public void showProgress(Player player, QuesterLang lang) throws QuesterException {
		showProgress(player, -1, lang);
	}
	
	public void showProgress(Player player, int index, QuesterLang lang) throws QuesterException {
		Quest quest = null;
		List<Integer> progress = null;
		if(index < 0) {
			quest = getPlayerQuest(player.getName());
			progress = profMan.getProgress(player.getName());
		}
		else {
			quest = getPlayerQuest(player.getName(), index);
			progress = profMan.getProgress(player.getName(), index);
		}
		if(quest == null) {
			throw new QuestException(lang.ERROR_Q_NOT_ASSIGNED);
		}
		if(!quest.hasFlag(QuestFlag.HIDDENOBJS)) {
			player.sendMessage(lang.INFO_PROGRESS.replaceAll("%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
			List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				if(!objs.get(i).isHidden()) {
					if(objs.get(i).isComplete(progress.get(i))) {
						player.sendMessage(ChatColor.GREEN + " - " + lang.INFO_PROGRESS_COMPLETED);
					} else {
						boolean active = isObjectiveActive(player, i);
						if((active || !DataManager.ordOnlyCurrent)) {
							ChatColor col = active ? ChatColor.YELLOW : ChatColor.RED;
							player.sendMessage(col + " - " + objs.get(i).inShow(progress.get(i)));
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
		if(!profMan.hasProfile(name)) {
			sender.sendMessage(ChatColor.RED + lang.INFO_PROFILE_NOT_EXIST.replaceAll("%p", name));
			return;
		}
		PlayerProfile prof = profMan.getProfile(name);
		sender.sendMessage(ChatColor.BLUE + (sender.getName().equalsIgnoreCase(name) ? "Your quests: " : prof.getName() + "\'s quests: " ) 
				+ "(Limit: " + DataManager.maxQuests + ")");
		int current = prof.getActiveIndex();
		for(int i=0; i<prof.getQuestAmount(); i++) {
			sender.sendMessage("[" + i + "] " + (current == i ? ChatColor.GREEN : ChatColor.YELLOW) + getQuest(prof.getQuest(i)).getName());
		}
		
	}
	
	public static Inventory createInventory(Player player) {
		
		Inventory inv = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
		ItemStack[] contents = player.getInventory().getContents();
		
		for(int i = 0; i < contents.length; i++){
			if(contents[i] != null){
				inv.setItem(i, contents[i].clone());
			}
		}
		return inv;
	}
	
	public void saveQuests() {
		for(StorageKey subKey : questStorage.getKey("").getSubKeys()) {
			subKey.removeKey("");
		}
		for(Quest q : quests.values()) {
			StorageKey key = questStorage.getKey(String.valueOf(q.getID()));
			q.serialize(key);
		}
		questStorage.save();
	}
	
	public void loadQuests() {
		questStorage.load();
		List<Quest> onHold = new ArrayList<Quest>();
		int lastGeneric = 0;
		for(StorageKey questKey : questStorage.getKey("").getSubKeys()) {
			if(questKey.hasSubKeys()) {
				if(DataManager.debug) {
					Quester.log.info("Deserializing quest " + questKey.getName() + ".");
				}
				Quest quest = Quest.deserialize(questKey);
				if(quest == null) {
					Quester.log.severe("Quest " + questKey.getName() + " corrupted.");
					continue;
				}
				if(questNames.containsKey(quest.getName().toLowerCase())) { // duplicate name, generating new one
					quest.setName("");
					String name = "";
					while(questNames.containsKey(quest.getName()) || quest.getName().isEmpty()) {
						name = "generic" + lastGeneric;
						quest.setName(name);
						lastGeneric++;
					}
					Quester.log.severe("Duplicate quest name in quest " + questKey.getName() + " detected, generated new name '" + name + "'.");
				}
				if(quest.hasID()) {
					if(quests.get(quest.getID()) != null) { // duplicate ID
						Quester.log.severe("Duplicate quest ID in quest " + questKey.getName() + " detected, new ID will be assigned.");
						quest.setID(-1);
						onHold.add(quest);
					}
					else { // everything all right
						quests.put(quest.getID(), quest);
						if(quest.hasLocation()) {
							questLocations.put(quest.getID(), quest.getLocation());
						}
					}
				}
				else { // quest has default ID, new needs to be generated
					onHold.add(quest);
				}
				questNames.put(quest.getName().toLowerCase(), quest.getID());
				for(int i=0; i<quest.getObjectives().size(); i++) {
					if(quest.getObjective(i) == null) {
						Quester.log.info("Objective " + i + " is invalid.");
						quest.removeObjective(i);
						quest.removeFlag(QuestFlag.ACTIVE);
					}
				}
				for(int i=0; i<quest.getConditions().size(); i++) {
					if(quest.getCondition(i) == null) {
						Quester.log.info("Condition " + i + " is invalid.");
						quest.removeCondition(i);
					}
				}
				for(int i=0; i<quest.getQevents().size(); i++) {
					if(quest.getQevent(i) == null) {
						Quester.log.info("Event " + i + " is invalid.");
						quest.removeQevent(i);
					}
				}
			}
		}
		adjustQuestID(); // get ID ready
		for(Quest q : onHold) {
			assignQuestID(q);
			quests.put(q.getID(), q);
			questNames.put(q.getName().toLowerCase(), q.getID());
			if(q.hasLocation()) {
				questLocations.put(q.getID(), q.getLocation());
			}
		}
		if(DataManager.verbose) {
			Quester.log.info(quests.size() + " quests loaded.");
		}
	}
}
