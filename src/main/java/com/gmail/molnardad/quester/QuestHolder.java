package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gmail.molnardad.quester.exceptions.HolderException;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

public class QuestHolder {
	
	/**
	 * @uml.property  name="heldQuests"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.Integer"
	 */
	private List<Integer> heldQuests = new ArrayList<Integer>();
	/**
	 * @uml.property  name="name"
	 */
	private String name;
	/**
	 * @uml.property  name="interactions"
	 * @uml.associationEnd  qualifier="interacter:java.lang.String java.lang.Long"
	 */
	private Map<String, Long> interactions = new HashMap<String, Long>();
	/**
	 * @uml.property  name="selected"
	 * @uml.associationEnd  qualifier="name:java.lang.String java.lang.Integer"
	 */
	private Map<String, Integer> selected = new HashMap<String, Integer>();
	
	public QuestHolder(String name) {
		this.name = name;
	}
	
	public void interact(String interacter) {
		interactions.put(interacter, System.currentTimeMillis());
	}
	
	public boolean canInteract(String interacter) {
		if(interactions.get(interacter) == null) {
			return true;
		}
		return System.currentTimeMillis() - interactions.get(interacter) > 500;
	}
	
	public void setSelected(String name, int id) {
		selected.put(name, id);
	}
	
	public int getSelected(String name) {
		if(selected.get(name) == null) {
			selected.put(name, -1);
		}
		return selected.get(name);
	}
	
	public int getSelectedId(String name) {
		try {
			return heldQuests.get(selected.get(name));
		}
		catch (Exception ignore) {}
		return -1;
	}
	
	protected void setname(String newName) {
		name = newName;
	}
	
	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}
	
	public List<Integer> getQuests() {
		return heldQuests;
	}
	
	public void addQuest(int questID) {
		if(!heldQuests.contains(questID)) {
			heldQuests.add(questID);
		}
	}
	
	public void removeQuest(int questID) {
		for(int i=0; i<heldQuests.size(); i++) {
			if(questID == heldQuests.get(i)) {
				heldQuests.remove(i);
				break;
			}
		}
	}
	
	public void moveQuest(int from, int to) throws HolderException, IndexOutOfBoundsException {
		heldQuests.get(from);
		heldQuests.get(to);
		Util.moveListUnit(heldQuests, from, to);
	}
	
	public void serialize(StorageKey key) {
		if(!heldQuests.isEmpty()) {
			String str = "";
			boolean first = true;
			for(int i : heldQuests) {
				if(first) {
					str += String.valueOf(i);
					first = false;
				} else 
					str += "," + i;
			}
			key.setString("quests", str);
		}
		key.setString("name", name);
	}
	
	public static QuestHolder deserialize(StorageKey key) {
		QuestHolder qHolder = null;
		try{
			if(!key.hasSubKeys()) {
				return null;
			}
			String name = key.getString("name", "QuestHolder");
			String str = key.getString("quests", "");
			
			qHolder = new QuestHolder(name);
			String[] split = str.split(",");
			
			int i;
			for(String s : split) {
				try {
					i = Integer.parseInt(s);
					qHolder.addQuest(i);
				} catch (NumberFormatException f) {
				}
			}
			
		} catch (Exception ignore) {}
		
		return qHolder;
	}
}
