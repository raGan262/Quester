package me.ragan262.quester.holder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ragan262.quester.exceptions.HolderException;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Util;

public class QuestHolder {
	
	private final List<Integer> heldQuests = new ArrayList<Integer>();
	private String name;
	private final Map<String, Long> interactions = new HashMap<String, Long>();
	private final Map<String, Integer> selected = new HashMap<String, Integer>();
	private int id = -1;
	
	public QuestHolder(final String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	void setId(final int newId) {
		id = newId;
	}
	
	public void interact(final String interacter) {
		interactions.put(interacter, System.currentTimeMillis());
	}
	
	public boolean canInteract(final String interacter) {
		if(interactions.get(interacter) == null) {
			return true;
		}
		return System.currentTimeMillis() - interactions.get(interacter) > 500;
	}
	
	public void setSelected(final String name, final int id) {
		selected.put(name, id);
	}
	
	public int getSelected(final String name) {
		if(selected.get(name) == null) {
			selected.put(name, -1);
		}
		return selected.get(name);
	}
	
	public int getSelectedId(final String name) {
		try {
			return heldQuests.get(selected.get(name));
		}
		catch(final Exception ignore) {}
		return -1;
	}
	
	protected void setName(final String newName) {
		name = newName;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Integer> getQuests() {
		return heldQuests;
	}
	
	public void addQuest(final int questID) {
		if(!heldQuests.contains(questID)) {
			heldQuests.add(questID);
		}
	}
	
	public void removeQuest(final int questID) {
		for(int i = 0; i < heldQuests.size(); i++) {
			if(questID == heldQuests.get(i)) {
				heldQuests.remove(i);
				break;
			}
		}
	}
	
	public void moveQuest(final int from, final int to) throws HolderException, IndexOutOfBoundsException {
		heldQuests.get(from);
		heldQuests.get(to);
		Util.moveListUnit(heldQuests, from, to);
	}
	
	public void serialize(final StorageKey key) {
		if(!heldQuests.isEmpty()) {
			String str = "";
			boolean first = true;
			for(final int i : heldQuests) {
				if(first) {
					str += String.valueOf(i);
					first = false;
				}
				else {
					str += "," + i;
				}
			}
			key.setString("quests", str);
		}
		key.setString("name", name);
	}
	
	public static QuestHolder deserialize(final StorageKey key) {
		QuestHolder qHolder = null;
		try {
			if(!key.hasSubKeys()) {
				return null;
			}
			final String name = key.getString("name", "QuestHolder");
			final String str = key.getString("quests", "");
			
			qHolder = new QuestHolder(name);
			final String[] split = str.split(",");
			
			int i;
			for(final String s : split) {
				try {
					i = Integer.parseInt(s);
					qHolder.addQuest(i);
				}
				catch(final NumberFormatException f) {}
			}
			
		}
		catch(final Exception ignore) {}
		
		return qHolder;
	}
}
