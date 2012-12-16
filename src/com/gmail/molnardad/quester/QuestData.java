package com.gmail.molnardad.quester;


import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class QuestData {
	
	// GENERAL
	public static boolean verbose = false;
	public static int saveInterval = 15;
	public static boolean debug = true;
	// OBJECTIVE SECTION
	public static boolean ordOnlyCurrent = true;
		// BREAK
	public static boolean brkNoDrops = false;
	public static boolean brkSubOnPlace = true;
		// COLLECT
	public static boolean colRemPickup = true;
	public static boolean colSubOnDrop = false;
	// QUEST SECTION
	public static int maxQuests = 1;
		// MESSAGES
	public static boolean progMsgStart = true;
	public static boolean progMsgCancel = true;
	public static boolean progMsgDone = true;
	public static boolean progMsgObj = true;
	// COMMANDS
	public static String displayedCmd = "/q";
	public static String worldLabelThis = "this";
	public static String locLabelHere = "here";
	public static String locLabelPlayer = "player";

	public static final String PERM_USE_NPC = "quester.use.npc";
	public static final String PERM_USE_SIGN = "quester.use.sign";
	public static final String PERM_USE_HELP = "quester.use.help";
	public static final String PERM_USE_LIST = "quester.use.list";
	public static final String PERM_USE_INFO = "quester.use.info";
	public static final String PERM_USE_PROFILE = "quester.use.profile";
	public static final String PERM_USE_START_PICK = "quester.use.start.pick";
	public static final String PERM_USE_START_RANDOM = "quester.use.start.random";
	public static final String PERM_USE_DONE = "quester.use.done";
	public static final String PERM_USE_CANCEL = "quester.use.cancel";
	public static final String PERM_USE_PROGRESS = "quester.use.progress";
	public static final String PERM_USE_QUESTS = "quester.use.quests";
	public static final String PERM_USE_SWITCH = "quester.use.switch";
	public static final String MODIFY_PERM = "quester.modify";
	public static final String ADMIN_PERM = "quester.admin";


	public static Map<String, Quest> allQuests = new HashMap<String, Quest>();
	public static Map<Integer, String> questIds = new HashMap<Integer, String>();
	public static Map<Integer, Location> questLocations = new HashMap<Integer, Location>();
	public static Map<Integer, QuestHolder> holderIds = new HashMap<Integer, QuestHolder>();
	public static Map<String, QuesterSign> signs = new HashMap<String, QuesterSign>();
	public static Map<String, PlayerProfile> profiles = new HashMap<String, PlayerProfile>();
	public static Map<Integer, String> ranks = new HashMap<Integer, String>();
	
	public static List<Integer> sortedRanks = new ArrayList<Integer>();

	private static int questID = -1;
	private static int holderID = -1;
	
	// QUEST ID MANIPULATION
	
	public static int getLastQuestID(){
		return questID;
	}
	
	public static void assignQuestID(Quest qst) {
		questID++;
		qst.setID(questID);
	}
	
	public static void setQuestID(int newID) {
		questID = newID;
	}
	
	public static void adjustQuestID() {
		int newID = -1;
		for(int i : questIds.keySet()) {
			if(i > newID)
				newID = i;
		}
		questID = newID;
	}
	
	// HOLDER ID MANIPULATION
	
	public static Map<Integer, QuestHolder> getHolders() {
		return holderIds;
	}
	
	public static QuestHolder getHolder(int ID) {
		return holderIds.get(ID);
	}
	
	public static int getLastHolderID(){
		return holderID;
	}
	
	public static int getNewHolderID() {
		holderID++;
		return holderID;
	}
	
	public static void setHolderID(int newID) {
		holderID = newID;
	}
	
	public static void adjustHolderID() {
		int newID = -1;
		for(int i : holderIds.keySet()) {
			if(i > newID)
				newID = i;
		}
		holderID = newID;
	}
	
	// GENERAL
	
	static void wipeData(){
		allQuests = null;
		questIds = null;
		questLocations = null;
		holderIds = null;
		signs = null;
		profiles = null;
		ranks = null;
		sortedRanks = null;
		questID = -1;
		holderID = -1;
	}
	
	// PROFILES MANIPULATION
	
	static void saveProfiles(){
		Quester.profileConfig.saveConfig();
	}

	static void loadProfiles() {
		try {
			YamlConfiguration config = Quester.profileConfig.getConfig();
			PlayerProfile prof;
			for(String key : config.getKeys(false)) {
				prof = null;
				if(config.isConfigurationSection(key)) {
					if(debug) {
						Quester.log.info("Deserializing profile: " + key);
					}
					prof = PlayerProfile.deserialize(config.getConfigurationSection(key));
				} 
				if(prof != null) {
					if(!prof.getQuest().isEmpty()) {
						if(!Quester.qMan.isQuestActive(prof.getQuest()) || 
								(Quester.qMan.getObjectiveAmount(prof.getQuest()) != prof.getProgress().size())) {
							prof.unsetQuest();
							Quester.log.info("Incorrect quest info in profile: " + key);
						}
					}
					Quester.qMan.checkRank(prof);
					profiles.put(prof.getName().toLowerCase(), prof);
				} else {
					Quester.log.info("Invalid key in profiles.yml: " + key);
				}
			}
			saveProfiles();
			if(verbose) {
				Quester.log.info(profiles.size() + " profiles loaded.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// QUESTS MANIPULATION
	
	static void saveQuests(){
		
		Quester.questConfig.saveConfig();
	}
	
	static void loadQuests(){
		YamlConfiguration config = Quester.questConfig.getConfig();
		for(String key : config.getKeys(false)) {
			if(config.isConfigurationSection(key)) {
				if(debug)
					Quester.log.info("Deserializing quest " + key + ".");
				Quest quest = Quest.deserialize(config.getConfigurationSection(key));
				if(quest == null) {
					Quester.log.severe("Quest " + key + " corrupted.");
					continue;
				}
				allQuests.put(quest.getName().toLowerCase(), quest);
				if(quest.hasID())
					questIds.put(quest.getID(), quest.getName().toLowerCase());
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
			}
		}
		adjustQuestID();
		for(Quest q : allQuests.values()) {
			if(!q.hasID()) {
				QuestData.assignQuestID(q);
				questIds.put(q.getID(), q.getName().toLowerCase());
			}
			if(q.hasLocation()) {
				questLocations.put(q.getID(), q.getLocation());
			}
		}
		if(verbose) {
			Quester.log.info(allQuests.size() + " quests loaded.");
		}
	}
	
	// SIGN MANIPULATION
	
	static void saveHolders(){
		Quester.holderConfig.saveConfig();
	}

	@SuppressWarnings("unchecked")
	static void loadHolders() {
		try {

			YamlConfiguration config = Quester.holderConfig.getConfig();
			
			// HOLDERS
			ConfigurationSection holders = config.getConfigurationSection("holders");
			QuestHolder qh;
			if(holders != null) {
				for(String key : holders.getKeys(false)) {
					try {
						int id = Integer.parseInt(key);
						qh = QuestHolder.deserialize(holders.getConfigurationSection(key));
						if(qh == null){
							throw new InvalidKeyException();
						}
						if(holderIds.get(id) != null)
							Quester.log.info("Duplicate holder index: '" + key + "'");
						holderIds.put(id, qh);
					} catch (NumberFormatException e) {
						Quester.log.info("Not numeric holder index: '" + key + "'");
					} catch (Exception e) {
						Quester.log.info("Invalid holder: '" + key + "'");
					}
				}
			}
			adjustHolderID();
			
			// SIGNS
			Object object = config.get("signs");
			if(object != null) {
				if(object instanceof List) {
					List<Map<String, Object>> list = (List<Map<String, Object>>) object;
					for(Map<String, Object> map : list) {
						QuesterSign sign = QuesterSign.deserialize(map);
						if(sign == null)
							continue;
						String s = sign.getLocation().getWorld().getName() + sign.getLocation().getBlockX() + sign.getLocation().getBlockY() + sign.getLocation().getBlockZ();
						signs.put(s, sign);
					}
				} else {
					Quester.log.info("Invalid sign list in holders.yml.");
				}
			}
			
			saveHolders();
			if(verbose) {
				Quester.log.info(holderIds.size() + " holders loaded.");
				Quester.log.info(signs.size() + " signs loaded.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
