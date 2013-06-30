package com.gmail.molnardad.quester.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.InstanceNotFoundException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.PlayerProfile;
import com.gmail.molnardad.quester.Quest;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.storage.ConfigStorage;
import com.gmail.molnardad.quester.storage.Storage;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.strings.QuesterLang;

public class ProfileManager {

	private Storage profileStorage = null;
	private QuestManager qMan = null;
	private LanguageManager langMan = null;

	private Map<String, PlayerProfile> profiles = new HashMap<String, PlayerProfile>();
	private Map<Integer, String> ranks = new HashMap<Integer, String>();
	private List<Integer> sortedRanks = new ArrayList<Integer>();
	
	
	public ProfileManager(Quester plugin) {
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
	
	public Collection<PlayerProfile> getProfiles() {
		return profiles.values();
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
	
	public boolean hasQuest(String playerName, String questName) {
		return getProfile(playerName).hasQuest(questName);
	}

	public Map<Integer, String> getRanks() {
		return ranks;
	}
	
	public void checkRank(PlayerProfile prof) {
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
	
	public int getSelectedQuestID(String name) {
		if(name == null) {
			return -1;
		}
		return getProfile(name).getSelected();
	}
	
	public List<Integer> getProgress(String playerName) {
		return getProfile(playerName).getProgress();
	}
	
	public List<Integer> getProgress(String playerName, int index) {
		return getProfile(playerName).getProgress(index);
	}
	
	public void assignQuest(String playerName, Quest quest) {
		getProfile(playerName).addQuest(quest.getName(), quest.getObjectives().size());
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
	
	public void selectQuest(String changer, int id) throws QuesterException {
		getProfile(changer).setSelected(id);
	}
	
	public void clearSelectedQuest(String playerName) {
		getProfile(playerName).setHolderID(-1);
	}
	
	public void selectHolder(String changer, int id) throws QuesterException {
		getProfile(changer).setHolderID(id);
	}
	
	public void clearSelectedHolder(String playerName) {
		getProfile(playerName).setHolderID(-1);
	}
	
	public boolean switchQuest(Player player, int id) {
		return getProfile(player.getName()).setQuest(id);
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
		sender.sendMessage(ChatColor.BLUE + lang.INFO_PROFILE_COMPLETED + ": " + ChatColor.WHITE + prof.getCompletedNames());
		
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
			prof = PlayerProfile.deserialize(subKey);
			if(prof != null) {
				if(!prof.getQuest().isEmpty()) {
					if(!qMan.isQuestActive(prof.getQuest()) || 
							(qMan.getObjectiveAmount(prof.getQuest()) != prof.getProgress().size())) {
						prof.unsetQuest();
						Quester.log.info("Incorrect quest info in profile: " + subKey.getName());
					}
				}
				checkRank(prof);
				profiles.put(prof.getName().toLowerCase(), prof);
			} else {
				Quester.log.info("Invalid key in profiles.yml: " + subKey.getName());
			}
		}
		saveProfiles();
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
