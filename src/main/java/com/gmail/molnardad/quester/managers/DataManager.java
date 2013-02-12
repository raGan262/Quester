package com.gmail.molnardad.quester.managers;

import com.gmail.molnardad.quester.Quester;

public class DataManager {
	
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
	public static String locLabelBlock = "block";

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
	
	public DataManager(Quester plugin) {
	
	}
	
	// TODO STORAGE METHODS
	
	public void loadData() {
		
	}
	
	public void saveData() {
		
	}
}
