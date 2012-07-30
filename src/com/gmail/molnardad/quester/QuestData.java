package com.gmail.molnardad.quester;


import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.gmail.molnardad.quester.utils.Util;

public class QuestData {
	
	public static boolean verbose = false;
	public static boolean noDrops = false;
	public static boolean onlyFirst = false;
	public static int saveInterval = 15;
	public static boolean debug = true;
	public static boolean showObjs = true;
	public static boolean disUseCmds = false;

	public static String USE_PERM = "quester.use";
	public static String MODIFY_PERM = "quester.modify";
	public static String ADMIN_PERM = "quester.admin";
	
	// <QuestName, Quest>
	public static HashMap<String, Quest> allQuests = new HashMap<String, Quest>(); 
	// <PlayerName, QuestName>
	public static HashMap<String, String> currentQuests = new HashMap<String, String>(); 
	// <PlayerName, <ObjectiveIndex, Amount>>
	public static HashMap<String, ArrayList<Integer>> objectiveProgress = new HashMap<String, ArrayList<Integer>>();
	// <PlayerName, HashSet<CompletedQuests>>
	public static HashMap<String, HashSet<String>> completedQuests = new HashMap<String, HashSet<String>>();

	public static HashMap<String, String> selectedQuest = new HashMap<String, String>(); 
	
	private static void resetData() {
		currentQuests = new HashMap<String, String>(); 
		objectiveProgress = new HashMap<String, ArrayList<Integer>>(); 
	}
	
	static void wipeData(){
		allQuests = null;
		currentQuests = null;
		objectiveProgress = null;
	}
	
	static void saveProfiles(){
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(currentQuests);
		data.add(objectiveProgress);
		data.add(completedQuests);
		try {
			Util.saveObject(data, Quester.plugin.getDataFolder(), "Profiles.qd");
		} catch (IOException e) {
			Quester.log.severe("Error while saving profiles.");
			if(debug) {
				e.printStackTrace();
			}
			return;
		}
	}

	@SuppressWarnings("unchecked")
	static void loadProfiles(){
		ArrayList<Object> data = null;
		try {
			data = (ArrayList<Object>) Util.loadObject(Quester.plugin.getDataFolder(), "Profiles.qd");
			if(data != null) {
				currentQuests = (HashMap<String, String>) data.get(0);
				objectiveProgress = (HashMap<String, ArrayList<Integer>>) data.get(1);
				completedQuests = (HashMap<String, HashSet<String>>) data.get(2);
			}
		} catch (EOFException e) {
			Quester.log.severe("Couldn't load profiles. Is it first run or are they corrupted ?");
			if(debug) {
				e.printStackTrace();
			}
			return;
		} catch (Exception e) {
			Quester.log.severe("Error while loading profiles.");
			if(debug) {
				e.printStackTrace();
			}
			resetData();
			return;
		}
		if(verbose) {
			Quester.log.info("Profiles loaded.");
		}
	}
	
	static void saveQuests(){
		HashMap<String, Quest> data = allQuests;
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

	@SuppressWarnings("unchecked")
	static boolean loadOldData(){
		ArrayList<Object> data = null;
		try {
			data = (ArrayList<Object>) Util.loadObject(Quester.plugin.getDataFolder(), "Data.qd");
			if(data != null) {
				allQuests = (HashMap<String, Quest>) data.get(0);
				currentQuests = (HashMap<String, String>) data.get(1);
				objectiveProgress = (HashMap<String, ArrayList<Integer>>) data.get(2);
			}
		} catch (EOFException e) {
			Quester.log.severe("Error while loading quest data - file not found or empty.");
			if(debug) {
				e.printStackTrace();
			}
			return false;
		} catch (Exception e) {
			Quester.log.severe("Error while loading quest data - can't read.");
			if(debug) {
				e.printStackTrace();
			}
			return false;
		}
		if(verbose) {
			Quester.log.info("Data loaded.");
		}
		return true;
	}
}
