package com.gmail.molnardad.quester;


import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gmail.molnardad.quester.utils.Util;

public class QuestData {
	
	public static boolean verbose = false;
	public static boolean noDrops = false;
	public static boolean onlyFirst = false;
	public static int saveInterval = 15;
	public static boolean debug = true;
	public static boolean showObjs = true;
	public static boolean disUseCmds = false;

	public static final String USE_PERM = "quester.use";
	public static final String MODIFY_PERM = "quester.modify";
	public static final String ADMIN_PERM = "quester.admin";


	public static Map<String, Quest> allQuests = new HashMap<String, Quest>();
	public static Map<String, PlayerProfile> profiles = new HashMap<String, PlayerProfile>();

	
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
		Map<String, Quest> data = allQuests;
		try {
			Util.saveObject(data, Quester.plugin.getDataFolder(), "Quests.qd");
		} catch (IOException e) {
			Quester.log.severe("Error while saving quest data.");
			if(debug) {
				e.printStackTrace();
			}
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	static void loadQuests(){
		HashMap<String, Quest> data = null;
		try {
			data = (HashMap<String, Quest>) Util.loadObject(Quester.plugin.getDataFolder(), "Quests.qd");
			if(data != null) {
				allQuests = data;
			}
			if(verbose) {
				Quester.log.info("Quests loaded.");
			}
		} catch (EOFException e) {
			Quester.log.severe("Couldn't load quests. Is it first run or are they corrupted ?");
			if(debug) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Quester.log.severe("Error while loading quests.");
			if(debug) {
				e.printStackTrace();
			}
		}
	}
}
