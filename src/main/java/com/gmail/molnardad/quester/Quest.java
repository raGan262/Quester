package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.conditions.Condition;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.qevents.Qevent;
import com.gmail.molnardad.quester.utils.Util;
import com.gmail.molnardad.quester.QuestFlag;

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
	
	public void setID(int newID) {
		ID = newID;
	}
	
	public boolean hasFlag(QuestFlag flag) {
		return flags.contains(flag);
	}
	
	public Set<QuestFlag> getFlags() {
		return flags;
	}
	
	public void addFlag(QuestFlag flag) {
		flags.add(flag);
	}
	
	public void removeFlag(QuestFlag flag) {
		flags.remove(flag);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public String getDescription(String playerName) {
		return ChatColor.translateAlternateColorCodes('&', description).replaceAll("%p", playerName);
	}
	
	public void setDescription(String newDescription) {
		description = newDescription;
	}
	
	public void addDescription(String toAdd) {
		description = (description + " " + toAdd).trim();
	}
	
	public boolean hasLocation() {
		return location != null;
	}
	
	public String getLocationString() {
		if(location != null)
			return String.format("%.1f %.1f %.1f("+location.getWorld().getName()+"), range: %d", location.getX(), location.getY(), location.getZ(), range);
		else
			return "none";
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location loc) {
		location = loc;
	}
	
	public int getRange() {
		return range;
	}
	
	public void setRange(int rng) {
		range = rng;
	}
	
	public Objective getObjective(int id) {
		if(id < objectives.size() && id >= 0){
			return objectives.get(id);
		}
		return null;
	}
	
	public List<Objective> getObjectives() {
		return objectives;
	}
	
	public ArrayList<Objective> getObjectives(String type) {
		ArrayList<Objective> result = new ArrayList<Objective>();
		for(Objective o: objectives){
			if(o.getType().equalsIgnoreCase(type)){
				result.add(o);
			}
		}
		return result;
	}
	
	public boolean removeObjective(int id) {
		if(id < objectives.size()){
			objectives.remove(id);
			return true;
		}
		return false;
	}
	
	public void addObjective(Objective newObjective) {
		objectives.add(newObjective);
	}

	public Condition getCondition(int id) {
		if(id < conditions.size()){
			return conditions.get(id);
		}
		return null;
	}
	
	public List<Condition> getConditions() {
		return conditions;
	}
	
	public boolean removeCondition(int id) {
		if(id < conditions.size() && id >= 0){
			conditions.remove(id);
			return true;
		}
		return false;
	}
	
	public void addCondition(Condition newCondition) {
		conditions.add(newCondition);
	}
	
	public Qevent getQevent(int id) {
		if(id < qevents.size()){
			return qevents.get(id);
		}
		return null;
	}
	
	public List<Qevent> getQevents() {
		return qevents;
	}
	
	public Map<Integer, Map<Integer, Qevent>> getQeventMap() {
		Map<Integer, Map<Integer, Qevent>> result = new HashMap<Integer, Map<Integer, Qevent>>();
		Qevent q = null;
		int occ = 0;
		for(int i=0; i<qevents.size(); i++) {
			q = qevents.get(i);
			occ = q.getOccasion();
			if(result.get(occ) == null) {
				result.put(occ, new HashMap<Integer, Qevent>());
			}
			result.get(occ).put(i, q);
		}
		return result;
	}
	
	public boolean removeQevent(int id) {
		if(id < qevents.size() && id >= 0){
			qevents.remove(id);
			return true;
		}
		return false;
	}
	
	public void addQevent(Qevent newQevent) {
		qevents.add(newQevent);
	}
	
	public String getWorldNames() {
		return Util.implode(worlds.toArray(new String[0]), ',');
	}
	
	public Set<String> getWorlds() {
		return worlds;
	}
	
	public void addWorld(String worldName) {
		worlds.add(worldName.toLowerCase());
	}
	
	public boolean removeWorld(String worldName) {
		if(!worlds.contains(worldName))
			return false;
		worlds.remove(worldName);
		return true;
	}
	
	public boolean allowedWorld(String worldName) {
		return (worlds.contains(worldName.toLowerCase()) || worlds.isEmpty());
	}
	
	public void serialize(ConfigurationSection section) {
		
		section.set("name", name);
		if(!description.isEmpty())
			section.set("description", description);
		if(location != null) {
			section.set("location", Util.serializeLocString(location));
			if(range > 1)
				section.set("range", range);
		}
		if(!worlds.isEmpty())
			section.set("worlds", worlds.toArray(new String[0]));
		if(!flags.isEmpty())
			section.set("flags", QuestFlag.serialize(flags));
		
		if(!objectives.isEmpty()) {
			ConfigurationSection subsection = section.createSection("objectives");
			for(int i=0; i<objectives.size(); i++) {
				objectives.get(i).serialize(subsection.createSection(String.valueOf(i)));
			}
		}
		if(!conditions.isEmpty()) {
			ConfigurationSection subsection = section.createSection("conditions");
			for(int i=0; i<conditions.size(); i++) {
				conditions.get(i).serialize(subsection.createSection(String.valueOf(i)));
			}
		}
		if(!qevents.isEmpty()) {
			ConfigurationSection subsection = section.createSection("events");
			for(int i=0; i<qevents.size(); i++) {
				qevents.get(i).serialize(subsection.createSection(String.valueOf(i)));
			}
		}
		
		if(hasID())
			section.set("ID", ID);
	}
	
	@SuppressWarnings("unchecked")
	public static Quest deserialize(ConfigurationSection section) {
		Quest quest;
		DataManager data = DataManager.getInstance();
		try {
			String name;
			if(section.isString("name")) {
				name = section.getString("name");
				quest = new Quest(name);
			} else
				return null;
			if(section.isString("description"))
				quest.setDescription((String) section.getString("description"));
			
			if(section.isString("location")) {
				quest.setLocation(Util.deserializeLocString(section.getString("location")));
				if(section.isInt("range")) {
					int rng = section.getInt("range");
					if(rng > 1)
						quest.setRange(rng);
				}
			}
			
			if(section.isInt("ID")) {
				int id = (Integer) section.getInt("ID");
				if(id >= 0) {
					quest.setID(id);
				}
			}
			
			if(section.isString("flags")) {
				Set<QuestFlag> flags = QuestFlag.deserialize(section.getString("flags"));
				for(QuestFlag f : flags) {
					quest.addFlag(f);
				}
			}
			
			if(section.isList("worlds")) {
				List<String> strs = (List<String>) section.getList("worlds", new ArrayList<String>());
				for(String s : strs) {
					if(s != null)
						quest.addWorld(s);
				}
			}

			if(data.debug)
				Quester.log.info("Deserializing objectives.");
			Objective obj = null;
			if(section.isConfigurationSection("objectives")) {
				ConfigurationSection subsection = section.getConfigurationSection("objectives");
				Set<String> keys = subsection.getKeys(false);
				for(int i=0; i<keys.size(); i++) {
					obj = Objective.deserialize(subsection.getConfigurationSection(String.valueOf(i)));
					if(obj != null) {
						quest.addObjective(obj);
						if(data.debug)
							Quester.log.info("Objective " + i + " OK.");
					} else
						Quester.log.severe("Error occured when deserializing objective ID " + i + " in quest '" + quest.getName() + "'.");
				}
			}

			if(data.debug)
				Quester.log.info("Deserializing conditions.");
			Condition con = null;
			if(section.isConfigurationSection("conditions")) {
				ConfigurationSection subsection = section.getConfigurationSection("conditions");
				Set<String> keys = subsection.getKeys(false);
				for(int i=0; i<keys.size(); i++) {
					con = Condition.deserialize(subsection.getConfigurationSection(String.valueOf(i)));
					if(con != null) {
						quest.addCondition(con);
						if(data.debug)
							Quester.log.info("Condition " + i + " OK.");
					} else
						Quester.log.severe("Error occured when deserializing condition ID " + i + " in quest '" + quest.getName() + "'.");
				}
			}

			if(data.debug)
				Quester.log.info("Deserializing events.");
			Qevent qvt = null;
			if(section.isConfigurationSection("events")) {
				ConfigurationSection subsection = section.getConfigurationSection("events");
				Set<String> keys = subsection.getKeys(false);
				for(int i=0; i<keys.size(); i++) {
					qvt = Qevent.deserialize(subsection.getConfigurationSection(String.valueOf(i)));
					if(qvt != null) {
						quest.addQevent(qvt);
						if(data.debug)
							Quester.log.info("Event " + i + " OK.");
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
}
