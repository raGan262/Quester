package com.gmail.molnardad.quester.config;

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
	public void saveConfig() {
		config.set("signs", null);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(QuesterSign sign : QuestData.signs.values()) {
			list.add(sign.serialize());
		}
		config.set("signs", list);
		
		config.set("holders", null);
		for(int key : QuestData.holderIds.keySet()) {
			config.set("holders." + key, QuestData.holderIds.get(key).serialize());
		}
		super.saveConfig();
	}
	
}
