package com.gmail.molnardad.quester.strings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gmail.molnardad.quester.config.LanguageConfig;

public class LanguageManager {
	
	private Map<String, LanguageConfig> languages = new HashMap<String, LanguageConfig>();

	public boolean hasLang(String name) {
		return languages.get(name.toLowerCase()) != null;
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
		LanguageConfig cnf = new LanguageConfig(fileName);
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
