package com.gmail.molnardad.quester;

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
		
		// NO DROPS
		path = "objectives.break.no-drops";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, false);
			wrongConfig(path);
		}
		QuestData.noDrops = this.config.getBoolean(path);
		
		// ONLY FIRST
		path = "quests.only-first";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, true);
			wrongConfig(path);
		}
		QuestData.onlyFirst = this.config.getBoolean(path);
		
		path = "quests.show-objectives";
		if(this.config.getString(path) != "true" && this.config.getString(path) != "false") {
			this.config.set(path, true);
			wrongConfig(path);
		}
		QuestData.showObjs = this.config.getBoolean(path);
	
		saveConfig();
	}

	@Override
	public boolean validate() {
		//Validate not needed, since keys and values are fixed during initialization
		return true;
	}

}
