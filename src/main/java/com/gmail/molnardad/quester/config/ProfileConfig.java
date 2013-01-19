package com.gmail.molnardad.quester.config;

import com.gmail.molnardad.quester.Quester;

public final class ProfileConfig extends CustomConfig {

	
	public ProfileConfig(Quester plugin, String fileName) {
		super(plugin, fileName);
	}
	
	@Override
	public void saveConfig() {
		for(String key : config.getKeys(false))
			config.set(key, null);
		for(String key : Quester.data.profiles.keySet()) {
			Quester.data.profiles.get(key).serialize(config.createSection(key));
		}
		super.saveConfig();
	}
}
