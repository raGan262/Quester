package com.gmail.molnardad.quester.config;

import com.gmail.molnardad.quester.DataManager;
import com.gmail.molnardad.quester.Quester;

public final class QuestConfig extends CustomConfig {

	public QuestConfig(Quester plugin, String fileName) {
		super(plugin, fileName);
	}

	@Override
	public void saveConfig() {
		DataManager data = DataManager.getInstance();
		for(String key : config.getKeys(false))
			config.set(key, null);
		for(String key : data.allQuests.keySet()) {
			data.allQuests.get(key).serialize(config.createSection(key));
		}
		super.saveConfig();
	}
}
