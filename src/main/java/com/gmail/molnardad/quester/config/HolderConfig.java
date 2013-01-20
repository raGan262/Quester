package com.gmail.molnardad.quester.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gmail.molnardad.quester.DataManager;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.QuesterSign;


public class HolderConfig extends CustomConfig {

	public HolderConfig(Quester plugin, String fileName) {
		super(plugin, fileName);
	}

	@Override
	public void saveConfig() {
		DataManager data = DataManager.getInstance();
		config.set("signs", null);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(QuesterSign sign : data.signs.values()) {
			list.add(sign.serialize());
		}
		config.set("signs", list);
		
		config.set("holders", null);
		for(int key : data.holderIds.keySet()) {
			config.set("holders." + key, data.holderIds.get(key).serialize());
		}
		super.saveConfig();
	}
	
}
