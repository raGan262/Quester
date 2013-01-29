package com.gmail.molnardad.quester.config;

import java.lang.reflect.Field;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.managers.DataManager;
import com.gmail.molnardad.quester.strings.QuesterStrings;

public class LanguageConfig extends CustomConfig {
	
	private QuesterStrings strings;
	
	public LanguageConfig(Quester plugin, String fileName) {
		super(plugin, fileName + ".yml");
		
		strings = new QuesterStrings();
		
		loadStrings();
	}
	
	public QuesterStrings getStrings() {
		return strings;
	}
	
	private void loadStrings() {
		Exception ex = null;
		int eCount = 0;
		for(Field f : strings.getClass().getFields()) {
			String val = config.getString(f.getName(), "");
			if(val.isEmpty()) {
				try {
					config.set(f.getName(),((String)f.get(strings)).replaceAll("\\n", "%n"));
					if(DataManager.getInstance().debug) {
						Quester.log.info(f.getName() + " in " + this.conFile.getName() + " reset to default.");
					}
				} catch (Exception e) {
					ex = e;
					eCount++;
				}
			} else {
				try {
					f.set(strings, (String) val.replaceAll("%n", "\n"));
				} catch (Exception e) {
					ex = e;
					eCount++;
				}
			}
		}
		if(ex != null) {
			Quester.log.info(eCount + " error(s) occured while loading strings from file " + conFile.getName() + ".");
			if(DataManager.getInstance().debug) {
				Quester.log.info("Last error:");
				ex.printStackTrace();
			}
		}
	}
	
	public void reloadStrings() {
		loadConfig();
		loadStrings();
	}
}
