package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import com.gmail.molnardad.quester.conditions.Condition;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.qevents.Qevent;
import com.gmail.molnardad.quester.rewards.Reward;
import com.gmail.molnardad.quester.utils.Util;
import com.gmail.molnardad.quester.QuestFlag;
@SerializableAs("QeusterQuest")
public class Quest implements ConfigurationSerializable{

	private List<Objective> objectives = null;
	private List<Reward> rewards = null;
	private List<Condition> conditions = null;
	private List<Qevent> qevents = null;
	private Set<String> worlds = null;
	private Set<QuestFlag> flags = null;
	private String description = null;
	private String name = null;
	private int ID = -1;
	
	
	public Quest(String name) {
		this.name = name;
		description = "";
		objectives = new ArrayList<Objective>();
		rewards = new ArrayList<Reward>();
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
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String newDescription) {
		description = newDescription;
	}
	
	public void addDescription(String toAdd) {
		description = (description + " " + toAdd).trim();
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

	public Reward getReward(int id) {
		if(id < rewards.size()){
			return rewards.get(id);
		}
		return null;
	}
	
	public List<Reward> getRewards() {
		return rewards;
	}
	
	public ArrayList<Reward> getRewards(String type) {
		ArrayList<Reward> result = new ArrayList<Reward>();
		for(Reward r: rewards){
			if(r.getType().equalsIgnoreCase(type)){
				result.add(r);
			}
		}
		return result;
	}
	
	public boolean removeReward(int id) {
		if(id < rewards.size() && id >= 0){
			rewards.remove(id);
			return true;
		}
		return false;
	}
	
	public void addReward(Reward newReward) {
		rewards.add(newReward);
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
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Integer, Objective> objs = new HashMap<Integer, Objective>();
		Map<Integer, Reward> rews = new HashMap<Integer, Reward>();
		Map<Integer, Condition> cons = new HashMap<Integer, Condition>();
		Map<Integer, Qevent> qvts = new HashMap<Integer, Qevent>();
		
		for(int i=0; i<objectives.size(); i++) {
			objs.put(i, objectives.get(i));
		}
		for(int i=0; i<rewards.size(); i++) {
			rews.put(i, rewards.get(i));
		}
		for(int i=0; i<conditions.size(); i++) {
			cons.put(i, conditions.get(i));
		}
		for(int i=0; i<qevents.size(); i++) {
			qvts.put(i, qevents.get(i));
		}
		
		map.put("name", name);
		if(!description.isEmpty())
			map.put("description", description);
		if(!worlds.isEmpty())
			map.put("worlds", worlds.toArray(new String[0]));
		if(!flags.isEmpty())
			map.put("flags", QuestFlag.serialize(flags));
		if(!objs.isEmpty())
			map.put("objectives", objs);
		if(!rews.isEmpty())
			map.put("rewards", rews);
		if(!cons.isEmpty())
			map.put("conditions", cons);
		if(!qvts.isEmpty())
			map.put("events", qvts);
		if(hasID())
			map.put("ID", ID);
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static Quest deserialize(Map<String, Object> map) {
		Quest quest;
		try {
			String name = (String) map.get("name");
			if(name != null)
				quest = new Quest(name);
			else
				return null;
			if(map.get("description") != null)
				quest.setDescription((String) map.get("description"));

			if(map.get("ID") != null) {
				int id = (Integer) map.get("ID");
				if(id >= 0) {
					quest.setID(id);
				}
			}
			
			if(map.get("flags") != null) {
				Set<QuestFlag> flags = QuestFlag.deserialize((String) map.get("flags"));
				for(QuestFlag f : flags) {
					quest.addFlag(f);
				}
			}
			
			if(map.get("worlds") != null) {
				List<String> strs = (List<String>) map.get("worlds");
				for(String s : strs) {
					if(s != null)
						quest.addWorld(s);
				}
			}
			
			Map<Integer, Objective> objs = new HashMap<Integer, Objective>();
			if(map.get("objectives") != null) {
				objs = (Map<Integer, Objective>) map.get("objectives");
				for(int i=0; i<objs.size(); i++) {
					if(objs.get(i) != null)
						quest.addObjective(objs.get(i));
				}
			}
			
			Map<Integer, Reward> rews = new HashMap<Integer, Reward>();
			if(map.get("rewards") != null) {
				rews = (Map<Integer, Reward>) map.get("rewards");
				for(int i=0; i<rews.size(); i++) {
					if(rews.get(i) != null)
					quest.addReward(rews.get(i));
				}
			}
			
			Map<Integer, Condition> cons = new HashMap<Integer, Condition>();
			if(map.get("conditions") != null) {
				cons = (Map<Integer, Condition>) map.get("conditions");
				for(int i=0; i<cons.size(); i++) {
					if(cons.get(i) != null)
					quest.addCondition(cons.get(i));
				}
			}
			
			Map<Integer, Qevent> qvts = new HashMap<Integer, Qevent>();
			if(map.get("events") != null) {
				qvts = (Map<Integer, Qevent>) map.get("events");
				for(int i=0; i<qvts.size(); i++) {
					if(qvts.get(i) != null)
					quest.addQevent(qvts.get(i));
				}
			}
			
		} catch (Exception e) {
			return null;
		}
		
		
		return quest;
	}
}
