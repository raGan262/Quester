package com.gmail.molnardad.quester.config;

import com.gmail.molnardad.quester.Quester;

public final class QuestConfig extends CustomConfig {

	public QuestConfig(String fileName) {
		super(Quester.plugin, fileName);
	}

	@Override
	public void saveConfig() {
		for(String key : config.getKeys(false))
			config.set(key, null);
		for(String key : Quester.data.allQuests.keySet()) {
			Quester.data.allQuests.get(key).serialize(config.createSection(key));
		}
		super.saveConfig();
	}
}
