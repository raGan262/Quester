package com.gmail.molnardad.quester;

import java.io.IOException;

public final class QuestConfig extends CustomConfig {

	public QuestConfig(String fileName) {
		super(Quester.plugin, fileName);
	}

	@Override
	public void initialize() {
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public void saveConfig() {
		for(String key : QuestData.allQuests.keySet()) {
			config.set(key, QuestData.allQuests.get(key));
		}
		try {
			config.save(conFile);
		} catch (IOException ex) {
			plugin.getLogger().severe("Can't Write To File '" + conFile.getName() + "'!");
	    }
	}
}
