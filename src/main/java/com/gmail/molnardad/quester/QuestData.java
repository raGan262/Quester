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
	
	private QuestManager qMan;
	
	// GENERAL
	public boolean verbose = false;
	public int saveInterval = 15;
	public boolean debug = true;
	// OBJECTIVE SECTION
	public boolean ordOnlyCurrent = true;
		// BREAK
	public boolean brkNoDrops = false;
	public boolean brkSubOnPlace = true;
		// COLLECT
	public boolean colRemPickup = true;
	public boolean colSubOnDrop = false;
	// QUEST SECTION
	public int maxQuests = 1;
		// MESSAGES
	public boolean progMsgStart = true;
	public boolean progMsgCancel = true;
	public boolean progMsgDone = true;
	public boolean progMsgObj = true;
	// COMMANDS
	public String displayedCmd = "/q";
	public String worldLabelThis = "this";
	public String locLabelHere = "here";
	public String locLabelPlayer = "player";
	public String locLabelBlock = "block";

	public static final String PERM_USE_NPC = "quester.use.npc";
	public static final String PERM_USE_SIGN = "quester.use.sign";
	public static final String PERM_USE_HELP = "quester.use.help";
	public static final String PERM_USE_LIST = "quester.use.list";
	public static final String PERM_USE_SHOW = "quester.use.show";
	public static final String PERM_USE_PROFILE = "quester.use.profile";
	public static final String PERM_USE_START_PICK = "quester.use.start.pick";
	public static final String PERM_USE_START_RANDOM = "quester.use.start.random";
	public static final String PERM_USE_DONE = "quester.use.done";
	public static final String PERM_USE_CANCEL = "quester.use.cancel";
	public static final String PERM_USE_PROGRESS = "quester.use.progress";
	public static final String PERM_USE_QUESTS = "quester.use.quests";
	public static final String PERM_USE_SWITCH = "quester.use.switch";
	public static final String PERM_MODIFY = "quester.modify";
	public static final String PERM_ADMIN = "quester.admin";


	public Map<String, Quest> allQuests = new HashMap<String, Quest>();
	public Map<Integer, String> questIds = new HashMap<Integer, String>();
	public Map<Integer, Location> questLocations = new HashMap<Integer, Location>();
	public Map<Integer, QuestHolder> holderIds = new HashMap<Integer, QuestHolder>();
	public Map<String, QuesterSign> signs = new HashMap<String, QuesterSign>();
	public Map<String, PlayerProfile> profiles = new HashMap<String, PlayerProfile>();
	public Map<Integer, String> ranks = new HashMap<Integer, String>();
	
	public List<Integer> sortedRanks = new ArrayList<Integer>();

	private int questID = -1;
	private int holderID = -1;
	
	// QUEST ID MANIPULATION
	
	public int getLastQuestID(){
		return questID;
	}
	
	public void assignQuestID(Quest qst) {
		questID++;
		qst.setID(questID);
	}
	
	public void setQuestID(int newID) {
		questID = newID;
	}
	
	public void adjustQuestID() {
		int newID = -1;
		for(int i : questIds.keySet()) {
			if(i > newID)
				newID = i;
		}
		questID = newID;
	}
	
	// HOLDER ID MANIPULATION
	
	public Map<Integer, QuestHolder> getHolders() {
		return holderIds;
	}
	
	public QuestHolder getHolder(int ID) {
		return holderIds.get(ID);
	}
	
	public int getLastHolderID(){
		return holderID;
	}
	
	public int getNewHolderID() {
		holderID++;
		return holderID;
	}
	
	public void setHolderID(int newID) {
		holderID = newID;
	}
	
	public void adjustHolderID() {
		int newID = -1;
		for(int i : holderIds.keySet()) {
			if(i > newID)
				newID = i;
		}
		holderID = newID;
	}
	
	// GENERAL
	
	public QuestData(Quester plugin) {
		qMan = plugin.getQuestManager();
	}
	
	public void wipeData(){
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
	
	void saveProfiles(){
		Quester.profileConfig.saveConfig();
	}

	void loadProfiles() {
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
						if(!qMan.isQuestActive(prof.getQuest()) || 
								(qMan.getObjectiveAmount(prof.getQuest()) != prof.getProgress().size())) {
							prof.unsetQuest();
							Quester.log.info("Incorrect quest info in profile: " + key);
						}
					}
					qMan.checkRank(prof);
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
	
	void saveQuests(){
		
		Quester.questConfig.saveConfig();
	}
	
	void loadQuests(){
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
				assignQuestID(q);
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
	
	void saveHolders(){
		Quester.holderConfig.saveConfig();
	}

	@SuppressWarnings("unchecked")
	void loadHolders() {
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
