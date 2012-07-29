package com.gmail.molnardad.quester;


import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.gmail.molnardad.quester.utils.Util;

public class QuestData {
	
	public static boolean verbose = false;
	public static boolean noDrops = false;
	public static boolean onlyFirst = false;
	public static int saveInterval = 15;
	public static boolean debug = true;
	public static boolean showObjs = true;

	public static String USE_PERM = "quester.use";
	public static String MODIFY_PERM = "quester.modify";
	public static String ADMIN_PERM = "quester.admin";
	
	// <QuestName, Quest>
	public static HashMap<String, Quest> allQuests = new HashMap<String, Quest>(); 
	// <PlayerName, QuestName>
	public static HashMap<String, String> currentQuests = new HashMap<String, String>(); 
	// <PlayerName, <ObjectiveIndex, Amount>>
	public static HashMap<String, ArrayList<Integer>> objectiveProgress = new HashMap<String, ArrayList<Integer>>();

	public static HashMap<String, String> selectedQuest = new HashMap<String, String>(); 
	
	static void saveData(){
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(allQuests);
		data.add(currentQuests);
		data.add(objectiveProgress);
		try {
			Util.saveObject(data, Quester.plugin.getDataFolder(), "Data.qd");
		} catch (IOException e) {
			Quester.log.severe("Error while saving quest data.");
			if(debug) {
				e.printStackTrace();
			}
			return;
		}
		if(verbose) {
			Quester.log.info("Data saved.");
		}
	}

	@SuppressWarnings("unchecked")
	static void loadData(){
		ArrayList<Object> data = null;
		try {
			data = (ArrayList<Object>) Util.loadObject(Quester.plugin.getDataFolder(), "Data.qd");
			if(data != null) {
				allQuests = (HashMap<String, Quest>) data.get(0);
				currentQuests = (HashMap<String, String>) data.get(1);
				objectiveProgress = (HashMap<String, ArrayList<Integer>>) data.get(2);
			}
		} catch (EOFException e) {
			Quester.log.severe("Couldn't load quest data. Is it first run or are they corrupted ?");
			if(debug) {
				e.printStackTrace();
			}
			resetData();
			return;
		} catch (Exception e) {
			Quester.log.severe("Error while loading quest data.");
			if(debug) {
				e.printStackTrace();
			}
			resetData();
			return;
		}
		if(verbose) {
			Quester.log.info("Data loaded.");
		}
	}
	
	private static void resetData() {
		allQuests = new HashMap<String, Quest>(); 
		currentQuests = new HashMap<String, String>(); 
		objectiveProgress = new HashMap<String, ArrayList<Integer>>(); 
	}
	
	static void wipeData(){
		allQuests = null;
		currentQuests = null;
		objectiveProgress = null;
	}
	
}
