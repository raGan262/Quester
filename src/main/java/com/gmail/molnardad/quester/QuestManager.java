package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.conditions.Condition;
import com.gmail.molnardad.quester.exceptions.*;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.qevents.Qevent;
import com.gmail.molnardad.quester.strings.QuesterStrings;
import com.gmail.molnardad.quester.utils.Util;

public class QuestManager {
	
	private QuesterStrings lang;
	private QuestData qData;
	private Quester plugin;
	
	public QuestManager(Quester plugin) {
		this.lang = plugin.getLanguageManager().getLang("english");
		this.qData = Quester.data;
		this.plugin = plugin;
	}

	private Quest getSelected(String name) {
		if(name == null) 
			return null;
		return getQuest(getProfile(name).getSelected());
	}
	
	private Collection<Quest> getQuests() {
		return qData.allQuests.values();
	}
	
	private List<Integer> getProgress(String playerName) {
		return getProfile(playerName).getProgress();
	}
	
	private List<Integer> getProgress(String playerName, int index) {
		return getProfile(playerName).getProgress(index);
	}
	
	private void assignQuest(String playerName, Quest quest) {
		getProfile(playerName).addQuest(quest.getName(), quest.getObjectives().size());
	}
	
	private void unassignQuest(String playerName) {
		unassignQuest(playerName, -1);
	}
	
	private void unassignQuest(String playerName, int index) {
		PlayerProfile prof = getProfile(playerName);
		if(index < 0) {
			prof.unsetQuest();
		}
		else {
			prof.unsetQuest(index);
		}
		prof.refreshActive();
	}
	
	private PlayerProfile createProfile(String playerName) {
		PlayerProfile prof = new PlayerProfile(playerName);
		qData.profiles.put(playerName.toLowerCase(), prof);
		return prof;
	}
	
	private void modifyCheck(Quest quest) throws QuesterException {
		if(quest == null) {
			throw new QuesterException(ExceptionType.Q_NOT_SELECTED);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuesterException(ExceptionType.Q_CANT_MODIFY);
		}
	}
	
	public Quest getQuest(String questName) {
		if(questName == null || questName.isEmpty()) return null;
		return qData.allQuests.get(questName.toLowerCase());
	}
	
	public Quest getQuest(int questID) {
		return getQuest(qData.questIds.get(questID));
	}
	
	public String getQuestNameByID(int id) {
		Quest q = getQuest(id);
		if(q == null)
			return "non-existant";
		else
			return q.getName();
	}
	
	public int getCurrentObjective(Player player) {
		List<Objective> objs = getPlayerQuest(player.getName()).getObjectives();
		PlayerProfile prof = getProfile(player.getName());
	
		for(int i=0; i<objs.size(); i++) {
			if(!objs.get(i).isComplete(player, prof.getProgress().get(i)))
				return i;
		}
		
		return -1;
	}
	
	public boolean isObjectiveActive(Player player, int id) {
		List<Objective> objs = getPlayerQuest(player.getName()).getObjectives();
		List<Integer> progress = getProgress(player.getName());
		Set<Integer> prereq = objs.get(id).getPrerequisites();
		if(objs.get(id).isComplete(player, progress.get(id))) {
			return false;
		}
		for(int i : prereq) {
			try {
				if(!objs.get(i).isComplete(player, progress.get(i))) {
					return false;
				}
			} catch (IndexOutOfBoundsException ignore) {
			}
		}
		return true;
	}
	
	public boolean areObjectivesCompleted(Player player) {
		
		List<Objective> objs = getPlayerQuest(player.getName()).getObjectives();
		PlayerProfile prof = getProfile(player.getName());
		boolean all = true;
		
		for(int i = 0; i < objs.size(); i++) {
			if(objs.get(i).isComplete(player, prof.getProgress().get(i)))
				continue;
			all = false;
		}
		
		return all;
	}
	
	public boolean areConditionsMet(Player player, String questName) throws QuesterException {
		return areConditionsMet(player, getQuest(questName));
	}
	
	public boolean areConditionsMet(Player player, Quest quest) throws QuesterException {
		if(quest == null)
			throw new QuesterException(ExceptionType.Q_NOT_EXIST);
		for(Condition c : quest.getConditions()) {
			if(!c.isMet(player))
				return false;
		}
		
		return true;
	}
	
	public PlayerProfile getProfile(String playerName) {
		if(playerName == null)
			return null;
		PlayerProfile prof = qData.profiles.get(playerName.toLowerCase());
		if(prof == null)
			prof = createProfile(playerName);
		return prof;
	}
	
	public boolean hasProfile(String playerName) {
		return qData.profiles.get(playerName.toLowerCase()) != null;
	}
	
	public boolean hasQuest(String playerName, String questName) {
		return getProfile(playerName).hasQuest(questName);
	}

	public Quest getPlayerQuest(String playerName) {
		return getQuest(getProfile(playerName).getQuest());
	}
	
	public Quest getPlayerQuest(String playerName, int index) {
		if(index < 0) {
			return null;
		}
		else {
			return getQuest(getProfile(playerName).getQuest(index));
		}
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
	
	public boolean isQuest(int questID) {
		return getQuest(questID) != null;
	}
	
	public boolean isQuest(String questName) {
		return getQuest(questName) != null;
	}
	
	public int getSelectedID(String playerName) {
		int id = getProfile(playerName).getSelected();
		if(getQuest(id) != null)
			return id;
		return -1;
	}
	
	public int getSelectedHolderID(String playerName) {
		int id = getProfile(playerName).getHolderID();
		if(getHolder(id) != null)
			return id;
		return -1;
	}

	public void checkRank(PlayerProfile prof) {
		int pts = prof.getPoints();
		String lastRank = "";
		for(int i : qData.sortedRanks) {
			if(pts >= i) {
				lastRank = qData.ranks.get(i);
			} 
			else 
				break;
		}
		prof.setRank(lastRank);
	}
	
	public boolean isQuestActive(CommandSender sender) {
		return isQuestActive(getSelected(sender.getName()));
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
	
	public void selectQuest(String changer, int id) throws QuesterException {
		Quest q = getQuest(id);
		if(q == null) {
			throw new QuesterException(ExceptionType.Q_NOT_EXIST);
		}
		getProfile(changer).setSelected(q.getID());
	}
	
	public void createQuest(String changer, String questName) throws QuesterException {
		if(isQuest(questName)){
			throw new QuesterException(ExceptionType.Q_EXIST);
		}
		Quest q = new Quest(questName);
		qData.assignQuestID(q);
		qData.allQuests.put(questName.toLowerCase(), q);
		qData.questIds.put(q.getID(), questName.toLowerCase());
		selectQuest(changer, q.getID());
		qData.saveQuests();
	}
	
	public void removeQuest(String changer, int questID) throws QuesterException {
		Quest q = getQuest(questID);
		modifyCheck(q);
		qData.questIds.remove(q.getID());
		qData.questLocations.remove(q.getID());
		qData.allQuests.remove(q.getName().toLowerCase());
		plugin.questConfig.getConfig().set(q.getName().toLowerCase(), null);
		qData.adjustQuestID();
		qData.saveQuests();
	}
	
	public void activateQuest(Quest q) {
		q.addFlag(QuestFlag.ACTIVE);
		qData.saveQuests();
	}
	
	public void deactivateQuest(Quest q) {
		q.removeFlag(QuestFlag.ACTIVE);
		qData.saveQuests();
		for(PlayerProfile prof: qData.profiles.values()) {
			while(prof.hasQuest(q.getName())) {
				prof.unsetQuest(q.getName());
				Player player = Bukkit.getServer().getPlayerExact(prof.getName());
				if(player != null){
					player.sendMessage(Quester.LABEL + lang.MSG_Q_DEACTIVATED);
				}
			}
			prof.refreshActive();
		}
		qData.saveProfiles();
	}
	
	public void toggleQuest(CommandSender changer) throws QuesterException {
		toggleQuest(getSelected(changer.getName()));
	}
	
	public void toggleQuest(int questID) throws QuesterException {
		toggleQuest(getQuest(questID));
	}
	
	public void toggleQuest(Quest q) throws QuesterException {
		if(q == null){	
			throw new QuesterException(ExceptionType.Q_NOT_EXIST);
		}
		if(q.hasFlag(QuestFlag.ACTIVE)){
			deactivateQuest(q);
		} else {
			activateQuest(q);
		}
	}
	
	public void changeQuestName(String changer, String newName) throws QuesterException {
		int questId = getSelectedID(changer);
		Quest quest = getQuest(questId);
		if(isQuest(newName)) {
			throw new QuesterException(ExceptionType.Q_EXIST);
		}
		modifyCheck(quest);
		
		qData.allQuests.remove(quest.getName().toLowerCase());
		plugin.questConfig.getConfig().set(quest.getName().toLowerCase(), null);
		quest.setName(newName);
		qData.allQuests.put(newName.toLowerCase(), quest);
		qData.questIds.remove(questId);
		qData.questIds.put(questId, newName.toLowerCase());
		qData.saveQuests();
	}
	
	public void setQuestDescription(String changer, String newDesc) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.setDescription(newDesc);
		qData.saveQuests();
	}
	
	public void addQuestDescription(String changer, String descToAdd) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.addDescription(descToAdd);
		qData.saveQuests();
	}
	
	public void setQuestLocation(String changer, Location loc, int range) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.setLocation(loc);
		quest.setRange(range);
		qData.questLocations.put(quest.getID(), loc);
		qData.saveQuests();
	}
	
	public void removeQuestLocation(String changer) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.setLocation(null);
		quest.setRange(1);
		qData.questLocations.remove(quest.getID());
		qData.saveQuests();
	}
	
	public void addQuestWorld(String changer, String worldName) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.addWorld(worldName);
		qData.saveQuests();
	}
	
	public void removeQuestWorld(String changer, String worldName) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.removeWorld(worldName.toLowerCase());
		qData.saveQuests();
	}
	
	public void addQuestFlag(String changer, QuestFlag[] flags) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		for(QuestFlag f : flags)
			quest.addFlag(f);
		qData.saveQuests();
	}
	
	public void removeQuestFlag(String changer, QuestFlag[] flags) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		for(QuestFlag f : flags)
			quest.removeFlag(f);
		qData.saveQuests();
	}
	
	public void addQuestObjective(String changer, Objective newObjective) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.addObjective(newObjective);
		qData.saveQuests();
	}
	
	public void removeQuestObjective(String changer, int id) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		if(!quest.removeObjective(id)){
			throw new QuesterException(ExceptionType.OBJ_NOT_EXIST);
		} else {
			qData.saveQuests();
		}
	}
	
	public void addObjectiveDescription(String changer, int id, String desc) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new QuesterException(ExceptionType.OBJ_NOT_EXIST);
		}
		objs.get(id).addDescription(desc);
		qData.saveQuests();
	}
	
	public void removeObjectiveDescription(String changer, int id) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new QuesterException(ExceptionType.OBJ_NOT_EXIST);
		}
		objs.get(id).removeDescription();
		qData.saveQuests();
	}
	
	public void swapQuestObjectives(String changer, int first, int second) throws QuesterException {
		Quest quest = getSelected(changer);
		if(first == second) {
			throw new QuesterException(ExceptionType.WHY);
		}
		modifyCheck(quest);
		
		if(quest.getObjective(first) == null || quest.getObjective(second) == null) {
			throw new QuesterException(ExceptionType.OBJ_NOT_EXIST);
		}
		List<Objective> objs = quest.getObjectives();
		Objective obj = objs.get(first);
		objs.set(first, objs.get(second));
		objs.set(second, obj);
		qData.saveQuests();
	}
	
	public void moveQuestObjective(String changer, int which, int where) throws QuesterException {
		Quest quest = getSelected(changer);
		if(which == where) {
			throw new QuesterException(ExceptionType.WHY);
		}
		modifyCheck(quest);
		
		if(quest.getObjective(which) == null || quest.getObjective(where) == null) {
			throw new QuesterException(lang.ERROR_CMD_ID_OUT_OF_BOUNDS);
		}
		Util.moveListUnit(quest.getObjectives(), which, where);
		qData.saveQuests();
	}
	
	public void addObjectivePrerequisites(String changer, int id, Set<Integer> prereq) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new QuesterException(ExceptionType.OBJ_NOT_EXIST);
		}
		for(int i : prereq) {
			if(i >= objs.size() || i < 0 || i != id) {
				objs.get(id).addPrerequisity(i);
			}
		}
		qData.saveQuests();
	}
	
	public void removeObjectivePrerequisites(String changer, int id, Set<Integer> prereq) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new QuesterException(ExceptionType.OBJ_NOT_EXIST);
		}
		for(int i : prereq) {
			objs.get(id).removePrerequisity(i);
		}
		qData.saveQuests();
	}
	
	public void addQuestCondition(String changer, Condition newCondition) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.addCondition(newCondition);
		qData.saveQuests();
	}
	
	public void removeQuestCondition(String changer, int id) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		if(!quest.removeCondition(id)){
			throw new QuesterException(ExceptionType.CON_NOT_EXIST);
		} else {
			qData.saveQuests();
		}
	}
	
	public void addConditionDescription(String changer, int id, String desc) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		List<Condition> cons = quest.getConditions();
		if(id >= cons.size() || id < 0) {
			throw new QuesterException(ExceptionType.CON_NOT_EXIST);
		}
		cons.get(id).addDescription(desc);
		qData.saveQuests();
	}
	
	public void removeConditionDescription(String changer, int id) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		List<Condition> cons = quest.getConditions();
		if(id >= cons.size() || id < 0) {
			throw new QuesterException(ExceptionType.CON_NOT_EXIST);
		}
		cons.get(id).removeDescription();
		qData.saveQuests();
	}
	
	public void addQevent(String changer, Qevent newQevent) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		int occasion = newQevent.getOccasion();
		if(occasion < -3 || occasion >= quest.getObjectives().size() ) {
			throw new QuesterException(ExceptionType.OCC_NOT_EXIST);
		}
		quest.addQevent(newQevent);
		qData.saveQuests();
	}
	
	public void removeQevent(String changer, int id) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		if(!quest.removeQevent(id)){
			throw new QuesterException(ExceptionType.EVT_NOT_EXIST);
		}
		qData.saveQuests();
	}
	
	public void selectHolder(String changer, int id) throws QuesterException {
		QuestHolder qh = getHolder(id);
		if(qh == null) {
			throw new QuesterException(ExceptionType.HOL_NOT_EXIST);
		}
		getProfile(changer).setHolderID(id);
	}
	
	public QuestHolder getHolder(int ID) {
		return qData.getHolder(ID);
	}
	
	public int createHolder(String name) {
		QuestHolder qh = new QuestHolder(name, this);
		int id = qData.getNewHolderID();
		qData.holderIds.put(id, qh);
		qData.saveHolders();
		return id;
	}
	
	public void removeHolder(int ID) {
		qData.holderIds.remove(ID);
		qData.saveHolders();
	}
	
	public void addHolderQuest(String changer, int questID) throws QuesterException {
		QuestHolder qh = getHolder(getProfile(changer).getHolderID());
		if(qh == null) {
			throw new QuesterException(ExceptionType.HOL_NOT_EXIST);
		}
		qh.addQuest(questID);
		qData.saveHolders();
	}
	
	public void removeHolderQuest(String changer, int questID) throws QuesterException {
		QuestHolder qh = getHolder(getProfile(changer).getHolderID());
		if(qh == null) {
			throw new QuesterException(ExceptionType.HOL_NOT_EXIST);
		}
		qh.removeQuest(questID);
		qData.saveHolders();
	}
	
	public void moveHolderQuest(String changer, int which, int where) throws QuesterException {
		QuestHolder qh = getHolder(getProfile(changer).getHolderID());
		if(qh == null) {
			throw new QuesterException(ExceptionType.HOL_NOT_SELECTED);
		}
		try {
			qh.moveQuest(which, where);
		}
		catch (IndexOutOfBoundsException e) {
			throw new QuesterException(lang.ERROR_CMD_ID_OUT_OF_BOUNDS);
		}
		qData.saveHolders();
	}
	
	public void startQuest(Player player, int questID, boolean command) throws QuesterException {
		Quest qst = getQuest(questID);
		startQuest(player, qst, command);
	}
	
	public void startQuest(Player player, String questName, boolean command) throws QuesterException {
		Quest qst = getQuest(questName);
		startQuest(player, qst, command);
	}
	
	public void startQuest(Player player, Quest qst, boolean command) throws QuesterException {
		String playerName = player.getName();
		if(qst == null){
			throw new QuesterException(ExceptionType.Q_NOT_EXIST);
		}
		PlayerProfile prof = getProfile(playerName);
		if(prof.hasQuest(qst.getName())) {
			throw new QuesterException(ExceptionType.Q_ASSIGNED);
		}
		if(prof.getQuestAmount() >= qData.maxQuests) {
			throw new QuesterException(ExceptionType.Q_MAX_AMOUNT);
		}
		if(!qst.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuesterException(ExceptionType.Q_NOT_EXIST);
		}
		if(command && qst.hasFlag(QuestFlag.HIDDEN))
			throw new QuesterException(ExceptionType.Q_NOT_CMD);
		if (!Util.permCheck(player, QuestData.PERM_ADMIN, false)){
			for(Condition con : qst.getConditions()) {
				if(!con.isMet(player)) {
					player.sendMessage(ChatColor.RED + con.show());
					return;
				}
			}
		}
		assignQuest(playerName, qst);
		if(qData.progMsgStart)
			player.sendMessage(Quester.LABEL + lang.MSG_Q_STARTED.replaceAll("%q", ChatColor.GOLD + qst.getName() + ChatColor.BLUE));
		String description = qst.getDescription(playerName);
		if(!description.isEmpty() && !qst.hasFlag(QuestFlag.NODESC))
			player.sendMessage(description);
		if(qData.verbose)
			Quester.log.info(playerName + " started quest '" + qst.getName() + "'.");
		for(Qevent qv : qst.getQevents()) {
			if(qv.getOccasion() == -1)
				qv.execute(player);
		}
		qData.saveProfiles();
	}
	
	public void startRandomQuest(Player player) throws QuesterException {
		Collection<Quest> qsts = getQuests();
		ArrayList<Quest> aqsts = new ArrayList<Quest>();
		for(Quest q : qsts) {
			if(q.hasFlag(QuestFlag.ACTIVE) 
					&& !q.hasFlag(QuestFlag.HIDDEN) 
					&& !hasQuest(player.getName(), q.getName()) 
					&& areConditionsMet(player, q)) {
				aqsts.add(q);
			}
		}
		qsts = null;
		if(aqsts.isEmpty()) {
			throw new QuesterException(ExceptionType.Q_NONE_ACTIVE);
		}
		int id = Quester.randGen.nextInt(aqsts.size());
		startQuest(player, aqsts.get(id).getName(), false);
	}
	public void cancelQuest(Player player, boolean command) throws QuesterException {
		cancelQuest(player, -1, command);
	}
	public void cancelQuest(Player player, int index, boolean command) throws QuesterException {
		Quest quest = null;
		if(index < 0) {
			quest = getPlayerQuest(player.getName());
		}
		else {
			quest = getPlayerQuest(player.getName(), index);
		}
		if(quest == null) {
			throw new QuesterException(ExceptionType.Q_NOT_ASSIGNED);
		}
		if(command && quest.hasFlag(QuestFlag.UNCANCELLABLE)) {
			throw new QuesterException(ExceptionType.Q_CANT_CANCEL);
		}
		unassignQuest(player.getName(), index);
		if(qData.progMsgCancel)
			player.sendMessage(Quester.LABEL + lang.MSG_Q_CANCELLED.replaceAll("%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
		if(qData.verbose)
			Quester.log.info(player.getName() + " cancelled quest '" + quest.getName() + "'.");
		for(Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -2)
				qv.execute(player);
		}
		qData.saveProfiles();
	}
	
	public void complete(Player player, boolean command) throws QuesterException {
		Quest quest = getPlayerQuest(player.getName());
		if(quest == null)
			throw new QuesterException(ExceptionType.Q_NOT_ASSIGNED);
		if(command && quest.hasFlag(QuestFlag.HIDDEN))
			throw new QuesterException(ExceptionType.Q_NOT_CMD);
    	if(!quest.allowedWorld(player.getWorld().getName()))
    		throw new QuesterException(ExceptionType.Q_BAD_WORLD);
    	
		completeObjective(player);
	}
	
	public void completeObjective(Player player) throws QuesterException {
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

		if(areObjectivesCompleted(player)) {
			completeQuest(player);
		}
		else if(!completed && i != 0) {
			throw new QuesterException(ExceptionType.OBJ_CANT_DO);
		}
	}
	
	public void completeQuest(Player player) throws QuesterException {
		Quest quest = getPlayerQuest(player.getName());
		
		unassignQuest(player.getName());
		if(qData.progMsgDone)
			player.sendMessage(Quester.LABEL + lang.MSG_Q_COMPLETED.replaceAll("%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
		if(qData.verbose)
			Quester.log.info(player.getName() + " completed quest '" + quest.getName() + "'.");
		for(Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -3)
				qv.execute(player);
		}
		getProfile(player.getName()).addCompleted(quest.getName(), (int) (System.currentTimeMillis() / 1000));
		qData.saveProfiles();
		if(quest.hasFlag(QuestFlag.ONLYFIRST)) {
			deactivateQuest(quest);
		}
	}
	
	public boolean switchQuest(Player player, int id) {
		return getProfile(player.getName()).setQuest(id);
	}
	
	public void incProgress(Player player, int id) {
		incProgress(player, id, 1, true);
	}
	
	public void incProgress(Player player, int id, boolean checkAll) {
		incProgress(player, id, 1, checkAll);
	}
	
	public void incProgress( Player player, int id, int amount) {
		incProgress(player, id, amount, true);
	}
	
	public void incProgress(final Player player,final int id, final int amount, final boolean checkAll) {
		PlayerProfile prof = getProfile(player.getName());
		int newValue = prof.getProgress().get(id) + amount;
		Quest q = getQuest(prof.getQuest());
		Objective obj = q.getObjectives().get(id);
		prof.getProgress().set(id, newValue);
		if(obj.getTargetAmount() <= newValue) {
			if(qData.progMsgObj)
				player.sendMessage(Quester.LABEL + lang.MSG_OBJ_COMPLETED);
			for(Qevent qv : q.getQevents()) {
				if(qv.getOccasion() == id) {
					qv.execute(player);
				}
			}
			if(checkAll) {
				try{
					complete(player, false);
				} catch (QuesterException ignore) {}
			}
			qData.saveProfiles();
		} 
	}
	
	public void showProfile(CommandSender sender) {
		showProfile(sender, sender.getName());
	}
	
	public void showProfile(CommandSender sender, String name) {
		if(!hasProfile(name)) {
			sender.sendMessage(ChatColor.RED + lang.INFO_PROFILE_NOT_EXIST.replaceAll("%p", name));
			return;
		}
		PlayerProfile prof = getProfile(name);
		sender.sendMessage(ChatColor.BLUE + lang.INFO_NAME + ": " + ChatColor.GOLD + prof.getName());
		sender.sendMessage(ChatColor.BLUE + lang.INFO_PROFILE_POINTS + ": " + ChatColor.WHITE + prof.getPoints());
		sender.sendMessage(ChatColor.BLUE + lang.INFO_PROFILE_RANK + ": " + ChatColor.GOLD + prof.getRank());
		sender.sendMessage(ChatColor.BLUE + lang.INFO_PROFILE_COMPLETED + ": " + ChatColor.WHITE + prof.getCompletedNames());
		
	}
	
	public void showQuest(CommandSender sender, String questName) throws QuesterException {
		Quest qst = getQuest(questName);
		if(qst == null)
			throw new QuesterException(ExceptionType.Q_NOT_EXIST);
		if(!qst.hasFlag(QuestFlag.ACTIVE) || qst.hasFlag(QuestFlag.HIDDEN)) {
			if(!Util.permCheck(sender, QuestData.PERM_MODIFY, false)) {
				throw new QuesterException(ExceptionType.Q_NOT_EXIST);
			}
		}
		Player player = null;
		if(sender instanceof Player) {
			player = (Player) sender;
		}
		sender.sendMessage(ChatColor.BLUE + lang.INFO_NAME + ": " + ChatColor.GOLD + qst.getName());
		sender.sendMessage(ChatColor.BLUE + lang.INFO_DESCRIPTION + ": " + ChatColor.WHITE + qst.getDescription(sender.getName()));
		List<Condition> cons = getQuest(questName).getConditions();
		if(!cons.isEmpty()) {
			sender.sendMessage(ChatColor.BLUE + lang.INFO_CONDITIONS + ":");
		}
		ChatColor color = ChatColor.WHITE;
		for(int i = 0; i < cons.size(); i++) {
			if(player != null) {
				color = cons.get(i).isMet(player) ? ChatColor.GREEN : ChatColor.RED;
			}
			sender.sendMessage(color + " - " + cons.get(i).show());
		}
		if(!qst.hasFlag(QuestFlag.HIDDENOBJS)) {
			List<Objective> objs = qst.getObjectives();
			sender.sendMessage(ChatColor.BLUE + lang.INFO_OBJECTIVES + ":");
			for(int i = 0; i < objs.size(); i++) {
				if(objs.get(i).getPrerequisites().isEmpty() || !qData.ordOnlyCurrent) {
					sender.sendMessage(ChatColor.WHITE + " - " + objs.get(i).progress(0));
				}
			}
		}
	}
	
	public void showQuestInfo(CommandSender sender) throws QuesterException {
		showQuestInfo(sender, getSelected(sender.getName()));
	}
	
	public void showQuestInfo(CommandSender sender, int id) throws QuesterException {
		showQuestInfo(sender, getQuest(id));
	}
	
	public void showQuestInfo(CommandSender sender, String questName) throws QuesterException {
		showQuestInfo(sender, getQuest(questName));
	}
	
	public void showQuestInfo(CommandSender sender, Quest qst) throws QuesterException {
		if(qst == null) {
			throw new QuesterException(ExceptionType.Q_NOT_EXIST);
		}
		
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.INFO_QUEST_INFO, ChatColor.GOLD));
		
		sender.sendMessage(ChatColor.BLUE + lang.INFO_NAME + ": " + "[" + qst.getID() + "]" + ChatColor.GOLD + qst.getName());
		sender.sendMessage(ChatColor.BLUE + lang.INFO_DESCRIPTION + ": " + ChatColor.WHITE + qst.getDescription("%p"));
		sender.sendMessage(ChatColor.BLUE + lang.INFO_LOCATION + ": " + ChatColor.WHITE + qst.getLocationString());
		sender.sendMessage(ChatColor.BLUE + lang.INFO_FLAGS + ": " + ChatColor.WHITE + QuestFlag.stringize(qst.getFlags()));
		String worlds = qst.getWorlds().isEmpty() ? "ANY" : qst.getWorldNames();
		sender.sendMessage(ChatColor.BLUE + lang.INFO_WORLDS + ": " + ChatColor.WHITE + worlds);
		int i;
		Map<Integer, Map<Integer, Qevent>> qmap = qst.getQeventMap();
		sender.sendMessage(ChatColor.BLUE + lang.INFO_EVENTS + ":");
		for(i=-1; i > -4; i--){
			if(qmap.get(i) != null) {
				sender.sendMessage(ChatColor.GOLD + " " + Qevent.parseOccasion(i) + ":");
				for(int j : qmap.get(i).keySet()) {
					sender.sendMessage("  <" + j + "> " + qmap.get(i).get(j).toString());
				}
			}
		}
		sender.sendMessage(ChatColor.BLUE + lang.INFO_CONDITIONS + ":");
		i = 0;
		for(Condition c: qst.getConditions()){
			sender.sendMessage(" [" + i + "] " + c.toString());
			i++;
			
		}
		sender.sendMessage(ChatColor.BLUE + lang.INFO_OBJECTIVES + ":");
		i = 0;
		for(Objective o: qst.getObjectives()){
			sender.sendMessage(" [" + i + "] " + o.toString());
			if(qmap.get(i) != null) {
				for(int j : qmap.get(i).keySet()) {
					sender.sendMessage("  <" + j + "> " + qmap.get(i).get(j).toString());
				}
			}
			i++;
		}
	}
	
	public void showQuestList(CommandSender sender) {
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.INFO_QUEST_LIST, ChatColor.GOLD));
		Player player = null;
		if(sender instanceof Player)
			player = (Player) sender;
		ChatColor color = ChatColor.BLUE;
		for(Quest q: getQuests()){
			if(q.hasFlag(QuestFlag.ACTIVE) && !q.hasFlag(QuestFlag.HIDDEN)) {
				if(player != null)
					try {
						color = areConditionsMet(player, q.getName()) ? ChatColor.BLUE : ChatColor.YELLOW;
					} catch (Exception e){}
				sender.sendMessage(ChatColor.GOLD + "* " + color + q.getName());
			}
		}
	}
	
	public void showFullQuestList(CommandSender sender) {
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.INFO_QUEST_LIST, ChatColor.GOLD));
		for(Quest q: getQuests()){
			ChatColor color = q.hasFlag(QuestFlag.ACTIVE) ? ChatColor.GREEN : ChatColor.RED;
			ChatColor color2 = q.hasFlag(QuestFlag.HIDDEN) ? ChatColor.YELLOW : ChatColor.BLUE;
			sender.sendMessage(color2 + "[" + q.getID() + "]" + color + q.getName());
		}
	}
	
	public void showProgress(Player player) throws QuesterException {
		showProgress(player, -1);
	}
	
	public void showProgress(Player player, int index) throws QuesterException {
		Quest quest = null;
		List<Integer> progress = null;
		if(index < 0) {
			quest = getPlayerQuest(player.getName());
			progress = getProgress(player.getName());
		}
		else {
			quest = getPlayerQuest(player.getName(), index);
			progress = getProgress(player.getName(), index);
		}
		if(quest == null) {
			throw new QuesterException(ExceptionType.Q_NOT_ASSIGNED);
		}
		if(!quest.hasFlag(QuestFlag.HIDDENOBJS)) {
			player.sendMessage(lang.INFO_PROGRESS.replaceAll("%q", ChatColor.GOLD + quest.getName() + ChatColor.BLUE));
			List<Objective> objs = quest.getObjectives();
			for(int i = 0; i < objs.size(); i++) {
				if(objs.get(i).isComplete(player, progress.get(i))) {
					player.sendMessage(ChatColor.GREEN + " - " + lang.INFO_PROGRESS_COMPLETED);
				} else {
					boolean active = isObjectiveActive(player, i);
					if(active || !qData.ordOnlyCurrent) {
						ChatColor col = active ? ChatColor.YELLOW : ChatColor.RED;
						player.sendMessage(col + " - " + objs.get(i).progress(progress.get(i)));
					}
				}
			}
		} else {
			player.sendMessage(Quester.LABEL + lang.INFO_PROGRESS_HIDDEN);
		}
	}

	public void showHolderList(CommandSender sender) {
		sender.sendMessage(Util.line(ChatColor.BLUE, lang.INFO_HOLDER_LIST, ChatColor.GOLD));
		for(int id : qData.getHolders().keySet()){
			sender.sendMessage(ChatColor.BLUE + "[" + id + "]" + ChatColor.GOLD + " " + getHolder(id).getName());
		}
	}

	public void showHolderInfo(CommandSender sender, int holderID) throws QuesterException {
		QuestHolder qh;
		int id;
		if(holderID < 0) {
			id = getProfile(sender.getName()).getHolderID();
		} else {
			id = holderID;
		}
		qh = getHolder(id);
		if(qh == null) {
			if(holderID < 0)
				throw new QuesterException(ExceptionType.HOL_NOT_SELECTED);
			else
				throw new QuesterException(ExceptionType.HOL_NOT_EXIST);
		}
		sender.sendMessage(ChatColor.GOLD + "Holder ID: " + ChatColor.RESET + id);
		qh.showQuestsModify(sender);
	}
	
	public void showTakenQuests(CommandSender sender) {
		showTakenQuests(sender, sender.getName());
	}
	
	public void showTakenQuests(CommandSender sender, String name) {
		if(!hasProfile(name)) {
			sender.sendMessage(ChatColor.RED + lang.INFO_PROFILE_NOT_EXIST.replaceAll("%p", name));
			return;
		}
		PlayerProfile prof = getProfile(name);
		sender.sendMessage(ChatColor.BLUE + (sender.getName().equalsIgnoreCase(name) ? "Your quests: " : prof.getName() + "\'s quests: " ) 
				+ "(Limit: " + qData.maxQuests + ")");
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
	
}
