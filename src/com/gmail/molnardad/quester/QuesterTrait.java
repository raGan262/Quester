package com.gmail.molnardad.quester;

import java.util.ArrayList;

import net.citizensnpcs.api.exception.NPCLoadException;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

public class QuesterTrait extends Trait {
	
	private int selected = -1;
	private ArrayList<String> quests = new ArrayList<String>();
	
	public QuesterTrait() {
		super("quester");
	}
	
	@Override
	public void load(DataKey key) throws NPCLoadException {
		DataKey qKey = key.getRelative("quests");
		Iterable<DataKey> qsts = qKey.getIntegerSubKeys();
		for(DataKey k : qsts) {
			quests.add(k.getString(""));
		}
	}

	@Override
	public void save(DataKey key) {
		DataKey qKey = key.getRelative("quests");
		if(qKey != null) {
			qKey.removeKey("");
		}
		int i = 0;
		for(String q : quests) {
			key.setString("quests." + i++, q);
		}
	}
	
	public int getSelected() {
		if(selected < quests.size() && selected >= 0) {
			return selected;
		} else {
			return -1;
		}
	}
	
	public void setSelected(int id) {
		if(id < quests.size() && id >= 0) {
			selected = id;
		} else {
			selected = -1;
		}
	}
	
	public String getSelectedName() {
		if(selected < 0 || selected >= quests.size()) {
			return "";
		} else {
			return quests.get(selected);
		}
	}
	
	public ArrayList<String> getQuests() {
		return quests;
	}
	
	public void addQuest(String quest) {
		if(!quests.contains(quest)) {
			quests.add(quest);
		}
	}
	
	public void removeQuest(int id) {
		if(id == selected) {
			selected = -1;
		}
		quests.remove(id);
	}
	
	public void removeQuest(String quest) {
		int id = quests.indexOf(quest);
		if(id >= 0) {
			quests.remove(id);
			if(selected == id)
				selected = -1;
		}
	}
	
	public void check() {
		for(int i=0; i < quests.size(); i++) {
			if(!Quester.qMan.isQuest(quests.get(i))) {
				removeQuest(i);
			}
		}
	}
}
