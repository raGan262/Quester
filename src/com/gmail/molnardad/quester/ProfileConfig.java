package com.gmail.molnardad.quester;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	
	@SuppressWarnings("unchecked")
	public Map<String, PlayerProfile> getPlayers() {
		if(config.get("Players") == null)
			return new HashMap<String, PlayerProfile>();
		return (Map<String, PlayerProfile>) config.get("Players");
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
