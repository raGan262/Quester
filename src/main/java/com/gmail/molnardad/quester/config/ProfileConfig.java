package com.gmail.molnardad.quester.config;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.managers.DataManager;

public final class ProfileConfig extends CustomConfig {

	
	public ProfileConfig(Quester plugin, String fileName) {
		super(plugin, fileName);
	}
	
	@Override
	public void saveConfig() {
		DataManager data = DataManager.getInstance();
		for(String key : config.getKeys(false))
			config.set(key, null);
		for(String key : data.profiles.keySet()) {
			data.profiles.get(key).serialize(config.createSection(key));
		}
		super.saveConfig();
	}
}
