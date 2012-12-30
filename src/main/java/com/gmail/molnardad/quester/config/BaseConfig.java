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
	
	private void checkBoolean(String path) {
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, true);
			wrongConfig(path);
		}
	}
	
	@Override
	public void initialize() {
		
		String path;
		String temp;
		// VERBOSE-LOGGING
		path = "general.verbose-logging";
		checkBoolean(path);
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
		checkBoolean(path);
		QuestData.debug = this.config.getBoolean(path);
		
		// SHOW ONLY CURRENT
		path = "objectives.show-only-current";
		checkBoolean(path);
		QuestData.ordOnlyCurrent = this.config.getBoolean(path);
		
		// BREAK NO DROPS
		path = "objectives.break.no-drops";
		checkBoolean(path);
		QuestData.brkNoDrops = this.config.getBoolean(path);
		
		// BREAK SUBTRACT ON PLACE
		path = "objectives.break.subtract-on-place";
		checkBoolean(path);
		QuestData.brkSubOnPlace = this.config.getBoolean(path);
		
		// COLLECT REMOVE ON PICKUP
		path = "objectives.collect.remove-on-pickup";
		checkBoolean(path);
		QuestData.colRemPickup = this.config.getBoolean(path);
				
		// COLLECT SUBTRACT ON DROP
		path = "objectives.collect.subtract-on-drop";
		checkBoolean(path);
		QuestData.colSubOnDrop = this.config.getBoolean(path);
		
		// MAX QUESTS
		path = "quests.max-amount";
		if(this.config.getInt(path) < 1) {
			this.config.set(path, 1);
			wrongConfig(path);
		}
		QuestData.maxQuests = this.config.getInt(path);
		
		// PROGRES MESSAGES
		path = "quests.messages.start-show";
		checkBoolean(path);
		QuestData.progMsgStart = this.config.getBoolean(path);
		
		path = "quests.messages.cancel-show";
		checkBoolean(path);
		QuestData.progMsgCancel = this.config.getBoolean(path);
		
		path = "quests.messages.done-show";
		checkBoolean(path);
		QuestData.progMsgDone = this.config.getBoolean(path);
		
		path = "quests.messages.objective-show";
		checkBoolean(path);
		QuestData.progMsgObj = this.config.getBoolean(path);
	
		// COMMANDS
		
		path = "commands.displayed-cmd";
		temp = this.config.getString(path, "");
		if(!temp.equals("/q") && !temp.equals("/quest") && !temp.equals("/quester")) {
			this.config.set(path, "/q");
			wrongConfig(path);
		}
		QuestData.displayedCmd = this.config.getString(path);
		
		path = "commands.world-label-this";
		temp = this.config.getString(path, "");
		if(temp.isEmpty()) {
			this.config.set(path, "this");
			wrongConfig(path);
		}
		QuestData.worldLabelThis = this.config.getString(path);
		
		path = "commands.loc-label-here";
		temp = this.config.getString(path, "");
		if(temp.isEmpty()) {
			this.config.set(path, "here");
			wrongConfig(path);
		}
		QuestData.locLabelHere = this.config.getString(path);
		
		path = "commands.loc-label-player";
		temp = this.config.getString(path, "");
		if(temp.isEmpty()) {
			this.config.set(path, "player");
			wrongConfig(path);
		}
		QuestData.locLabelPlayer = this.config.getString(path);
		
		// QUESTER RANKS
		
		Map<Integer, String> rankMap = new HashMap<Integer, String>();
		List<Integer> sortedRanks = new ArrayList<Integer>();
		
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

}
