package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.gmail.molnardad.quester.rewards.ItemReward;
import com.gmail.molnardad.quester.rewards.Reward;
import com.gmail.molnardad.quester.utils.Util;

import static com.gmail.molnardad.quester.QuestData.allQuests;
import static com.gmail.molnardad.quester.QuestData.profiles;
import static com.gmail.molnardad.quester.QuestData.questIds;
import static com.gmail.molnardad.quester.QuestData.questLocations;;

public class QuestManager {
	
	// QuestManager methods
	// - private part
	
	private Quest getSelected(String name) {
		if(name == null) 
			return null;
		return getQuest(getProfile(name).getSelected());
	}
	
	private Collection<Quest> getQuests() {
		return allQuests.values();
	}
	
	private List<Integer> getProgress(String playerName) {
		return getProfile(playerName).getProgress();
	}
	
	private void assignQuest(String playerName, Quest quest) {
		getProfile(playerName).setQuest(quest.getName().toLowerCase(), quest.getObjectives().size());
	}
	
	private void unassignQuest(String playerName) {
		getProfile(playerName).unsetQuest();
	}
	
	private PlayerProfile createProfile(String playerName) {
		PlayerProfile prof = new PlayerProfile(playerName);
		profiles.put(playerName.toLowerCase(), prof);
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
	
	// - public part
	
	public Quest getQuest(String questName) {
		if(questName == null || questName.isEmpty()) return null;
		return allQuests.get(questName.toLowerCase());
	}
	
	public Quest getQuest(int questID) {
		return getQuest(questIds.get(questID));
	}
	
	public void completeCheck(Player player) throws QuesterException {
		
		Inventory inv = createInventory(player);
		// check objectives
		Quest quest = getPlayerQuest(player.getName());
		List<Objective> objs = quest.getObjectives();
		PlayerProfile prof = getProfile(player.getName());
		boolean all = true;
		for(int i = 0; i < objs.size(); i++) {
			if(objs.get(i).isComplete(player, prof.getProgress().get(i)))
				continue;
			
			if(objs.get(i).tryToComplete(player)) {
				incProgress(player, i, false);
			} else {
				all = false;
				if(quest.hasFlag(QuestFlag.ORDERED))
					throw new QuesterException(ExceptionType.OBJ_CANT_DO);
			}
		}
		if(!all) {
			throw new QuesterException(ExceptionType.Q_NOT_COMPLETED);
		}
		//check item rewards
		List<Reward> rews = getPlayerQuest(player.getName()).getRewards();
		for(Reward r : rews) {
			if(r.getType().equalsIgnoreCase("ITEM")) {
				ItemReward ir = (ItemReward) r;
				if(ir.checkInventory(inv)) {
					ir.giveInventory(inv);
				} else {
					throw new QuesterException(ExceptionType.REW_CANT_DO);
				}
			}
		}
	}
	
	public String getQuestNameByID(int id) {
		Quest q = getQuest(id);
		if(q == null)
			return "-";
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
		Quest qst = getQuest(questName);
		if(qst == null)
			throw new QuesterException(ExceptionType.CON_NOT_MET);
		
		for(Condition c : qst.getConditions()) {
			if(!c.isMet(player))
				return false;
		}
		
		return true;
	}
	
	public PlayerProfile getProfile(String playerName) {
		if(playerName == null)
			return null;
		PlayerProfile prof = profiles.get(playerName.toLowerCase());
		if(prof == null)
			prof = createProfile(playerName);
		return prof;
	}
	
	public boolean hasProfile(String playerName) {
		return profiles.get(playerName.toLowerCase()) != null;
	}
	
	public boolean achievedTarget(Player player, int id) {
		String playerName = player.getName();
		return getPlayerQuest(playerName).getObjectives().get(id).isComplete(player, getProgress(playerName).get(id));
	}
	
	public boolean hasQuest(String playerName) {
		return !getProfile(playerName).getQuest().isEmpty();
	}

	public Quest getPlayerQuest(String playerName) {
		return getQuest(getProfile(playerName).getQuest());
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
		Quest q = getQuest(getProfile(playerName).getSelected());
		int sel = -1;
		if(q != null)
			sel = q.getID();
		return sel;
	}

	public void checkRank(PlayerProfile prof) {
		int pts = prof.getPoints();
		String lastRank = "";
		for(int i : QuestData.sortedRanks) {
			if(pts >= i) {
				lastRank = QuestData.ranks.get(i);
			} 
			else 
				break;
		}
		prof.setRank(lastRank);
	}
	
	// Quest modification methods
	
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
		getProfile(changer).setSelected(q.getName().toLowerCase());
	}
	
	public void createQuest(String changer, String questName) throws QuesterException {
		if(isQuest(questName)){
			throw new QuesterException(ExceptionType.Q_EXIST);
		}
		Quest q = new Quest(questName);
		QuestData.assignQuestID(q);
		allQuests.put(questName.toLowerCase(), q);
		questIds.put(q.getID(), questName.toLowerCase());
		selectQuest(changer, q.getID());
		QuestData.saveQuests();
	}
	
	public void removeQuest(String changer, int questID) throws QuesterException {
		Quest q = getQuest(questID);
		modifyCheck(q);
		questIds.remove(q.getID());
		questLocations.remove(q.getID());
		allQuests.remove(q.getName().toLowerCase());
		Quester.questConfig.getConfig().set(q.getName().toLowerCase(), null);
		QuestData.adjustQuestID();
		QuestData.saveQuests();
	}
	
	public void activateQuest(Quest q) {
		q.addFlag(QuestFlag.ACTIVE);
		QuestData.saveQuests();
	}
	
	public void deactivateQuest(Quest q) {
		q.removeFlag(QuestFlag.ACTIVE);
		QuestData.saveQuests();
		for(PlayerProfile prof: profiles.values()) {
			if(prof.getQuest().equalsIgnoreCase(q.getName())) {
				prof.unsetQuest();
				Player player = Bukkit.getServer().getPlayerExact(prof.getName());
				if(player != null){
					player.sendMessage(Quester.LABEL + "Your current quest hes been deactivated.");
				}
			}
		}
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
		Quest quest = getSelected(changer);
		if(isQuest(newName)) {
			throw new QuesterException(ExceptionType.Q_EXIST);
		}
		modifyCheck(quest);
		
		allQuests.remove(quest.getName().toLowerCase());
		Quester.questConfig.getConfig().set(quest.getName().toLowerCase(), null);
		quest.setName(newName);
		allQuests.put(quest.getName().toLowerCase(), quest);
		QuestData.saveQuests();
	}
	
	public void setQuestDescription(String changer, String newDesc) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.setDescription(newDesc);
		QuestData.saveQuests();
	}
	
	public void addQuestDescription(String changer, String descToAdd) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.addDescription(descToAdd);
		QuestData.saveQuests();
	}
	
	public void setQuestLocation(String changer, Location loc, int range) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.setLocation(loc);
		quest.setRange(range);
		questLocations.put(quest.getID(), loc);
		QuestData.saveQuests();
	}
	
	public void removeQuestLocation(String changer) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.setLocation(null);
		quest.setRange(1);
		questLocations.remove(quest.getID());
		QuestData.saveQuests();
	}
	
	public void addQuestWorld(String changer, String worldName) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.addWorld(worldName);
		QuestData.saveQuests();
	}
	
	public void removeQuestWorld(String changer, String worldName) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.removeWorld(worldName.toLowerCase());
		QuestData.saveQuests();
	}
	
	public void addQuestFlag(String changer, QuestFlag[] flags) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		for(QuestFlag f : flags)
			quest.addFlag(f);
		QuestData.saveQuests();
	}
	
	public void removeQuestFlag(String changer, QuestFlag[] flags) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		for(QuestFlag f : flags)
			quest.removeFlag(f);
		QuestData.saveQuests();
	}
	
	public void addQuestReward(String changer, Reward newReward) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.addReward(newReward);
		QuestData.saveQuests();
	}
	
	public void removeQuestReward(String changer, int id) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		if(!quest.removeReward(id)){
			throw new QuesterException(ExceptionType.REW_NOT_EXIST);
		} else {
			QuestData.saveQuests();
		}
	}
	
	public void addQuestObjective(String changer, Objective newObjective) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.addObjective(newObjective);
		QuestData.saveQuests();
	}
	
	public void removeQuestObjective(String changer, int id) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		if(!quest.removeObjective(id)){
			throw new QuesterException(ExceptionType.OBJ_NOT_EXIST);
		} else {
			QuestData.saveQuests();
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
		QuestData.saveQuests();
	}
	
	public void removeObjectiveDescription(String changer, int id) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new QuesterException(ExceptionType.OBJ_NOT_EXIST);
		}
		objs.get(id).removeDescription();
		QuestData.saveQuests();
	}
	
	public void swapQuestObjectives (String changer, int first, int second) throws QuesterException {
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
		QuestData.saveQuests();
	}
	
	public void addQuestCondition(String changer, Condition newCondition) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		quest.addCondition(newCondition);
		QuestData.saveQuests();
	}
	
	public void removeQuestCondition(String changer, int id) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		if(!quest.removeCondition(id)){
			throw new QuesterException(ExceptionType.CON_NOT_EXIST);
		} else {
			QuestData.saveQuests();
		}
	}
	
	public void addQevent(String changer, Qevent newQevent) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		int occasion = newQevent.getOccasion();
		if(occasion < -3 || occasion >= quest.getObjectives().size() ) {
			throw new QuesterException(ExceptionType.OCC_NOT_EXIST);
		}
		if(occasion < 0)
			quest.addQevent(newQevent);
		else
			quest.getObjective(occasion).addQevent(newQevent);
		QuestData.saveQuests();
	}
	
	public void removeQevent(String changer, int id, int objective) throws QuesterException {
		Quest quest = getSelected(changer);
		modifyCheck(quest);
		if(objective < 0) {
			if(!quest.removeQevent(id)){
				throw new QuesterException(ExceptionType.EVT_NOT_EXIST);
			}
		} else {
			Objective obj = quest.getObjective(objective);
			if(obj == null){
				throw new QuesterException(ExceptionType.OCC_NOT_EXIST);
			}
			obj.removeQevent(id);
		}
		QuestData.saveQuests();
	}
	
	// Quest management methods
	public void startQuest(Player player, String questName, boolean command) throws QuesterException {
		Quest qst = getQuest(questName);
		String playerName = player.getName();
		if(qst == null){
			throw new QuesterException(ExceptionType.Q_NOT_EXIST);
		}
		if(hasQuest(playerName)) {
			throw new QuesterException(ExceptionType.Q_ASSIGNED);
		}
		if(!qst.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuesterException(ExceptionType.Q_NOT_EXIST);
		}
		if(command && qst.hasFlag(QuestFlag.HIDDEN))
			throw new QuesterException(ExceptionType.Q_NOT_CMD);
		if(!areConditionsMet(player, questName))
			throw new QuesterException(ExceptionType.CON_NOT_MET);
		assignQuest(playerName, qst);
		if(QuestData.progMsgStart)
			player.sendMessage(Quester.LABEL + "You have started quest " + ChatColor.GOLD + qst.getName());
		if(QuestData.verbose)
			Quester.log.info(playerName + " started quest '" + qst.getName() + "'.");
		for(Qevent qv : qst.getQevents()) {
			if(qv.getOccasion() == -1)
				qv.execute(player);
		}
		QuestData.saveProfiles();
	}
	
	public void startRandomQuest(Player player) throws QuesterException {
		Collection<Quest> qsts = getQuests();
		ArrayList<Quest> aqsts = new ArrayList<Quest>();
		for(Quest q : qsts) {
			if(q.hasFlag(QuestFlag.ACTIVE) && !q.hasFlag(QuestFlag.HIDDEN)) {
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
	
	public void cancelQuest(Player player) throws QuesterException {
		Quest quest = getPlayerQuest(player.getName());
		if(quest == null) {
			throw new QuesterException(ExceptionType.Q_NOT_ASSIGNED);
		}
		if(quest.hasFlag(QuestFlag.UNCANCELLABLE)) {
			throw new QuesterException(ExceptionType.Q_CANT_CANCEL);
		}
		unassignQuest(player.getName());
		if(QuestData.progMsgCancel)
			player.sendMessage(Quester.LABEL + "Quest " + ChatColor.GOLD + quest.getName() + ChatColor.BLUE + " cancelled.");
		if(QuestData.verbose)
			Quester.log.info(player.getName() + " cancelled quest '" + quest.getName() + "'.");
		for(Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -2)
				qv.execute(player);
		}
		QuestData.saveProfiles();
	}
	
	public void complete(Player player, boolean command) throws QuesterException {
		Quest quest = getPlayerQuest(player.getName());
		if(quest == null)
			throw new QuesterException(ExceptionType.Q_NOT_ASSIGNED);
		if(command && quest.hasFlag(QuestFlag.HIDDEN))
			throw new QuesterException(ExceptionType.Q_NOT_CMD);
    	if(!quest.allowedWorld(player.getWorld().getName()))
    		throw new QuesterException(ExceptionType.Q_BAD_WORLD);
		if(quest.hasFlag(QuestFlag.ORDERED)) {
			completeObjective(player);
		} else {
			completeQuest(player);
		}
	}
	
	public void completeObjective(Player player) throws QuesterException {
		Quest quest = getPlayerQuest(player.getName());
		List<Objective> objs = quest.getObjectives();
		PlayerProfile prof = getProfile(player.getName());
		
		int i = 0;
		while(i<objs.size()) {
			if(!objs.get(i).isComplete(player, prof.getProgress().get(i))) {
				if(objs.get(i).tryToComplete(player)) {
					incProgress(player, i, false);
					return;
				} else {
					throw new QuesterException(ExceptionType.OBJ_CANT_DO);
				}
			}
			i++;
		}
		
		completeQuest(player);
	}
	
	public void completeQuest(Player player) throws QuesterException {
		Quest quest = getPlayerQuest(player.getName());
		
		completeCheck(player);
		
		List<Objective> objs = quest.getObjectives();
		for(Objective o : objs) {
			o.finish(player);
		}
		List<Reward> rews = quest.getRewards();
		for(Reward r : rews) {
			r.giveReward(player);
		}
		
		unassignQuest(player.getName());
		if(QuestData.progMsgDone)
			player.sendMessage(Quester.LABEL + "Quest " + ChatColor.GOLD + quest.getName() + ChatColor.BLUE + " completed.");
		if(QuestData.verbose)
			Quester.log.info(player.getName() + " completed quest '" + quest.getName() + "'.");
		for(Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -3)
				qv.execute(player);
		}
		getProfile(player.getName()).addCompleted(quest.getName());
		QuestData.saveProfiles();
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
			if(QuestData.progMsgObj)
				player.sendMessage(Quester.LABEL + "You completed a quest objective.");
			for(Qevent qv : obj.getQevents()) {
				qv.execute(player);
			}
			if(checkAll) {
				if(areObjectivesCompleted(player)) {
					try{
						complete(player, false);
					} catch (QuesterException e) {
						player.sendMessage(e.message());
					}
				}
			}
			QuestData.saveProfiles();
		} 
	}
	
	// Quest information printing
	public void showProfile(CommandSender sender) {
		showProfile(sender, sender.getName());
	}
	
	public void showProfile(CommandSender sender, String name) {
		if(!hasProfile(name)) {
			sender.sendMessage(ChatColor.RED + name + " does not have profile.");
			return;
		}
		PlayerProfile prof = getProfile(name);
		sender.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.GOLD + prof.getName());
		sender.sendMessage(ChatColor.BLUE + "Quest points: " + ChatColor.WHITE + prof.getPoints());
		sender.sendMessage(ChatColor.BLUE + "Quest rank: " + ChatColor.GOLD + prof.getRank());
		sender.sendMessage(ChatColor.BLUE + "Current quest: " + ChatColor.WHITE + prof.getQuest());
		sender.sendMessage(ChatColor.BLUE + "Completed quests: " + ChatColor.WHITE + prof.getCompletedNames());
		
	}
	
	public void showQuest(CommandSender sender, String questName) throws QuesterException {
		Quest qst = getQuest(questName);
		if(qst == null)
			throw new QuesterException(ExceptionType.Q_NOT_EXIST);
		if(!qst.hasFlag(QuestFlag.ACTIVE) || qst.hasFlag(QuestFlag.HIDDEN)) {
			if(!Util.permCheck(sender, QuestData.MODIFY_PERM, false)) {
				throw new QuesterException(ExceptionType.Q_NOT_EXIST);
			}
		}
		Player player = null;
		if(sender instanceof Player)
			player = (Player) sender;
		sender.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.GOLD + qst.getName());
		sender.sendMessage(ChatColor.BLUE + "Description: " + ChatColor.WHITE + qst.getDescription());
		List<Condition> cons = getQuest(questName).getConditions();
		if(!cons.isEmpty())
			sender.sendMessage(ChatColor.BLUE + "Conditions:");
		ChatColor color = ChatColor.WHITE;
		for(int i = 0; i < cons.size(); i++) {
			if(player != null)
				color = cons.get(i).isMet(player) ? ChatColor.GREEN : ChatColor.RED;
			sender.sendMessage(color + " - " + cons.get(i).show());
		}
		if(!qst.hasFlag(QuestFlag.HIDDENOBJS)) {
			List<Objective> objs = qst.getObjectives();
			if(QuestData.ordOnlyCurrent && qst.hasFlag(QuestFlag.ORDERED)) {
				sender.sendMessage(ChatColor.BLUE + "First objective:");
				if(objs.get(0) != null)
					sender.sendMessage(ChatColor.WHITE + " - " + objs.get(0).progress(0));
				return;
			}
			sender.sendMessage(ChatColor.BLUE + "Objectives:");
			for(int i = 0; i < objs.size(); i++) {
				sender.sendMessage(ChatColor.WHITE + " - " + objs.get(i).progress(0));
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
		if(qst == null)
			throw new QuesterException(ExceptionType.Q_NOT_EXIST);
		
		sender.sendMessage(Util.line(ChatColor.BLUE, "Quest info", ChatColor.GOLD));
		
		sender.sendMessage(ChatColor.BLUE + "Name: " + "[" + qst.getID() + "]" + ChatColor.GOLD + qst.getName());
		sender.sendMessage(ChatColor.BLUE + "Description: " + ChatColor.WHITE + qst.getDescription());
		sender.sendMessage(ChatColor.BLUE + "Location: " + ChatColor.WHITE + qst.getLocationString());
		sender.sendMessage(ChatColor.BLUE + "Flags: " + ChatColor.WHITE + QuestFlag.stringize(qst.getFlags()));
		String worlds = qst.getWorlds().isEmpty() ? "ANY" : qst.getWorldNames();
		sender.sendMessage(ChatColor.BLUE + "Worlds: " + ChatColor.WHITE + worlds);
		int i;
		sender.sendMessage(ChatColor.BLUE + "Events:");
		i = 0;
		for(Qevent e: qst.getQevents()){
			sender.sendMessage(" [" + i + "] " + e.toString());
			i++;
			
		}
		sender.sendMessage(ChatColor.BLUE + "Conditions:");
		i = 0;
		for(Condition c: qst.getConditions()){
			sender.sendMessage(" [" + i + "] " + c.toString());
			i++;
			
		}
		sender.sendMessage(ChatColor.BLUE + "Objectives:");
		i = 0;
		for(Objective o: qst.getObjectives()){
			sender.sendMessage(" [" + i + "] " + o.toString());
			i++;
		}
		sender.sendMessage(ChatColor.BLUE + "Rewards:");
		i = 0;
		for(Reward r: qst.getRewards()){
			sender.sendMessage(" [" + i + "] " + r.toString());
			i++;
			
		}
	}
	
	public void showQuestList(CommandSender sender) {
		sender.sendMessage(Util.line(ChatColor.BLUE, "Quest list", ChatColor.GOLD));
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
		sender.sendMessage(Util.line(ChatColor.BLUE, "Quest list", ChatColor.GOLD));
		for(Quest q: getQuests()){
			ChatColor color = q.hasFlag(QuestFlag.ACTIVE) ? ChatColor.GREEN : ChatColor.RED;
			ChatColor color2 = q.hasFlag(QuestFlag.HIDDEN) ? ChatColor.YELLOW : ChatColor.BLUE;
			sender.sendMessage(color2 + "[" + q.getID() + "]" + color + q.getName());
		}
	}
	
	public void showProgress(Player player) throws QuesterException {
		Quest quest = getPlayerQuest(player.getName());
		if(quest == null) {
			throw new QuesterException(ExceptionType.Q_NOT_ASSIGNED);
		}
		if(!quest.hasFlag(QuestFlag.HIDDENOBJS)) {
			player.sendMessage(ChatColor.GOLD + getPlayerQuest(player.getName()).getName() + ChatColor.BLUE + " progress:");
			List<Objective> objs = quest.getObjectives();
			List<Integer> progress = getProgress(player.getName());
			if(QuestData.ordOnlyCurrent && quest.hasFlag(QuestFlag.ORDERED)) {
				int curr = getCurrentObjective(player);
				player.sendMessage(ChatColor.GOLD + " - " + objs.get(curr).progress(progress.get(curr)));
			} else {
				for(int i = 0; i < objs.size(); i++) {
					if(objs.get(i).isComplete(player, progress.get(i))) {
							player.sendMessage(ChatColor.GREEN + " - Completed");
					} else {
							player.sendMessage(ChatColor.RED + " - " + objs.get(i).progress(progress.get(i)));
					}
				} 
			}
		} else {
			player.sendMessage(Quester.LABEL + "Quest progress hidden.");
		}
	}
	
	// Utility
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
