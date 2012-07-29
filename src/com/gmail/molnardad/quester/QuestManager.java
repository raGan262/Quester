package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gmail.molnardad.quester.exceptions.*;
import com.gmail.molnardad.quester.objectives.ExpObjective;
import com.gmail.molnardad.quester.objectives.ItemObjective;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.rewards.ItemReward;
import com.gmail.molnardad.quester.rewards.Reward;
import com.gmail.molnardad.quester.utils.ExpManager;
import com.gmail.molnardad.quester.utils.Util;

import static com.gmail.molnardad.quester.QuestData.allQuests;
import static com.gmail.molnardad.quester.QuestData.currentQuests;
import static com.gmail.molnardad.quester.QuestData.objectiveProgress;
import static com.gmail.molnardad.quester.QuestData.selectedQuest;

public class QuestManager {
	
	// RETURNS int
	//  0 - True/Yes/OK
	//  1 - Quest does not exist
	//  2 - Quest already exists
	//  3 - Can't modify quest
	//  4 - Player already has quest
	//  5 - Player doesn't have any quests
	//  6 - No quest selected
	//  7 - Specified index does not exist
	//  8 - Objectives are not completed
	//  9 - Not enough space for reward in inventory
	// 10 - No active quests
	
	public static Random randGen = new Random();
	
	// QuestManager methods
	// - private part
	private boolean canModify(String questName) {
		return !getQuest(questName).isActive();
	}
	
	private Quest getQuest(String questName) {
		if(questName == null) return null;
		return allQuests.get(questName.toLowerCase());
	}
	
	private Quest getSelected(String name) {
		return allQuests.get(selectedQuest.get(name.toLowerCase()));
	}
	
	private Collection<Quest> getQuests() {
		return allQuests.values();
	}
	
	private boolean createProgress(String playerName) {
		boolean result = getProgress(playerName) == null;
		objectiveProgress.put(playerName.toLowerCase(), new ArrayList<Integer>());
		ArrayList<Objective> objectives = getPlayerQuest(playerName).getObjectives();
		for(@SuppressWarnings("unused") Objective o:objectives) {
			objectiveProgress.get(playerName.toLowerCase()).add(0);
		}
		return result;
	}
	
	private void removeProgress(String playerName) {
		objectiveProgress.remove(playerName.toLowerCase());
	}
	
	private ArrayList<Integer> getProgress(String playerName) {
		return objectiveProgress.get(playerName.toLowerCase());
	}
	
	private void assignQuest(String playerName, String questName) {
		currentQuests.put(playerName.toLowerCase(), questName.toLowerCase());
	}
	
	private void unassignQuest(String playerName) {
		currentQuests.remove(playerName.toLowerCase());
	}
	
	// - public part
	
	public void completeCheck(Player player) throws QuestCompletionException {
		
		Inventory inv = createInventory(player);
		// check objectives
		ArrayList<Objective> objs = getPlayerQuest(player.getName()).getObjectives();
		int totalExp = new ExpManager(player).getCurrentExp();
		for(int i = 0; i < objs.size(); i++) {
			if(objs.get(i).getType().equalsIgnoreCase("ITEM")){
				if(!((ItemObjective) objs.get(i)).takeInventory(inv)){
					throw new QuestCompletionException("completeCheck() 1", true);
				}
			} else if(objs.get(i).getType().equalsIgnoreCase("EXPERIENCE")){
				totalExp = ((ExpObjective) objs.get(i)).takeExp(totalExp);
				if(totalExp < 0) {
					throw new QuestCompletionException("completeCheck() 2", true);
				}
			} else if(!objs.get(i).isComplete(player, getProgress(player.getName()).get(i))){
				throw new QuestCompletionException("completeCheck() 3", true);
			}
		}
		//check item rewards
		ArrayList<Reward> rews = getPlayerQuest(player.getName()).getRewards();
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
	
	public boolean achievedTarget(Player player, int id) {
		String playerName = player.getName();
		return getPlayerQuest(playerName).getObjectives().get(id).isComplete(player, getProgress(playerName).get(id));
	}
	
	public boolean hasQuest(String playerName) {
		return currentQuests.containsKey(playerName.toLowerCase());
	}

	public Quest getPlayerQuest(String playerName) {
		return getQuest(currentQuests.get(playerName.toLowerCase()));
	}

	public boolean isQuest(String questName) {
		return allQuests.containsKey(questName.toLowerCase());
	}
	
	public String getSelectedName(String name) {
		return selectedQuest.get(name.toLowerCase());
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
		if(questName.equals(""))
			selectedQuest.remove(changer.toLowerCase());
		else
			selectedQuest.put(changer.toLowerCase(), questName.toLowerCase());
	}
	
	public void createQuest(String changer, String questName) throws QuestExistenceException {
		if(isQuest(questName)){
			throw new QuestExistenceException("createQuest()", true);
		}
		allQuests.put(questName.toLowerCase(), new Quest(questName));
		selectQuest(changer, questName);
	}
	
	public void removeQuest(String changer, String questName) throws QuestExistenceException, QuestModificationException {
		if(!isQuest(questName)) {
			throw new QuestExistenceException("removeQuest()", false);
		}
		if(!canModify(questName)) {
			throw new QuestModificationException("removeQuest()", false);
		}
		allQuests.remove(questName.toLowerCase());
	}
	
	public void activateQuest(String questName) throws QuestExistenceException {
		if(!isQuest(questName)){
			throw new QuestExistenceException("activateQuest()", false);
		}
		getQuest(questName).activate();
	}
	
	public void deactivateQuest(String questName) throws QuestExistenceException {
		if(!isQuest(questName)){
			throw new QuestExistenceException("deactivateQuest()", false);
		}
		getQuest(questName).deactivate();
		if(currentQuests.containsValue(questName.toLowerCase())) {
			for(String playerNameKey: currentQuests.keySet()) {
				if(currentQuests.get(playerNameKey).equalsIgnoreCase(questName)) {
					currentQuests.remove(playerNameKey);
					objectiveProgress.remove(playerNameKey);
					Player player = Bukkit.getServer().getPlayerExact(playerNameKey);
					if(player != null){
						player.sendMessage(Quester.LABEL + "Your current quest hes been deactivated.");
					}
				}
			}
		}
	}
	public void toggleQuest(CommandSender changer) throws QuestModificationException, QuestExistenceException {
		if(selectedQuest == null){
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
		allQuests.remove(getSelected(changer).getName().toLowerCase());
		getSelected(changer).setName(newName);
		allQuests.put(getSelected(changer).getName().toLowerCase(), getSelected(changer));
	}
	
	public void setQuestDescription(String changer, String newDesc) throws QuestModificationException {
		if(getSelected(changer) == null) {
			throw new QuestModificationException("setQuestDescription()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("setQuestDescription() 1", false);
		}
		getSelected(changer).setDescription(newDesc);
	}
	
	public void addQuestDescription(String changer, String descToAdd) throws QuestModificationException {
		if(getSelected(changer) == null) {
			throw new QuestModificationException("addQuestDescription()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("addQuestDescription() 1", false);
		}
		getSelected(changer).addDescription(descToAdd);
	}
	
	public void addQuestReward(String changer, Reward newReward) throws QuestModificationException {
		if(getSelected(changer) == null) {
			throw new QuestModificationException("addQuestReward()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("addQuestReward() 1", false);
		}
		getSelected(changer).addReward(newReward);
	}
	
	public void removeQuestReward(String changer, int id) throws QuestModificationException, QuestExistenceException {
		if(getSelected(changer) == null) {
			throw new QuestModificationException("removeQuestReward()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("removeQuestReward() 1", false);
		}
		if(!getSelected(changer).removeReward(id)){
			throw new QuestExistenceException("removeQuestReward()", false);
		}
	}
	
	public void addQuestObjective(String changer, Objective newObjective) throws QuestModificationException {
		if(getSelected(changer) == null) {
			throw new QuestModificationException("addQuestObjective()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("addQuestObjective() 1", false);
		}
		getSelected(changer).addObjective(newObjective);
	}
	
	public void removeQuestObjective(String changer, int id) throws QuestModificationException, QuestExistenceException {
		if(getSelected(changer) == null) {
			throw new QuestModificationException("removeQuestObjective()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("removeQuestObjective() 1", false);
		}
		if(!getSelected(changer).removeObjective(id)){
			throw new QuestExistenceException("removeQuestObjective()", false);
		}
	}
	
	// Quest management methods
	public void startQuest(Player player, String questName) throws QuestExistenceException, QuestAssignmentException {
		if(!isQuest(questName)){
			throw new QuestExistenceException("startQuest()", false);
		}
		if(hasQuest(player.getName())) {
			throw new QuestAssignmentException("startQuest()", true);
		}
		if(!isQuestActive(questName)) {
			throw new QuestExistenceException("startQuest() 1", false);
		}
		assignQuest(player.getName(), questName);
		createProgress(player.getName());
		player.sendMessage(Quester.LABEL + "You have started quest " + ChatColor.GOLD + getQuest(questName).getName());
		if(QuestData.verbose) {
			Quester.log.info(player.getName() + " started quest '" + getQuest(questName).getName() + "'.");
		}
	}
	
	public void startRandomQuest(Player player) throws QuestAssignmentException, QuestAvailabilityException, QuestExistenceException {
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
		String questName = getPlayerQuest(player.getName()).getName();
		player.sendMessage(Quester.LABEL + "Quest " + ChatColor.GOLD + questName + ChatColor.BLUE + " cancelled.");
		unassignQuest(player.getName());
		removeProgress(player.getName());
	}
	
	public void completeQuest(Player player) throws QuestAssignmentException, QuestCompletionException, QuestExistenceException {
		if(!hasQuest(player.getName())){
			throw new QuestAssignmentException("completeQuest()", false);
		}
		
		completeCheck(player);
		
		ArrayList<Objective> objs = getPlayerQuest(player.getName()).getObjectives();
		for(Objective o : objs) {
			o.finish(player);
		}
		ArrayList<Reward> rews = getPlayerQuest(player.getName()).getRewards();
		for(Reward r : rews) {
			r.giveReward(player);
		}
		String questName = getPlayerQuest(player.getName()).getName();
		player.sendMessage(Quester.LABEL + "Quest " + ChatColor.GOLD + getPlayerQuest(player.getName()).getName() + ChatColor.BLUE + " was completed by " + player.getName() + ".");
		unassignQuest(player.getName());
		removeProgress(player.getName());
		if(QuestData.onlyFirst) {
			deactivateQuest(questName);
		}
	}
	
	public void incProgress(Player player, int id) {
		String playerName = player.getName();
		int newValue = getProgress(playerName).get(id) + 1;
		getProgress(playerName).set(id, newValue);
		if(getPlayerQuest(playerName).getObjectives().get(id).getTargetAmount() == newValue) {
			player.sendMessage(Quester.LABEL + "You completed a quest objective.");
		} 
	}
	
	// Quest information printing
	public void showQuest(CommandSender sender, String questName) throws QuestExistenceException {
		Quest qst = getQuest(questName);
		if(qst == null)
			throw new QuestExistenceException("showQuest()", false);
		if(!qst.isActive()) {
			throw new QuestExistenceException("showQuest() 1", false);
		}
		sender.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.GOLD + qst.getName());
		sender.sendMessage(ChatColor.BLUE + "Description: " + ChatColor.WHITE + qst.getDescription());
		if(QuestData.showObjs) {
			sender.sendMessage(ChatColor.BLUE + "Objectives:");
			ArrayList<Objective> objs = getQuest(questName).getObjectives();
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
		sender.sendMessage(ChatColor.BLUE + "Active: " + color + String.valueOf(qst.isActive()));
		sender.sendMessage(ChatColor.BLUE + "Objectives:");
		int i;
		i = 0;
		for(Objective o: qst.getObjectives()){
			sender.sendMessage(" [" + String.valueOf(i) + "] " + o.toString());
			i++;
		}
		sender.sendMessage(ChatColor.BLUE + "Rewards:");
		i = 0;
		for(Reward r: qst.getRewards()){
			sender.sendMessage(" [" + String.valueOf(i) + "] " + r.toString());
			i++;
			
		}
	}
	
	public void showQuestList(CommandSender sender) {
		sender.sendMessage(Util.line(ChatColor.BLUE, "Quest list", ChatColor.GOLD));
		for(Quest q: getQuests()){
			if(q.isActive()) {
				sender.sendMessage(ChatColor.BLUE + "* " + ChatColor.GOLD + q.getName());
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
			ArrayList<Objective> objs = getPlayerQuest(player.getName()).getObjectives();
			ArrayList<Integer> progress = getProgress(player.getName());
			for(int i = 0; i < objs.size(); i++) {
				if(objs.get(i).isComplete(player, progress.get(i))) {
						player.sendMessage(ChatColor.GREEN + " - Completed");
				} else {
						player.sendMessage(ChatColor.RED + " - " + objs.get(i).progress(progress.get(i)));
				}
			} 
		} else {
			player.sendMessage(Quester.LABEL + "Quest progress hidden.");
		}
	}
	
	// Utility
	public Inventory createInventory(Player player) {
		
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
