package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

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

public class QuestManager {
	
	public static Random randGen = new Random();
	
	// QuestManager methods
	// - private part
	private boolean canModify(String questName) {
		return !getQuest(questName).isActive();
	}
	
	private Quest getQuest(String questName) {
		if(questName == null || questName.isEmpty()) return null;
		return allQuests.get(questName.toLowerCase());
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
	
	private void assignQuest(String playerName, String questName) {
		int objs = getObjectiveAmount(questName);
		getProfile(playerName).setQuest(questName.toLowerCase(), objs);
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
				incProgress(player, i);
			} else {
				all = false;
				if(quest.isOrdered())
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

	public int getObjectiveAmount(String questName) {
		if(getQuest(questName) == null)
			return -1;
		return getQuest(questName).getObjectives().size();
	}
	
	public boolean isQuest(String questName) {
		if(questName == null) {
			return false;
		}
		return allQuests.containsKey(questName.toLowerCase());
	}
	
	public String getSelectedName(String playerName) {
		if(isQuest(getProfile(playerName).getSelected())) {
			return getProfile(playerName).getSelected();
		} else {
			return "";
		}
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
		if(getSelected(sender.getName()) == null)
			return false;
		return getSelected(sender.getName()).isActive();
	}
	
	public boolean isQuestActive(String questName) {
		if(getQuest(questName) == null)
			return false;
		return getQuest(questName).isActive();
	}
	
	public void selectQuest(String changer, String questName) throws QuestExistenceException {
		if(!isQuest(questName)) {
			throw new QuestExistenceException("selectQuest()", false);
		}
		getProfile(changer).setSelected(questName);
	}
	
	public void createQuest(String changer, String questName) throws QuestExistenceException {
		if(isQuest(questName)){
			throw new QuestExistenceException("createQuest()", true);
		}
		allQuests.put(questName.toLowerCase(), new Quest(questName));
		selectQuest(changer, questName);
		QuestData.saveQuests();
	}
	
	public void removeQuest(String changer, String questName) throws QuestExistenceException, QuestModificationException {
		if(!isQuest(questName)) {
			throw new QuestExistenceException("removeQuest()", false);
		}
		if(!canModify(questName)) {
			throw new QuestModificationException("removeQuest()", false);
		}
		allQuests.remove(questName.toLowerCase());
		Quester.questConfig.getConfig().set(questName.toLowerCase(), null);
		QuestData.saveQuests();
	}
	
	public void activateQuest(String questName) throws QuestExistenceException {
		if(!isQuest(questName)){
			throw new QuestExistenceException("activateQuest()", false);
		}
		getQuest(questName).activate();
		QuestData.saveQuests();
	}
	
	public void deactivateQuest(String questName) throws QuestExistenceException {
		if(!isQuest(questName)){
			throw new QuestExistenceException("deactivateQuest()", false);
		}
		getQuest(questName).deactivate();
		QuestData.saveQuests();
		for(PlayerProfile prof: profiles.values()) {
			if(prof.getQuest().equalsIgnoreCase(questName)) {
				prof.unsetQuest();
				Player player = Bukkit.getServer().getPlayerExact(prof.getName());
				if(player != null){
					player.sendMessage(Quester.LABEL + "Your current quest hes been deactivated.");
				}
			}
		}
	}
	
	public void toggleQuest(CommandSender changer) throws QuestModificationException, QuestExistenceException {
		if(getSelected(changer.getName()) == null){
			throw new QuestModificationException("toggleQuest()", true);
		}
		toggleQuest(getSelected(changer.getName()).getName());
	}
	
	public void toggleQuest(String questName) throws QuestExistenceException {
		if(!isQuest(questName)){	
			throw new QuestExistenceException("toggleQuest()", false);
		}
		if(getQuest(questName).isActive()){
			deactivateQuest(questName);
		} else {
			activateQuest(questName);
		}
	}
	
	public void setOrdered(String changer, boolean ordered) throws QuestModificationException {
		Quest quest = getSelected(changer);
		if(getSelected(changer) == null) {
			throw new QuestModificationException("setOrdered()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("setOrdered() 1", false);
		}
		quest.setOrdered(ordered);
		QuestData.saveQuests();
	}
	
	public void changeQuestName(String changer, String newName) throws QuestExistenceException, QuestModificationException {
		if(isQuest(newName)) {
			throw new QuestExistenceException("changeQuestName()", true);
		}
		if(getSelected(changer) == null) {
			throw new QuestModificationException("changeQuestName()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("changeQuestName() 1", false);
		}
		Quest qst = getSelected(changer);
		allQuests.remove(qst.getName().toLowerCase());
		qst.setName(newName);
		allQuests.put(qst.getName().toLowerCase(), qst);
		QuestData.saveQuests();
	}
	
	public void setQuestDescription(String changer, String newDesc) throws QuestModificationException {
		if(getSelected(changer) == null) {
			throw new QuestModificationException("setQuestDescription()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("setQuestDescription() 1", false);
		}
		getSelected(changer).setDescription(newDesc);
		QuestData.saveQuests();
	}
	
	public void addQuestDescription(String changer, String descToAdd) throws QuestModificationException {
		if(getSelected(changer) == null) {
			throw new QuestModificationException("addQuestDescription()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("addQuestDescription() 1", false);
		}
		getSelected(changer).addDescription(descToAdd);
		QuestData.saveQuests();
	}
	
	public void addQuestReward(String changer, Reward newReward) throws QuestModificationException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("addQuestReward()", true);
		}
		if(quest.isActive()) {
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
		if(quest.isActive()) {
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
		if(quest.isActive()) {
			throw new QuestModificationException("addQuestObjective() 1", false);
		}
		quest.addObjective(newObjective);
		QuestData.saveQuests();
	}
	
	public void removeQuestObjective(String changer, int id) throws QuestModificationException, QuestExistenceException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("removeQuestObjective()", true);
		}
		if(quest.isActive()) {
			throw new QuestModificationException("removeQuestObjective() 1", false);
		}
		if(!quest.removeObjective(id)){
			throw new QuestExistenceException("removeQuestObjective()", false);
		} else {
			QuestData.saveQuests();
		}
	}
	
	public void addQuestCondition(String changer, Condition newCondition) throws QuestModificationException {
		Quest quest = getSelected(changer);
		if(quest == null) {
			throw new QuestModificationException("addQuestCondition()", true);
		}
		if(quest.isActive()) {
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
		if(quest.isActive()) {
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
		if(quest.isActive()) {
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
		if(quest.isActive()) {
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
		if(!isQuest(questName)){
			throw new QuestExistenceException("startQuest()", false);
		}
		if(hasQuest(player.getName())) {
			throw new QuestAssignmentException("startQuest()", true);
		}
		if(!isQuestActive(questName)) {
			throw new QuestExistenceException("startQuest() 1", false);
		}
		if(!areConditionsMet(player, questName))
			throw new QuestConditionsException("startQuest()");
		assignQuest(player.getName(), questName);
		player.sendMessage(Quester.LABEL + "You have started quest " + ChatColor.GOLD + getQuest(questName).getName());
		if(QuestData.verbose) {
			Quester.log.info(player.getName() + " started quest '" + getQuest(questName).getName() + "'.");
		}
		for(Qevent qv : getQuest(questName).getQevents()) {
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
			if(q.isActive()) {
				aqsts.add(q);
			}
		}
		qsts = null;
		if(aqsts.isEmpty()) {
			throw new QuestAvailabilityException("startRandomQuest()", false);
		}
		int id = randGen.nextInt(aqsts.size());
		startQuest(player, aqsts.get(id).getName());
	}
	
	public void cancelQuest(Player player) throws QuestAssignmentException {
		if(!hasQuest(player.getName())) {
			throw new QuestAssignmentException("cancelQuest()", false);
		}
		Quest quest = getPlayerQuest(player.getName());
		player.sendMessage(Quester.LABEL + "Quest " + ChatColor.GOLD + quest.getName() + ChatColor.BLUE + " cancelled.");
		unassignQuest(player.getName());
		for(Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -2)
				qv.execute(player);
		}
		QuestData.saveProfiles();
	}
	
	public void complete(Player player) throws QuestAssignmentException, QuestCompletionException, QuestExistenceException, ObjectiveCompletionException {
		if(getPlayerQuest(player.getName()).isOrdered()) {
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
					incProgress(player, i);
					return;
				} else {
					throw new ObjectiveCompletionException();
				}
			}
			i++;
		}
		
		completeQuest(player);
	}
	
	public void completeQuest(Player player) throws QuestAssignmentException, QuestCompletionException, QuestExistenceException {
		if(!hasQuest(player.getName())){
			throw new QuestAssignmentException("completeQuest()", false);
		}
		
		completeCheck(player);
		
		List<Objective> objs = getPlayerQuest(player.getName()).getObjectives();
		for(Objective o : objs) {
			o.finish(player);
		}
		List<Reward> rews = getPlayerQuest(player.getName()).getRewards();
		for(Reward r : rews) {
			r.giveReward(player);
		}
		Quest quest= getPlayerQuest(player.getName());
		player.sendMessage(Quester.LABEL + "Quest " + ChatColor.GOLD + quest.getName() + ChatColor.BLUE + " was completed by " + player.getName() + ".");
		unassignQuest(player.getName());
		for(Qevent qv : quest.getQevents()) {
			if(qv.getOccasion() == -3)
				qv.execute(player);
		}
		getProfile(player.getName()).addCompleted(quest.getName());
		QuestData.saveProfiles();
		if(QuestData.onlyFirst) {
			deactivateQuest(quest.getName());
		}
	}
	
	public void incProgress(Player player, int id) {
		incProgress(player, id, 1);
	}
	
	public void incProgress(Player player, int id, int amount) {
		PlayerProfile prof = getProfile(player.getName());
		int newValue = prof.getProgress().get(id) + amount;
		Objective obj = getQuest(prof.getQuest()).getObjectives().get(id);
		prof.getProgress().set(id, newValue);
		if(obj.getTargetAmount() <= newValue) {
			player.sendMessage(Quester.LABEL + "You completed a quest objective.");
			for(Qevent qv : obj.getQevents()) {
				qv.execute(player);
			}
			if(areObjectivesCompleted(player)) {
				try{
					complete(player);
				} catch (QuesterException e) {
					player.sendMessage(e.message());
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
		String cmpltd = "";
		for(String s : prof.getCompleted()) {
			cmpltd = cmpltd + s + ", ";
		}
		cmpltd = cmpltd.substring(0, cmpltd.length()-2);
		sender.sendMessage(ChatColor.BLUE + "Completed quests: " + ChatColor.WHITE + cmpltd);
		
	}
	
	public void showQuest(CommandSender sender, String questName) throws QuestExistenceException {
		Quest qst = getQuest(questName);
		if(qst == null)
			throw new QuestExistenceException("showQuest()", false);
		if(!qst.isActive()) {
			throw new QuestExistenceException("showQuest() 1", false);
		}
		Player player = null;
		if(sender instanceof Player)
			player = (Player) sender;
		sender.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.GOLD + qst.getName());
		sender.sendMessage(ChatColor.BLUE + "Description: " + ChatColor.WHITE + qst.getDescription());
		String is = qst.isOrdered() ? "YES" : "NO";
		sender.sendMessage(ChatColor.BLUE + "Ordered: " + ChatColor.WHITE + is);
		sender.sendMessage(ChatColor.BLUE + "Conditions:");
		List<Condition> cons = getQuest(questName).getConditions();
		ChatColor color = ChatColor.WHITE;
		for(int i = 0; i < cons.size(); i++) {
			if(player != null)
				color = cons.get(i).isMet(player) ? ChatColor.GREEN : ChatColor.RED;
			sender.sendMessage(color + " - " + cons.get(i).show());
		}
		if(QuestData.showObjs) {
			Quest quest = getQuest(questName);
			List<Objective> objs = quest.getObjectives();
			if(QuestData.ordOnlyCurrent && quest.isOrdered()) {
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
	
	public void showQuestInfo(CommandSender sender, String questName) throws QuestExistenceException {
		Quest qst = getQuest(questName);
		if(qst == null)
			throw new QuestExistenceException("showQuestInfo()", false);
		
		sender.sendMessage(Util.line(ChatColor.BLUE, "Quest info", ChatColor.GOLD));
		
		sender.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.GOLD + qst.getName());
		sender.sendMessage(ChatColor.BLUE + "Description: " + ChatColor.WHITE + qst.getDescription());
		ChatColor color = qst.isActive() ? ChatColor.GREEN : ChatColor.RED;
		sender.sendMessage(ChatColor.BLUE + "Active: " + color + qst.isActive());
		color = qst.isOrdered() ? ChatColor.GREEN : ChatColor.YELLOW;
		sender.sendMessage(ChatColor.BLUE + "Ordered: " + color + qst.isOrdered());
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
			if(q.isActive()) {
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
			ChatColor color = q.isActive() ? ChatColor.GREEN : ChatColor.RED;
			sender.sendMessage(ChatColor.BLUE + "* " + color + q.getName());
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
			if(QuestData.ordOnlyCurrent && quest.isOrdered()) {
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
