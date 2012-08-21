package com.gmail.molnardad.quester.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.QuesterSign;


public class HolderConfig extends CustomConfig {

	public HolderConfig(String fileName) {
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
		config.set("signs", null);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(QuesterSign sign : QuestData.signs.values()) {
			list.add(sign.serialize());
		}
		config.set("signs", list);
		try {
			config.save(conFile);
		} catch (IOException ex) {
			plugin.getLogger().severe("Can't Write To File '" + conFile.getName() + "'!");
	    }
	}
	
}
