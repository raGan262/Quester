package me.ragan262.quester.lang;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import me.ragan262.quester.Quester;
import me.ragan262.quester.storage.ConfigStorage;
import me.ragan262.quester.storage.Storage;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;

public class LanguageManager {
	
	public static QuesterLang defaultLang = new QuesterLang(null, new MessageRegistry());
	
	static final String CUSTOM_KEY = "-CUSTOM-";
	
	public static String getCustomMessageKey(final String rawMessage) {
		final String upper = rawMessage.toUpperCase();
		if(upper.startsWith("LANG:")) {
			return upper.substring(upper.indexOf(':') + 1);
		}
		else {
			return null;
		}
	}
	
	private final Map<String, QuesterLang> languages = new HashMap<String, QuesterLang>();
	private final Map<String, String> playerLangs = new HashMap<String, String>();
	private final Logger logger;
	private final File localFolder;
	private String defaultLangName = "english";
	private final MessageRegistry registry = new MessageRegistry();
	
	public LanguageManager(final Quester plugin, final File localFolder) {
		logger = plugin.getLogger();
		if(!localFolder.isDirectory()) {
			if(!localFolder.mkdir()) {
				throw new IllegalArgumentException("Could not create local directory.");
			}
		}
		this.localFolder = localFolder;
		final QuesterLang defLang = new QuesterLang(null, registry);
		defLang.addDefaults();
		LanguageManager.defaultLang = defLang;
		languages.put(defaultLangName, defLang);
	}
	
	public boolean messageExists(final String key) {
		return registry.messages.get(key) != null;
	}
	
	public boolean customMessageExists(final String key) {
		return registry.customMessages.get(key) != null;
	}
	
	public boolean registerMessage(final String key, final String message) {
		return registry.registerMessage(key, message);
	}
	
	public boolean registerCustomMessage(final String key, final String message) {
		return registry.registerCustomMessage(key, message);
	}
	
	public int loadCustomMessages(final File file) {
		final Storage messageStorage = new ConfigStorage(file, logger, null);
		int count = 0;
		if(messageStorage.load()) {
			for(final StorageKey key : messageStorage.getKey("").getSubKeys()) {
				if(registerCustomMessage(key.getName(), key.getString(""))) {
					count++;
				}
			}
			Ql.verbose("Loaded " + count + " custom messages.");
			return count;
		}
		else {
			Ql.severe("Failed to load custom messages.");
		}
		return 0;
	}
	
	public void clearCustomMessages() {
		registry.customMessages.clear();
	}
	
	public Set<String> getLangSet() {
		return new HashSet<String>(languages.keySet());
	}
	
	public boolean hasLang(final String name) {
		return name != null && languages.get(name.toLowerCase()) != null;
	}
	
	public QuesterLang getPlayerLang(final String playerName) {
		if(playerName == null) {
			return getDefaultLang();
		}
		return getLang(playerLangs.get(playerName.toLowerCase()));
	}
	
	public String getPlayerLangName(final String playerName) {
		if(playerName == null) {
			return defaultLangName;
		}
		final String lang = playerLangs.get(playerName.toLowerCase());
		return lang == null ? defaultLangName : lang;
	}
	
	public boolean setPlayerLang(final String playerName, final String langName) {
		if(playerName == null) {
			return false;
		}
		if(langName == null) {
			playerLangs.remove(playerName.toLowerCase());
			return true;
		}
		if(hasLang(langName)) {
			playerLangs.put(playerName.toLowerCase(), langName.toLowerCase());
			return true;
		}
		return false;
	}
	
	public QuesterLang getLang(final String name) {
		if(name == null || !hasLang(name)) {
			return getDefaultLang();
		}
		return languages.get(name.toLowerCase());
	}
	
	public QuesterLang getDefaultLang() {
		return languages.get(defaultLangName);
	}
	
	public String getDefaultLangName() {
		return defaultLangName;
	}
	
	public boolean setDefaultLang(final String langName) {
		if(hasLang(langName)) {
			defaultLangName = langName.toLowerCase();
			return true;
		}
		return false;
	}
	
	public void saveLanguage(final QuesterLang lang) {
		if(lang.addDefaults() > 0 && lang.getFile() != null) {
			final Storage storage = new ConfigStorage(lang.getFile(), logger, null);
			// save regular messages
			StorageKey storageKey = storage.getKey("");
			Map<String, String> messages = lang.getMessages();
			for(final String key : new TreeSet<String>(messages.keySet())) {
				storageKey.setString(key, messages.get(key));
			}
			// save user created messages
			storageKey = storage.getKey(CUSTOM_KEY);
			messages = lang.getCustomMessages();
			for(final String key : new TreeSet<String>(messages.keySet())) {
				storageKey.setString(key, messages.get(key));
			}
			
			storage.save();
		}
	}
	
	public void saveLanguages() {
		for(final QuesterLang lang : languages.values()) {
			saveLanguage(lang);
		}
	}
	
	public boolean loadLang(final String name, final File file) {
		if(hasLang(name)) {
			return false;
		}
		
		final Storage storage = new ConfigStorage(file, logger, null);
		storage.load();
		
		final StorageKey key = storage.getKey("");
		final QuesterLang lang = new QuesterLang(file, registry);
		
		for(final StorageKey subKey : key.getSubKeys()) {
			if(subKey.hasSubKeys() && subKey.getName().equals(LanguageManager.CUSTOM_KEY)) {
				for(final StorageKey customKey : subKey.getSubKeys()) {
					lang.putCustom(customKey.getName(), customKey.getString(""));
				}
			}
			else {
				lang.put(subKey.getName(), subKey.getString(""));
			}
		}
		saveLanguage(lang);
		languages.put(name.toLowerCase(), lang);
		return true;
	}
	
	public int loadLangs() {
		languages.clear();
		final String defaultFileName = defaultLangName + ".yml";
		loadLang(defaultLangName, new File(localFolder, defaultFileName));
		int i = 1;
		for(final File f : localFolder.listFiles()) {
			final String name = f.getName().toLowerCase();
			if(!name.equals(defaultFileName) && name.endsWith(".yml")) {
				loadLang(name.substring(0, name.length() - 4), f);
				i++;
			}
		}
		return i;
	}
	
	public boolean reloadLang(final String name) {
		if(!hasLang(name)) {
			return false;
		}
		final File file = languages.get(name).getFile();
		languages.remove(name);
		loadLang(name, file);
		return true;
	}
}
