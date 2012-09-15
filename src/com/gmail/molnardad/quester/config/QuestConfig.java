package com.gmail.molnardad.quester.config;

import java.io.IOException;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.Quester;

public final class QuestConfig extends CustomConfig {

	public QuestConfig(String fileName) {
		super(Quester.plugin, fileName);
	}

	@Override
	public void saveConfig() {
		for(String key : config.getKeys(false))
			config.set(key, null);
		for(String key : QuestData.allQuests.keySet()) {
			QuestData.allQuests.get(key).serialize(config.createSection(key));
		}
		try {
			config.save(conFile);
		} catch (IOException ex) {
			plugin.getLogger().severe("Can't Write To File '" + conFile.getName() + "'!");
	    }
	}
}
