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
import com.gmail.molnardad.quester.objectives.ExpObjective;
import com.gmail.molnardad.quester.objectives.ItemObjective;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.rewards.ItemReward;
import com.gmail.molnardad.quester.rewards.Reward;
import com.gmail.molnardad.quester.utils.ExpManager;
import com.gmail.molnardad.quester.utils.Util;

import static com.gmail.molnardad.quester.QuestData.allQuests;
import static com.gmail.molnardad.quester.QuestData.profiles;

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
		if(getSelected(changer) == null) {
			throw new QuestModificationException("addQuestReward()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("addQuestReward() 1", false);
		}
		getSelected(changer).addReward(newReward);
		QuestData.saveQuests();
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
		} else {
			QuestData.saveQuests();
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
		QuestData.saveQuests();
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
		} else {
			QuestData.saveQuests();
		}
	}
	
	public void addQuestCondition(String changer, Condition newCondition) throws QuestModificationException {
		if(getSelected(changer) == null) {
			throw new QuestModificationException("addQuestCondition()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("addQuestCondition() 1", false);
		}
		getSelected(changer).addCondition(newCondition);
		QuestData.saveQuests();
	}
	
	public void removeQuestCondition(String changer, int id) throws QuestModificationException, QuestExistenceException {
		if(getSelected(changer) == null) {
			throw new QuestModificationException("removeQuestCondition()", true);
		}
		if(!canModify(getSelected(changer).getName())) {
			throw new QuestModificationException("removeQuestCondition() 1", false);
		}
		if(!getSelected(changer).removeCondition(id)){
			throw new QuestExistenceException("removeQuestCondition()", false);
		} else {
			QuestData.saveQuests();
		}
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
		String questName = getPlayerQuest(player.getName()).getName();
		player.sendMessage(Quester.LABEL + "Quest " + ChatColor.GOLD + questName + ChatColor.BLUE + " cancelled.");
		unassignQuest(player.getName());
		QuestData.saveProfiles();
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
		getProfile(player.getName()).addCompleted(questName);
		QuestData.saveProfiles();
		if(QuestData.onlyFirst) {
			deactivateQuest(questName);
		}
	}
	
	public void incProgress(Player player, int id) {
		incProgress(player, id, 1);
	}
	
	public void incProgress(Player player, int id, int amount) {
		PlayerProfile prof = getProfile(player.getName());
		int newValue = prof.getProgress().get(id) + amount;
		prof.getProgress().set(id, newValue);
		if(getQuest(prof.getQuest()).getObjectives().get(id).getTargetAmount() <= newValue) {
			player.sendMessage(Quester.LABEL + "You completed a quest objective.");
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
		sender.sendMessage(ChatColor.BLUE + "Current quest: " + ChatColor.GOLD + prof.getQuest());
		sender.sendMessage(ChatColor.BLUE + "Quest points: " + ChatColor.GOLD + prof.getPoints());
		String cmpltd = "";
		for(String s : prof.getCompleted()) {
			cmpltd = cmpltd + s + "; ";
		}
		sender.sendMessage(ChatColor.BLUE + "Completed quests: " + ChatColor.GOLD + cmpltd);
		
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
		sender.sendMessage(ChatColor.BLUE + "Conditions:");
		ArrayList<Condition> cons = getQuest(questName).getConditions();
		ChatColor color = ChatColor.WHITE;
		for(int i = 0; i < cons.size(); i++) {
			if(player != null)
				color = cons.get(i).isMet(player) ? ChatColor.GREEN : ChatColor.RED;
			sender.sendMessage(color + " - " + cons.get(i).show());
		}
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
		sender.sendMessage(ChatColor.BLUE + "Conditions:");
		int i;
		i = 0;
		for(Condition c: qst.getConditions()){
			sender.sendMessage(" [" + String.valueOf(i) + "] " + c.toString());
			i++;
			
		}
		sender.sendMessage(ChatColor.BLUE + "Objectives:");
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
			List<Objective> objs = getPlayerQuest(player.getName()).getObjectives();
			List<Integer> progress = getProgress(player.getName());
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
