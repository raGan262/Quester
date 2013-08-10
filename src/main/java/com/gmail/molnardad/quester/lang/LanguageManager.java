package com.gmail.molnardad.quester.lang;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.storage.ConfigStorage;
import com.gmail.molnardad.quester.storage.Storage;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Ql;

public class LanguageManager {
	
	private final Map<String, QuesterLang> languages = new HashMap<String, QuesterLang>();
	private final Map<String, String> playerLangs = new HashMap<String, String>();
	private final Logger logger;
	private final File localFolder;
	private String defaultLangName = "english";
	
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
		languages.put(defaultLangName, new QuesterLang(null));
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
	
	public boolean loadLang(final String name, final File file) {
		if(hasLang(name)) {
			return false;
		}
		final Storage storage = new ConfigStorage(file, logger, null);
		storage.load();
		final StorageKey key = storage.getKey("");
		final QuesterLang lang = new QuesterLang(file);
		Exception ex = null;
		int eCount = 0;
		for(final Field f : lang.getClass().getFields()) {
			if(Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			final String val = key.getString(f.getName(), "");
			if(val.isEmpty()) {
				try {
					key.setString(f.getName(), ((String) f.get(lang)).replaceAll("\\n", "%n"));
					Ql.debug(f.getName() + " in " + file.getName() + " reset to default.");
				}
				catch (final Exception e) {
					ex = e;
					eCount++;
				}
			}
			else {
				try {
					f.set(lang, val.replaceAll("%n", "\n"));
				}
				catch (final Exception e) {
					ex = e;
					eCount++;
				}
			}
		}
		if(ex != null) {
			Ql.warning(eCount + " error(s) occured while loading strings from file "
					+ file.getName() + ".");
			Ql.debug("Last error:", ex);
		}
		languages.put(name.toLowerCase(), lang);
		storage.save();
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
