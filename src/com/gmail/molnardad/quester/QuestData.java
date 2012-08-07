package com.gmail.molnardad.quester;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

public class QuestData {
	
	public static boolean verbose = false;
	public static boolean brkNoDrops = false;
	public static boolean brkSubOnPlace = true;
	public static boolean onlyFirst = false;
	public static int saveInterval = 15;
	public static boolean debug = true;
	public static boolean showObjs = true;
	public static boolean disUseCmds = false;
	public static boolean colRemPickup = true;
	public static boolean colSubOnDrop = false;
	public static boolean ordOnlyCurrent = true;

	public static final String USE_PERM = "quester.use";
	public static final String MODIFY_PERM = "quester.modify";
	public static final String ADMIN_PERM = "quester.admin";


	public static Map<String, Quest> allQuests = new HashMap<String, Quest>();
	public static Map<String, PlayerProfile> profiles = new HashMap<String, PlayerProfile>();
	public static Map<Integer, String> ranks = new HashMap<Integer, String>();
	
	public static List<Integer> sortedRanks = new ArrayList<Integer>();

	
	static void wipeData(){
		allQuests = null;
		profiles = null;
	}
	
	static void saveProfiles(){
		Quester.profileConfig.saveConfig();
	}

	static void loadProfiles() {
		try {
			YamlConfiguration config = Quester.profileConfig.getConfig();
			for(String key : config.getKeys(false)) {
				if(config.get(key) != null) {
					if(config.get(key) instanceof PlayerProfile) {
						PlayerProfile prof = (PlayerProfile) config.get(key);
						if(!prof.getQuest().isEmpty()) {
							if(Quester.qMan.isQuestActive(prof.getQuest())) {
								if(Quester.qMan.getObjectiveAmount(prof.getQuest()) != prof.getProgress().size()) {
									prof.unsetQuest();
									if(verbose) {
										Quester.log.info("Invalid progress: " + key);
									}
								}
							} else {
								prof.unsetQuest();
								if(verbose) {
									Quester.log.info("Invalid progress: " + key);
								}
							}
						}
						Quester.qMan.checkRank(prof);
						profiles.put(prof.getName().toLowerCase(), prof);
					} else {
						if(verbose) {
							Quester.log.info("Invalid key in profiles.yml: " + key);
						}
					}
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
	
	static void saveQuests(){
		
		Quester.questConfig.saveConfig();
	}
	
	static void loadQuests(){
		try {
			YamlConfiguration config = Quester.questConfig.getConfig();
			for(String key : config.getKeys(false)) {
				if(config.get(key) != null) {
					if(config.get(key) instanceof Quest) {
						Quest quest = (Quest) config.get(key);
						allQuests.put(quest.getName().toLowerCase(), quest);
						for(int i=0; i<quest.getObjectives().size(); i++) {
							if(quest.getObjective(i) == null) {
								Quester.log.info("Objective " + i + " is invalid.");
								quest.removeObjective(i);
								quest.deactivate();
							}
						}
						for(int i=0; i<quest.getRewards().size(); i++) {
							if(quest.getReward(i) == null) {
								Quester.log.info("Reward " + i + " is invalid.");	
								quest.removeReward(i);
							}
						}
						for(int i=0; i<quest.getConditions().size(); i++) {
							if(quest.getCondition(i) == null) {
								Quester.log.info("Condition " + i + " is invalid.");
								quest.removeCondition(i);
							}
						}
					} else {
						if(verbose) {
							Quester.log.info("Invalid key in quests.yml: " + key);
						}
					}
				}
			}
			saveQuests();
			if(verbose) {
				Quester.log.info(allQuests.size() + " quests loaded.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
