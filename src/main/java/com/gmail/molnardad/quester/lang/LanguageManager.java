package com.gmail.molnardad.quester.lang;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.storage.ConfigStorage;
import com.gmail.molnardad.quester.storage.Storage;
import com.gmail.molnardad.quester.storage.StorageKey;

public class LanguageManager {
	
	public static QuesterLang defaultLang = new QuesterLang(null, new MessageRegistry());
	
	private final Map<String, QuesterLang> languages = new HashMap<String, QuesterLang>();
	private final Map<String, String> playerLangs = new HashMap<String, String>();
	private final Logger logger;
	private final File localFolder;
	private String defaultLangName = "english";
	private final MessageRegistry registry = new MessageRegistry();
	
	public LanguageManager(final Quester plugin, final File localFolder, final String defaultLang) {
		logger = plugin.getLogger();
		if(!localFolder.isDirectory()) {
			if(!localFolder.mkdir()) {
				throw new IllegalArgumentException("Could not create local directory.");
			}
		}
		this.localFolder = localFolder;
		if(defaultLang != null) {
			defaultLangName = defaultLang;
		}
		final QuesterLang defLang = new QuesterLang(null, registry);
		defLang.addDefaults();
		LanguageManager.defaultLang = defLang;
		languages.put(defaultLangName, defLang);
	}
	
	public boolean registerMessage(final String key, final String message) {
		return registry.registerMessage(key, message);
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
			final StorageKey storageKey = storage.getKey("");
			final Map<String, String> messages = lang.getMessages();
			
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
			final String message = subKey.getString("");
			lang.put(subKey.getName(), message);
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
