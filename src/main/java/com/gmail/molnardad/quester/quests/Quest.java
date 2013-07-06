package com.gmail.molnardad.quester.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;
import com.gmail.molnardad.quester.Quester;

public class Quest {

	private List<Objective> objectives = null;
	private List<Condition> conditions = null;
	private List<Qevent> qevents = null;
	private Set<String> worlds = null;
	private Set<QuestFlag> flags = null;
	private String description = null;
	private String name = null;
	private Location location = null;
	private int range = 1;
	private int ID = -1;
	
	
	public Quest(String name) {
		this.name = name;
		description = "";
		objectives = new ArrayList<Objective>();
		conditions = new ArrayList<Condition>();
		qevents = new ArrayList<Qevent>();
		worlds = new HashSet<String>();
		flags = new HashSet<QuestFlag>();
	}
	
	public boolean hasID() {
		return ID >= 0;
	}
	
	public int getID() {
		return ID;
	}
	
	void setID(int newID) {
		ID = newID;
	}
	
	public boolean hasFlag(QuestFlag flag) {
		return flags.contains(flag);
	}
	
	public Set<QuestFlag> getFlags() {
		return new HashSet<QuestFlag>(flags);
	}
	
	void addFlag(QuestFlag flag) {
		flags.add(flag);
	}
	
	void removeFlag(QuestFlag flag) {
		flags.remove(flag);
	}
	
	public String getName() {
		return name;
	}
	
	void setName(String newName) {
		name = newName;
	}
	
	public String getDescription(String playerName) {
		return ChatColor.translateAlternateColorCodes('&', description).replaceAll("%p", playerName);
	}
	
	void setDescription(String newDescription) {
		description = newDescription;
	}
	
	void addDescription(String toAdd) {
		description = (description + " " + toAdd).trim();
	}
	
	public boolean hasLocation() {
		return location != null;
	}
	
	public Location getLocation() {
		return location;
	}
	
	void setLocation(Location loc) {
		location = loc;
	}
	
	public int getRange() {
		return range;
	}
	
	void setRange(int rng) {
		range = rng;
	}
	
	public Objective getObjective(int id) {
		if(id < objectives.size() && id >= 0){
			return objectives.get(id);
		}
		return null;
	}
	
	public List<Objective> getObjectives() {
		return new ArrayList<Objective>(objectives);
	}
	
	public List<Objective> getObjectives(String type) {
		List<Objective> result = new ArrayList<Objective>();
		for(Objective o: objectives){
			if(o.getType().equalsIgnoreCase(type)){
				result.add(o);
			}
		}
		return result;
	}
	
	boolean removeObjective(int id) {
		if(id < objectives.size()){
			objectives.remove(id);
			return true;
		}
		return false;
	}
	
	void addObjective(Objective newObjective) {
		objectives.add(newObjective);
	}

	public Condition getCondition(int id) {
		if(id < conditions.size()){
			return conditions.get(id);
		}
		return null;
	}
	
	public List<Condition> getConditions() {
		return new ArrayList<Condition>(conditions);
	}
	
	boolean removeCondition(int id) {
		if(id < conditions.size() && id >= 0){
			conditions.remove(id);
			return true;
		}
		return false;
	}
	
	void addCondition(Condition newCondition) {
		conditions.add(newCondition);
	}
	
	public Qevent getQevent(int id) {
		if(id < qevents.size()){
			return qevents.get(id);
		}
		return null;
	}
	
	public List<Qevent> getQevents() {
		return new ArrayList<Qevent>(qevents);
	}
	
	public Map<Integer, Map<Integer, Qevent>> getQeventMap() {
		return getQeventMap(null);
	}
	
	public Map<Integer, Map<Integer, Qevent>> getQeventMap(String type) {
		Map<Integer, Map<Integer, Qevent>> result = new HashMap<Integer, Map<Integer, Qevent>>();
		Qevent q = null;
		int occ = 0;
		for(int i=0; i<qevents.size(); i++) {
			q = qevents.get(i);
			if(type == null || type.equalsIgnoreCase(q.getType())) {
				occ = q.getOccasion();
				if(result.get(occ) == null) {
					result.put(occ, new TreeMap<Integer, Qevent>());
				}
				result.get(occ).put(i, q);
			}
		}
		return result;
	}
	
	boolean removeQevent(int id) {
		if(id < qevents.size() && id >= 0){
			qevents.remove(id);
			return true;
		}
		return false;
	}
	
	void addQevent(Qevent newQevent) {
		qevents.add(newQevent);
	}
	
	public String getWorldNames() {
		return Util.implode(worlds.toArray(new String[0]), ',');
	}
	
	public Set<String> getWorlds() {
		return new HashSet<String>(worlds);
	}
	
	void addWorld(String worldName) {
		worlds.add(worldName.toLowerCase());
	}
	
	boolean removeWorld(String worldName) {
		if(!worlds.contains(worldName.toLowerCase()))
			return false;
		worlds.remove(worldName.toLowerCase());
		return true;
	}
	
	public boolean allowedWorld(String worldName) {
		return (worlds.contains(worldName.toLowerCase()) || worlds.isEmpty());
	}
	
	void serialize(StorageKey key) {
		
		key.setString("name", name);
		if(!description.isEmpty()) {
			key.setString("description", description);
		}
		if(location != null) {
			key.setString("location", Util.serializeLocString(location));
			if(range > 1) {
				key.setInt("range", range);
			}
		}
		if(!worlds.isEmpty()) {
			key.setRaw("worlds", worlds.toArray(new String[0]));
		}
		if(!flags.isEmpty()) {
			key.setString("flags", QuestFlag.serialize(flags));
		}
		
		if(!objectives.isEmpty()) {
			StorageKey subKey = key.getSubKey("objectives");
			for(int i=0; i<objectives.size(); i++) {
				objectives.get(i).serialize(subKey.getSubKey(String.valueOf(i)));
			}
		}
		if(!conditions.isEmpty()) {
			StorageKey subKey = key.getSubKey("conditions");
			for(int i=0; i<conditions.size(); i++) {
				conditions.get(i).serialize(subKey.getSubKey(String.valueOf(i)));
			}
		}
		if(!qevents.isEmpty()) {
			StorageKey subKey = key.getSubKey("events");
			for(int i=0; i<qevents.size(); i++) {
				qevents.get(i).serialize(subKey.getSubKey(String.valueOf(i)));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	static Quest deserialize(StorageKey key) {
		Quest quest;
		try {
			String name;
			if(key.getString("name") != null) {
				name = key.getString("name");
				quest = new Quest(name);
			} 
			else {
				return null;
			}
			if(key.getString("description") != null) {
				quest.setDescription(key.getString("description"));
			}
			
			if(key.getString("location") != null) {
				quest.setLocation(Util.deserializeLocString(key.getString("location")));
				if(key.getInt("range", 1) > 1) {
					quest.setRange(key.getInt("range"));
				}
			}
			
			int id = key.getInt("ID", -1);
			if(id < 0) {
				try {
					id = Integer.parseInt(key.getName());
				}
				catch (NumberFormatException ignore) {}
			}
			if(id >= 0) {
				quest.setID(id);
			}
			
			if(key.getString("flags") != null) {
				Set<QuestFlag> flags = QuestFlag.deserialize(key.getString("flags"));
				for(QuestFlag f : flags) {
					quest.addFlag(f);
				}
			}
			
			if(key.getRaw("worlds") instanceof List) {
				List<String> strs = (List<String>) key.getRaw("worlds", new ArrayList<String>());
				for(String s : strs) {
					if(s != null)
						quest.addWorld(s);
				}
			}

			Objective obj = null;
			if(key.getSubKey("objectives").hasSubKeys()) {
				StorageKey subKey = key.getSubKey("objectives");
				List<StorageKey> keys = subKey.getSubKeys();
				for(int i=0; i<keys.size(); i++) {
					obj = Objective.deserialize(subKey.getSubKey(String.valueOf(i)));
					if(obj != null) {
						quest.addObjective(obj);
					} else
						Quester.log.severe("Error occured when deserializing objective ID " + i + " in quest '" + quest.getName() + "'.");
				}
			}

			Condition con = null;
			if(key.getSubKey("conditions").hasSubKeys()) {
				StorageKey subKey = key.getSubKey("conditions");
				List<StorageKey> keys = subKey.getSubKeys();
				for(int i=0; i<keys.size(); i++) {
					con = Condition.deserialize(subKey.getSubKey(String.valueOf(i)));
					if(con != null) {
						quest.addCondition(con);
					} else
						Quester.log.severe("Error occured when deserializing condition ID " + i + " in quest '" + quest.getName() + "'.");
				}
			}

			Qevent qvt = null;
			if(key.getSubKey("events").hasSubKeys()) {
				StorageKey subKey = key.getSubKey("events");
				List<StorageKey> keys = subKey.getSubKeys();
				for(int i=0; i<keys.size(); i++) {
					qvt = Qevent.deserialize(subKey.getSubKey(String.valueOf(i)));
					if(qvt != null) {
						quest.addQevent(qvt);
					} else
						Quester.log.severe("Error occured when deserializing event ID:" + i + " in quest '" + quest.getName() + "'.");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
		return quest;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof Quest) {
			Quest other = (Quest) obj;
			return name.equals(other.name) 
					&& ID == other.ID
					&& flags.equals(other.flags);
		}
		return false;
	}
}
