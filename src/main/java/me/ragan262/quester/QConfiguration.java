package me.ragan262.quester;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import me.ragan262.quester.storage.ConfigStorage;
import me.ragan262.quester.storage.Storage;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;
import me.ragan262.quester.utils.Util;

public class QConfiguration {
	
	// GENERAL
	public static boolean verbose = true;
	public static int saveInterval = 15;
	public static boolean debug = false;
	public static boolean useRank = true;
	public static boolean useMetrics = true;
	public static String defaultLang = "english";
	public static boolean autoBackup = true;
	
	// MYSQL
	public static String mysqlUrl = "jdbc:mysql://localhost:3306/minecraft?useUnicode=true&characterEncoding=utf-8";
	public static String mysqlUser = "username";
	public static String mysqlPass = "password";
	
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
	public static boolean progMsg = true;
	
	// COMMANDS
	public static String displayedCmd = "/q";
	public static String worldLabelThis = "this";
	public static String locLabelHere = "here";
	public static String locLabelPlayer = "player";
	public static String locLabelBlock = "block";
	
	// QUEST ITEMS
	
	public static final Set<Integer> questItemInteractable = new HashSet<>();
	
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
	
	private static QConfiguration instance = null;
	private Storage storage = null;
	
	private QConfiguration(final Quester plugin) {
		final File file = new File(plugin.getDataFolder(), "config.yml");
		storage = new ConfigStorage(file, plugin.getLogger(), plugin.getResource(file.getName()));
		storage.load();
	}
	
	public static void createInstance(final Quester quester) {
		instance = new QConfiguration(quester);
	}
	
	private static void wrongConfig(final String path, final String def) {
		Ql.info("Invalid or missing value in config: " + path.replace('.', ':')
				+ ". Setting to default. (" + def + ")");
	}
	
	public static StorageKey getConfigKey(final String key) throws InstanceNotFoundException {
		if(instance == null) {
			throw new InstanceNotFoundException();
		}
		return instance.storage.getKey(key);
	}
	
	// STORAGE METHODS
	
	public static void reloadData() throws InstanceNotFoundException {
		if(instance == null) {
			throw new InstanceNotFoundException();
		}
		instance.storage.load();
		loadData();
	}
	
	public static void loadData() throws InstanceNotFoundException {
		if(instance == null) {
			throw new InstanceNotFoundException();
		}
		final StorageKey mainKey = instance.storage.getKey("");
		String path, temp;
		
		// VERBOSE-LOGGING
		path = "general.verbose-logging";
		QConfiguration.verbose = mainKey.getBoolean(path, false);
		mainKey.setBoolean(path, QConfiguration.verbose);
		
		// SAVE INTERVAL
		path = "general.save-interval";
		if(mainKey.getInt(path, -1) < 0) {
			mainKey.setInt(path, 15);
			wrongConfig(path, "15");
		}
		QConfiguration.saveInterval = mainKey.getInt(path, 15);
		mainKey.setInt(path, QConfiguration.saveInterval);
		
		// DEBUG INFO
		path = "general.debug-info";
		QConfiguration.debug = mainKey.getBoolean(path);
		mainKey.setBoolean(path, QConfiguration.debug);
		
		// USE RANK
		path = "general.use-rank";
		QConfiguration.useRank = mainKey.getBoolean(path);
		mainKey.setBoolean(path, QConfiguration.useRank);
		
		// PROFILE STORAGE
		//		path = "general.profile-storage";
		//		temp = mainKey.getString(path, "");
		//		StorageType type = StorageType.forName(temp);
		//		if(type == null) {
		//			type = StorageType.CONFIG;
		//			mainKey.setString(path, "CONFIG");
		//			wrongConfig(path, "CONFIG");
		//		}
		//		QConfiguration.profileStorageType = type;
		//		mainKey.setString(path, profileStorageType.name());
		
		// USE METRICS
		path = "general.use-metrics";
		QConfiguration.useMetrics = mainKey.getBoolean(path);
		mainKey.setBoolean(path, QConfiguration.useMetrics);
		
		// DEFAULT LANG
		path = "general.default-lang";
		temp = mainKey.getString(path, "");
		if(temp.isEmpty()) {
			mainKey.setString(path, "english");
			wrongConfig(path, "english");
		}
		QConfiguration.defaultLang = mainKey.getString(path, "english");
		mainKey.setString(path, QConfiguration.defaultLang);
		
		// MYSQL
		path = "mysql.host";
		temp = mainKey.getString(path, "");
		if(temp.isEmpty()) {
			mainKey.setString(path, "localhost");
			wrongConfig(path, "localhost");
		}
		
		path = "mysql.port";
		final int tempInt = mainKey.getInt(path, -1);
		if(tempInt < 0) {
			mainKey.setInt(path, 3306);
			wrongConfig(path, "3306");
		}
		
		path = "mysql.database";
		temp = mainKey.getString(path, "");
		if(temp.isEmpty()) {
			mainKey.setString(path, "minecraft");
			wrongConfig(path, "minecraft");
		}
		QConfiguration.mysqlUrl = "jdbc:mysql://" + mainKey.getString("mysql.host") + ":"
				+ mainKey.getInt("mysql.port") + "/" + mainKey.getString("mysql.database")
				+ "?useUnicode=true&characterEncoding=utf-8";
		
		path = "mysql.username";
		temp = mainKey.getString(path, "");
		if(temp.isEmpty()) {
			mainKey.setString(path, "username");
			wrongConfig(path, "username");
		}
		QConfiguration.mysqlUser = mainKey.getString(path, "username");
		mainKey.setString(path, QConfiguration.mysqlUser);
		
		path = "mysql.password";
		temp = mainKey.getString(path, "");
		if(temp.isEmpty()) {
			mainKey.setString(path, "password");
			wrongConfig(path, "password");
		}
		QConfiguration.mysqlPass = mainKey.getString(path, "password");
		mainKey.setString(path, QConfiguration.mysqlPass);
		
		// SHOW ONLY CURRENT
		path = "objectives.show-only-current";
		QConfiguration.ordOnlyCurrent = mainKey.getBoolean(path, true);
		mainKey.setBoolean(path, QConfiguration.ordOnlyCurrent);
		
		// BREAK NO DROPS
		path = "objectives.break.no-drops";
		QConfiguration.brkNoDrops = mainKey.getBoolean(path, false);
		mainKey.setBoolean(path, QConfiguration.brkNoDrops);
		
		// BREAK SUBTRACT ON PLACE
		path = "objectives.break.subtract-on-place";
		QConfiguration.brkSubOnPlace = mainKey.getBoolean(path, true);
		mainKey.setBoolean(path, QConfiguration.brkSubOnPlace);
		
		// COLLECT REMOVE ON PICKUP
		path = "objectives.collect.remove-on-pickup";
		QConfiguration.colRemPickup = mainKey.getBoolean(path, true);
		mainKey.setBoolean(path, QConfiguration.colRemPickup);
		
		// COLLECT SUBTRACT ON DROP
		path = "objectives.collect.subtract-on-drop";
		QConfiguration.colSubOnDrop = mainKey.getBoolean(path, false);
		mainKey.setBoolean(path, QConfiguration.colSubOnDrop);
		
		// AUTO BACKUP
		path = "quests.auto-backup";
		QConfiguration.autoBackup = mainKey.getBoolean(path, true);
		mainKey.setBoolean(path, QConfiguration.autoBackup);
		
		// MAX QUESTS
		path = "quests.max-amount";
		if(mainKey.getInt(path, -1) < 1) {
			mainKey.setInt(path, 1);
			wrongConfig(path, "1");
		}
		QConfiguration.maxQuests = mainKey.getInt(path, 1);
		mainKey.setInt(path, QConfiguration.maxQuests);
		
		// PROGRES MESSAGES
		path = "quests.messages.start-show";
		QConfiguration.progMsgStart = mainKey.getBoolean(path, true);
		mainKey.setBoolean(path, QConfiguration.progMsgStart);
		
		path = "quests.messages.cancel-show";
		QConfiguration.progMsgCancel = mainKey.getBoolean(path, true);
		mainKey.setBoolean(path, QConfiguration.progMsgCancel);
		
		path = "quests.messages.done-show";
		QConfiguration.progMsgDone = mainKey.getBoolean(path, true);
		mainKey.setBoolean(path, QConfiguration.progMsgDone);
		
		path = "quests.messages.objective-show";
		QConfiguration.progMsgObj = mainKey.getBoolean(path, true);
		mainKey.setBoolean(path, QConfiguration.progMsgObj);
		
		path = "quests.messages.progress-show";
		QConfiguration.progMsg = mainKey.getBoolean(path, true);
		mainKey.setBoolean(path, QConfiguration.progMsg);
		
		// COMMANDS
		path = "commands.displayed-cmd";
		temp = mainKey.getString(path, "");
		if(!temp.equals("/q") && !temp.equals("/quest") && !temp.equals("/quester")) {
			mainKey.setString(path, "/q");
			wrongConfig(path, "/q");
		}
		QConfiguration.displayedCmd = mainKey.getString(path, "/q");
		mainKey.setString(path, QConfiguration.displayedCmd);
		
		path = "commands.world-label-this";
		temp = mainKey.getString(path, "");
		if(temp.isEmpty()) {
			mainKey.setString(path, "this");
			wrongConfig(path, "this");
		}
		QConfiguration.worldLabelThis = mainKey.getString(path, "this");
		mainKey.setString(path, QConfiguration.worldLabelThis);
		
		path = "commands.loc-label-here";
		temp = mainKey.getString(path, "");
		if(temp.isEmpty()) {
			mainKey.setString(path, "here");
			wrongConfig(path, "here");
		}
		QConfiguration.locLabelHere = mainKey.getString(path, "here");
		mainKey.setString(path, QConfiguration.locLabelHere);
		
		path = "commands.loc-label-player";
		temp = mainKey.getString(path, "");
		if(temp.isEmpty()) {
			mainKey.setString(path, "player");
			wrongConfig(path, "player");
		}
		QConfiguration.locLabelPlayer = mainKey.getString(path, "player");
		mainKey.setString(path, QConfiguration.locLabelPlayer);
		
		path = "commands.loc-label-block";
		temp = mainKey.getString(path, "");
		if(temp.isEmpty()) {
			mainKey.setString(path, "block");
			wrongConfig(path, "block");
		}
		QConfiguration.locLabelBlock = mainKey.getString(path, "block");
		mainKey.setString(path, QConfiguration.locLabelBlock);
		
		// IS WHITELIST
		path = "quest-item.interactable";
		QConfiguration.questItemInteractable.clear();
		try {
			for(final String s : mainKey.getString(path).split(",")) {
				QConfiguration.questItemInteractable.add(Integer.valueOf(s));
			}
		}
		catch(final Exception ignore) {}
		mainKey.setString(path, Util.implodeIterable(QConfiguration.questItemInteractable, ","));
		
		saveData();
	}
	
	public static void saveData() throws InstanceNotFoundException {
		if(instance == null) {
			throw new InstanceNotFoundException();
		}
		instance.storage.save();
	}
}
