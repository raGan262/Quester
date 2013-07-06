package com.gmail.molnardad.quester;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gmail.molnardad.quester.storage.ConfigStorage;
import com.gmail.molnardad.quester.storage.Storage;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.strings.QuesterLang;

public class LanguageManager {
	
	private Map<String, QuesterLang> languages = new HashMap<String, QuesterLang>();

	public boolean hasLang(String name) {
		return languages.get(name.toLowerCase()) != null;
	}
	
	public QuesterLang getPlayerLang(String playerName) {
		// this will change
		return getDefaultLang();
	}
	
	public QuesterLang getDefaultLang() {
		return languages.get("english");
	}
	
	public QuesterLang getLang(String name) {
		if(!hasLang(name)) {
			return null;
		}
		return languages.get(name);
	}
	
	public boolean loadLang (String name, File file) {
		if(hasLang(name)) {
			return false;
		}
		Storage storage = new ConfigStorage(file, Quester.log, null);
		storage.load();
		StorageKey key = storage.getKey("");
		QuesterLang lang = new QuesterLang(file);
		Exception ex = null;
		int eCount = 0;
		for(Field f : lang.getClass().getFields()) {
			if(Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			String val = key.getString(f.getName(), "");
			if(val.isEmpty()) {
				try {
					key.setString(f.getName(),((String)f.get(lang)).replaceAll("\\n", "%n"));
					if(QConfiguration.debug) {
						Quester.log.info(f.getName() + " in " + file.getName() + " reset to default.");
					}
				} catch (Exception e) {
					ex = e;
					eCount++;
				}
			} else {
				try {
					f.set(lang, (String) val.replaceAll("%n", "\n"));
				} catch (Exception e) {
					ex = e;
					eCount++;
				}
			}
		}
		if(ex != null) {
			Quester.log.info(eCount + " error(s) occured while loading strings from file " + file.getName() + ".");
			if(QConfiguration.debug) {
				Quester.log.info("Last error:");
				ex.printStackTrace();
			}
		}
		languages.put(name.toLowerCase(), lang);
		storage.save();
		return true;
	}
	
	public boolean reloadLang(String name) {
		if(!hasLang(name)) {
			return false;
		}
		File file = languages.get(name).getFile();
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
