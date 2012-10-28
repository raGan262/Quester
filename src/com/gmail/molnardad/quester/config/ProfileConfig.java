package com.gmail.molnardad.quester.config;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.Quester;

public final class ProfileConfig extends CustomConfig {

	
	public ProfileConfig(String fileName) {
		super(Quester.plugin, fileName);
	}
	
	@Override
	public void saveConfig() {
		for(String key : config.getKeys(false))
			config.set(key, null);
		for(String key : QuestData.profiles.keySet()) {
			QuestData.profiles.get(key).serialize(config.createSection(key));
		}
		super.saveConfig();
	}
}
