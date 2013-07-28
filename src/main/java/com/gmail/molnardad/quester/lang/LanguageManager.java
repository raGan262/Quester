package com.gmail.molnardad.quester.lang;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.storage.ConfigStorage;
import com.gmail.molnardad.quester.storage.Storage;
import com.gmail.molnardad.quester.storage.StorageKey;

public class LanguageManager {
	
	private final Map<String, QuesterLang> languages = new HashMap<String, QuesterLang>();
	
	public boolean hasLang(final String name) {
		return languages.get(name.toLowerCase()) != null;
	}
	
	public QuesterLang getPlayerLang(final String playerName) {
		// this will change
		return getDefaultLang();
	}
	
	public QuesterLang getDefaultLang() {
		return languages.get("english");
	}
	
	public QuesterLang getLang(final String name) {
		if(!hasLang(name)) {
			return null;
		}
		return languages.get(name);
	}
	
	public boolean loadLang(final String name, final File file) {
		if(hasLang(name)) {
			return false;
		}
		final Storage storage = new ConfigStorage(file, Quester.log, null);
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
					if(QConfiguration.debug) {
						Quester.log
								.info(f.getName() + " in " + file.getName() + " reset to default.");
					}
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
			Quester.log.info(eCount + " error(s) occured while loading strings from file " + file
					.getName() + ".");
			if(QConfiguration.debug) {
				Quester.log.info("Last error:");
				ex.printStackTrace();
			}
		}
		languages.put(name.toLowerCase(), lang);
		storage.save();
		return true;
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
	
	public void reloadLangs() {
		// TODO
	}
	
	public Set<String> getLangSet() {
		return languages.keySet();
	}
}
