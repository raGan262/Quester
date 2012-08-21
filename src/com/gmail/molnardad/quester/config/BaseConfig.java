package com.gmail.molnardad.quester.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.Quester;

public class BaseConfig extends CustomConfig {

	public BaseConfig(String fileName) {
		super(Quester.plugin, fileName);
	}
	
	private void wrongConfig(String path) {
		Quester.log.info("Invalid or missing value in config: " + path.replace('.', ':') + ". Setting to default.");
	}
	
	@Override
	public void initialize() {
		
		String path;
		// VERBOSE-LOGGING
		path = "general.verbose-logging";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, true);
			wrongConfig(path);
		}
		QuestData.verbose = this.config.getBoolean(path);

		// SAVE INTERVAL
		path = "general.save-interval";
		if(this.config.getInt(path) < 0) {
			this.config.set(path, 15);
			wrongConfig(path);
		}
		QuestData.saveInterval = this.config.getInt(path);
		
		// DEBUG INFO
		path = "general.debug-info";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, false);
			wrongConfig(path);
		}
		QuestData.debug = this.config.getBoolean(path);
		
		path = "general.disable-usecmds";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, false);
			wrongConfig(path);
		}
		QuestData.disUseCmds = this.config.getBoolean(path);
		
		// SHOW ONLY CURRENT
		path = "objectives.show-only-current";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, true);
			wrongConfig(path);
		}
		QuestData.ordOnlyCurrent = this.config.getBoolean(path);
		
		// BREAK NO DROPS
		path = "objectives.break.no-drops";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, false);
			wrongConfig(path);
		}
		QuestData.brkNoDrops = this.config.getBoolean(path);
		
		// BREAK SUBTRACT ON PLACE
		path = "objectives.break.subtract-on-place";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, true);
			wrongConfig(path);
		}
		QuestData.brkSubOnPlace = this.config.getBoolean(path);
		
		// COLLECT REMOVE ON PICKUP
		path = "objectives.collect.remove-on-pickup";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, true);
			wrongConfig(path);
		}
		QuestData.colRemPickup = this.config.getBoolean(path);
				
		// COLLECT SUBTRACT ON DROP
		path = "objectives.collect.subtract-on-drop";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, false);
			wrongConfig(path);
		}
		QuestData.colSubOnDrop = this.config.getBoolean(path);
		
		// SHOW OBJECTIVES
		path = "quests.show-objectives";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, true);
			wrongConfig(path);
		}
		QuestData.showObjs = this.config.getBoolean(path);
		
		// PROGRES MESSAGES
		path = "quests.messages.start-show";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, true);
			wrongConfig(path);
		}
		QuestData.progMsgStart = this.config.getBoolean(path);
		
		path = "quests.messages.cancel-show";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, true);
			wrongConfig(path);
		}
		QuestData.progMsgCancel = this.config.getBoolean(path);
		
		path = "quests.messages.done-show";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, true);
			wrongConfig(path);
		}
		QuestData.progMsgDone = this.config.getBoolean(path);
		
		path = "quests.messages.objective-show";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, true);
			wrongConfig(path);
		}
		QuestData.progMsgObj = this.config.getBoolean(path);
	
		
		
		Map<Integer, String> rankMap = new HashMap<Integer, String>();
		List<Integer> sortedRanks = new ArrayList<Integer>();
		// QUESTER RANKS
		ConfigurationSection ranks = this.config.getConfigurationSection("ranks");
		if(ranks != null) {
			for(String key : ranks.getKeys(false)) {
				rankMap.put(ranks.getInt(key), key.replace('-', ' '));
				sortedRanks.add(ranks.getInt(key));
			}
		}
		if(sortedRanks.size() == 0) {
			wrongConfig("ranks");
			this.config.set("ranks.Quester", 0);
			rankMap.put(0, "Quester");
			sortedRanks.add(0);
			this.config.set("ranks.Apprentice-Quester", 25);
			rankMap.put(25, "Apprentice Quester");
			sortedRanks.add(25);
			this.config.set("ranks.Master-Quester", 50);
			rankMap.put(50, "Master Quester");
			sortedRanks.add(50);
		}
		Collections.sort(sortedRanks);
		QuestData.ranks = rankMap;
		QuestData.sortedRanks = sortedRanks;
		
		
		saveConfig();
	}

	@Override
	public boolean validate() {
		//Validate not needed, since keys and values are fixed during initialization
		return true;
	}

}
