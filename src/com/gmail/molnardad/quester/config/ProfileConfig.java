package com.gmail.molnardad.quester.config;

import java.io.IOException;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.Quester;

public final class ProfileConfig extends CustomConfig {

	
	public ProfileConfig(String fileName) {
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
		for(String key : QuestData.profiles.keySet()) {
			config.set(key, QuestData.profiles.get(key));
		}
		try {
			config.save(conFile);
		} catch (IOException ex) {
			plugin.getLogger().severe("Can't Write To File '" + conFile.getName() + "'!");
	    }
	}
}
