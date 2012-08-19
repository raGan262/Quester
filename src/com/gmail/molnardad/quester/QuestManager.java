package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class QuestManager {
	
	// QuestManager methods
	// - private part
	
	private Quest getQuest(String questName) {
		if(questName == null || questName.isEmpty()) return null;
		return allQuests.get(questName.toLowerCase());
	}
	
	private Quest getQuest(int questID) {
		return getQuest(questIds.get(questID));
	}
	
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
	
	// - public part
	
	public void completeCheck(Player player) throws QuestCompletionException {
		
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
					throw new QuestCompletionException("completeCheck() 1", true);
			}
		}
		if(!all) {
			throw new QuestCompletionException("completeCheck() 2", true);
		}
		//check item rewards
		List<Reward> rews = getPlayerQuest(player.getName()).getRewards();
		for(Reward r : rews) {
			if(r.getType().equalsIgnoreCase("ITEM")) {
				ItemReward ir = (ItemReward) r;
				if(ir.checkInventory(inv)) {
					ir.giveInventory(inv);
				} else {
					throw new QuestCompletionException("completeCheck() 4", false);
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
	
	public boolean areConditionsMet(Player player, String questName) throws QuestExistenceException {
		Quest qst = getQuest(questName);
		if(qst == null)
			throw new QuestExistenceException("areConditionsMet()", false);
		
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
	
	public void selectQuest(String changer, int id) throws QuestExistenceException {
		Quest q = getQuest(id);
		if(q == null) {
			throw new QuestExistenceException("selectQuest()", false);
		}
		getProfile(changer).setSelected(q.getName().toLowerCase());
	}
	
	public void createQuest(String changer, String questName) throws QuestExistenceException {
		if(isQuest(questName)){
			throw new QuestExistenceException("createQuest()", true);
		}
		Quest q = new Quest(questName);
		QuestData.assignQuestID(q);
		allQuests.put(questName.toLowerCase(), q);
		questIds.put(q.getID(), questName.toLowerCase());
		selectQuest(changer, q.getID());
		QuestData.saveQuests();
	}
	
	public void removeQuest(String changer, int questID) throws QuestExistenceException, QuestModificationException {
		Quest q = getQuest(questID);
		if(q == null) {
			throw new QuestExistenceException("removeQuest()", false);
		}
		if(q.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("removeQuest()", false);
		}
		questIds.remove(q.getID());
		allQuests.remove(q.getName().toLowerCase());
		Quester.questConfig.getConfig().set(q.getName().toLowerCase(), null);
		QuestData.adjustQuestID();
		QuestData.saveQuests();
	}
	
	public void activateQuest(Quest q) throws QuestExistenceException {
		q.addFlag(QuestFlag.ACTIVE);
		QuestData.saveQuests();
	}
	
	public void deactivateQuest(Quest q) throws QuestExistenceException {
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
	
	public void toggleQuest(CommandSender changer) throws QuestModificationException, QuestExistenceException {
		toggleQuest(getSelected(changer.getName()));
	}
	
	public void toggleQuest(int questID) throws QuestExistenceException {
		toggleQuest(getQuest(questID));
	}
	
	public void toggleQuest(Quest q) throws QuestExistenceException {
		if(q == null){	
			throw new QuestExistenceException("toggleQuest()", false);
		}
		if(q.hasFlag(QuestFlag.ACTIVE)){
			deactivateQuest(q);
		} else {
			activateQuest(q);
		}
	}
	
	public void changeQuestName(String changer, String newName) throws QuestExistenceException, QuestModificationException {
		Quest quest = getSelected(changer);
		if(isQuest(newName)) {
			throw new QuestExistenceException("changeQuestName()", true);
		}
		if(quest == null) {
			throw new QuestModificationException("changeQuestName()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("changeQuestName() 1", false);
		}
		allQuests.remove(quest.getName().toLowerCase());
		Quester.questConfig.getConfig().set(quest.getName().toLowerCase(), null);
		quest.setName(newName);
		allQuests.put(quest.getName().toLowerCase(), quest);
		QuestData.saveQuests();
	}
	
	public void setQuestDescription(String changer, String newDesc) throws QuestModificationException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("setQuestDescription()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("setQuestDescription() 1", false);
		}
		quest.setDescription(newDesc);
		QuestData.saveQuests();
	}
	
	public void addQuestDescription(String changer, String descToAdd) throws QuestModificationException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("addQuestDescription()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("addQuestDescription() 1", false);
		}
		quest.addDescription(descToAdd);
		QuestData.saveQuests();
	}
	
	public void addQuestWorld(String changer, String worldName) throws QuestModificationException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("addQuestWorld()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("addQuestWorld() 1", false);
		}
		quest.addWorld(worldName);
		QuestData.saveQuests();
	}
	
	public void removeQuestWorld(String changer, String worldName) throws QuestModificationException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("removeQuestWorld()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("removeQuestWorld() 1", false);
		}
		quest.removeWorld(worldName.toLowerCase());
		QuestData.saveQuests();
	}
	
	public void addQuestFlag(String changer, QuestFlag[] flags) throws QuestModificationException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("addQuestDescription()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("addQuestDescription() 1", false);
		}
		for(QuestFlag f : flags)
			quest.addFlag(f);
		QuestData.saveQuests();
	}
	
	public void removeQuestFlag(String changer, QuestFlag[] flags) throws QuestModificationException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("addQuestWorld()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("addQuestWorld() 1", false);
		}
		for(QuestFlag f : flags)
			quest.removeFlag(f);
		QuestData.saveQuests();
	}
	
	public void addQuestReward(String changer, Reward newReward) throws QuestModificationException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("addQuestReward()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("addQuestReward() 1", false);
		}
		quest.addReward(newReward);
		QuestData.saveQuests();
	}
	
	public void removeQuestReward(String changer, int id) throws QuestModificationException, QuestExistenceException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("removeQuestReward()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("removeQuestReward() 1", false);
		}
		if(!quest.removeReward(id)){
			throw new QuestExistenceException("removeQuestReward()", false);
		} else {
			QuestData.saveQuests();
		}
	}
	
	public void addQuestObjective(String changer, Objective newObjective) throws QuestModificationException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("addQuestObjective()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("addQuestObjective() 1", false);
		}
		quest.addObjective(newObjective);
		QuestData.saveQuests();
	}
	
	public void removeQuestObjective(String changer, int id) throws QuestModificationException, QuestExistenceException, ObjectiveExistenceException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("removeQuestObjective()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("removeQuestObjective() 1", false);
		}
		if(!quest.removeObjective(id)){
			throw new ObjectiveExistenceException();
		} else {
			QuestData.saveQuests();
		}
	}
	
	public void addObjectiveDescription(String changer, int id, String desc) throws QuestModificationException, ObjectiveExistenceException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("addQuestObjective()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("addQuestObjective() 1", false);
		}
		List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveExistenceException();
		}
		objs.get(id).addDescription(desc);
		QuestData.saveQuests();
	}
	
	public void removeObjectiveDescription(String changer, int id) throws QuestModificationException, QuestExistenceException, ObjectiveExistenceException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("removeQuestObjective()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("removeQuestObjective() 1", false);
		}
		List<Objective> objs = quest.getObjectives();
		if(id >= objs.size() || id < 0) {
			throw new ObjectiveExistenceException();
		}
		objs.get(id).removeDescription();
		QuestData.saveQuests();
	}
	
	public void swapQuestObjectives (String changer, int first, int second) throws QuestModificationException, ObjectiveExistenceException, WhyException {
		Quest quest = getSelected(changer);
		if(first == second)
			throw new WhyException();
		if(quest == null)
			throw new QuestModificationException("removeQuestObjective()", true);
		if(quest.hasFlag(QuestFlag.ACTIVE))
			throw new QuestModificationException("removeQuestObjective() 1", false);
		if(quest.getObjective(first) == null || quest.getObjective(second) == null)
			throw new ObjectiveExistenceException();
		List<Objective> objs = quest.getObjectives();
		Objective obj = objs.get(first);
		objs.set(first, objs.get(second));
		objs.set(second, obj);
		QuestData.saveQuests();
	}
	
	public void addQuestCondition(String changer, Condition newCondition) throws QuestModificationException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("addQuestCondition()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("addQuestCondition() 1", false);
		}
		quest.addCondition(newCondition);
		QuestData.saveQuests();
	}
	
	public void removeQuestCondition(String changer, int id) throws QuestModificationException, QuestExistenceException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("removeQuestCondition()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("removeQuestCondition() 1", false);
		}
		if(!quest.removeCondition(id)){
			throw new QuestExistenceException("removeQuestCondition()", false);
		} else {
			QuestData.saveQuests();
		}
	}
	
	public void addQevent(String changer, Qevent newQevent) throws QuestModificationException, OccasionExistenceException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("addQevent()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("addQevent() 1", false);
		}
		int occasion = newQevent.getOccasion();
		if(occasion < -3 || occasion >= quest.getObjectives().size() ) {
			throw new OccasionExistenceException();
		}
		if(occasion < 0)
			quest.addQevent(newQevent);
		else
			quest.getObjective(occasion).addQevent(newQevent);
		QuestData.saveQuests();
	}
	
	public void removeQevent(String changer, int id, int objective) throws QuestModificationException, QuestExistenceException, OccasionExistenceException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("removeQevent()", true);
		}
		if(quest.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestModificationException("removeQevent() 1", false);
		}
		if(objective < 0) {
			if(!quest.removeQevent(id)){
				throw new QuestExistenceException("removeQevent()", false);
			}
		} else {
			Objective obj = quest.getObjective(objective);
			if(obj == null){
				throw new OccasionExistenceException();
			}
			obj.removeQevent(id);
		}
		QuestData.saveQuests();
	}
	
	// Quest management methods
	public void startQuest(Player player, String questName) throws QuestExistenceException, QuestAssignmentException, QuestConditionsException {
		Quest qst = getQuest(questName);
		String playerName = player.getName();
		if(qst == null){
			throw new QuestExistenceException("startQuest()", false);
		}
		if(hasQuest(playerName)) {
			throw new QuestAssignmentException("startQuest()", true);
		}
		if(!qst.hasFlag(QuestFlag.ACTIVE)) {
			throw new QuestExistenceException("startQuest() 1", false);
		}
		if(!areConditionsMet(player, questName))
			throw new QuestConditionsException("startQuest()");
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
	
	public void startRandomQuest(Player player) throws QuestAssignmentException, QuestAvailabilityException, QuestExistenceException, QuestConditionsException {
		if(hasQuest(player.getName())) {
			throw new QuestAssignmentException("startRandomQuest()", true);
		}
		Collection<Quest> qsts = getQuests();
		ArrayList<Quest> aqsts = new ArrayList<Quest>();
		for(Quest q : qsts) {
			if(q.hasFlag(QuestFlag.ACTIVE)) {
				aqsts.add(q);
			}
		}
		qsts = null;
		if(aqsts.isEmpty()) {
			throw new QuestAvailabilityException("startRandomQuest()", false);
		}
		int id = Quester.randGen.nextInt(aqsts.size());
		startQuest(player, aqsts.get(id).getName());
	}
	
	public void cancelQuest(Player player) throws QuestAssignmentException, QuestCancellationException {
		Quest quest = getPlayerQuest(player.getName());
		if(quest == null) {
			throw new QuestAssignmentException("cancelQuest()", false);
		}
		if(quest.hasFlag(QuestFlag.UNCANCELLABLE)) {
			throw new QuestCancellationException();
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
	
	public void complete(Player player) throws QuestAssignmentException, QuestCompletionException, QuestExistenceException, ObjectiveCompletionException, QuestWorldException {
		Quest quest = getPlayerQuest(player.getName());
		if(quest == null)
			throw new QuestAssignmentException("complete()", false);
    	if(!quest.allowedWorld(player.getWorld().getName()))
    		throw new QuestWorldException();
		if(quest.hasFlag(QuestFlag.ORDERED)) {
			completeObjective(player);
		} else {
			completeQuest(player);
		}
	}
	
	public void completeObjective(Player player) throws QuestAssignmentException, QuestCompletionException, QuestExistenceException, ObjectiveCompletionException {
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
					throw new ObjectiveCompletionException();
				}
			}
			i++;
		}
		
		completeQuest(player);
	}
	
	public void completeQuest(Player player) throws QuestCompletionException, QuestExistenceException {
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
		Objective obj = getQuest(prof.getQuest()).getObjectives().get(id);
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
						complete(player);
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
	
	public void showQuest(CommandSender sender, String questName) throws QuestExistenceException {
		Quest qst = getQuest(questName);
		if(qst == null)
			throw new QuestExistenceException("showQuest()", false);
		if(!qst.hasFlag(QuestFlag.ACTIVE)) {
			if(!Util.permCheck(sender, QuestData.MODIFY_PERM, false)) {
				throw new QuestExistenceException("showQuest() 1", false);
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
		if(QuestData.showObjs) {
			Quest quest = getQuest(questName);
			List<Objective> objs = quest.getObjectives();
			if(QuestData.ordOnlyCurrent && quest.hasFlag(QuestFlag.ORDERED)) {
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
	
	public void showQuestInfo(CommandSender sender) throws QuestModificationException, QuestExistenceException {
		if(getSelected(sender.getName()) == null){
			throw new QuestModificationException("showQuestInfo()", true);
		}
		showQuestInfo(sender, getSelected(sender.getName()).getName());
	}
	
	public void showQuestInfo(CommandSender sender, int id) throws QuestExistenceException {
		showQuestInfo(sender, getQuest(id));
	}
	
	public void showQuestInfo(CommandSender sender, String questName) throws QuestExistenceException {
		showQuestInfo(sender, getQuest(questName));
	}
	
	public void showQuestInfo(CommandSender sender, Quest qst) throws QuestExistenceException {
		if(qst == null)
			throw new QuestExistenceException("showQuestInfo()", false);
		
		sender.sendMessage(Util.line(ChatColor.BLUE, "Quest info", ChatColor.GOLD));
		
		sender.sendMessage(ChatColor.BLUE + "Name: " + "[" + qst.getID() + "]" + ChatColor.GOLD + qst.getName());
		sender.sendMessage(ChatColor.BLUE + "Description: " + ChatColor.WHITE + qst.getDescription());
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
			if(q.hasFlag(QuestFlag.ACTIVE)) {
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
			sender.sendMessage(ChatColor.BLUE + "[" + q.getID() + "]" + color + q.getName());
		}
	}
	
	public void showProgress(Player player) throws QuestAssignmentException {
		if(!hasQuest(player.getName())) {
			throw new QuestAssignmentException("showProgress()", false);
		}
		if(QuestData.showObjs) {
			player.sendMessage(ChatColor.GOLD + getPlayerQuest(player.getName()).getName() + ChatColor.BLUE + " progress:");
			Quest quest = getPlayerQuest(player.getName());
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
