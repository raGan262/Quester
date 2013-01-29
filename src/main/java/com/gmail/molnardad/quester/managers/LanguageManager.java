package com.gmail.molnardad.quester.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.config.LanguageConfig;
import com.gmail.molnardad.quester.strings.QuesterStrings;

public class LanguageManager {
	
	private static LanguageManager instance = null;
	
	private Quester plugin;
	private Map<String, LanguageConfig> languages = new HashMap<String, LanguageConfig>();

	public LanguageManager(Quester plugin) {
		this.plugin = plugin;
	}
	
	public static void setInstance(LanguageManager languageManager) {
		instance = languageManager;
	}
	
	public static LanguageManager getInstance() {
		return instance;
	}
	
	public boolean hasLang(String name) {
		return languages.get(name.toLowerCase()) != null;
	}
	
	public QuesterStrings getPlayerLang(String playerName) {
		return getDefaultLang();
	}
	
	public QuesterStrings getDefaultLang() {
		return languages.get("english").getStrings();
	}
	
	public QuesterStrings getLang(String name) {
		if(!hasLang(name)) {
			return null;
		}
		return languages.get(name).getStrings();
	}
	
	public boolean loadLang (String name, String fileName) {
		if(hasLang(name)) {
			return false;
		}
		LanguageConfig cnf = new LanguageConfig(plugin, fileName);
		languages.put(name.toLowerCase(), cnf);
		cnf.saveConfig();
		return true;
	}
	
	public boolean reloadLang(String name) {
		if(!hasLang(name)) {
			return false;
		}
		
		languages.get(name).reloadStrings();
		return true;
	}
	
	public Set<String> getLangSet() {
		return languages.keySet();
	}
}
