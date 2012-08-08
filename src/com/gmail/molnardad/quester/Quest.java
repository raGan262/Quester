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
@SerializableAs("QeusterQuest")
public class Quest implements ConfigurationSerializable{

	private List<Objective> objectives = null;
	private List<Reward> rewards = null;
	private List<Condition> conditions = null;
	private List<Qevent> qevents = null;
	private Set<String> worlds = null;
	private String description = null;
	private String name = null;
	private boolean active = false;
	private boolean ordered = false;
	
	
	public Quest(String name) {
		this.name = name;
		description = "";
		objectives = new ArrayList<Objective>();
		rewards = new ArrayList<Reward>();
		conditions = new ArrayList<Condition>();
		qevents = new ArrayList<Qevent>();
		worlds = new HashSet<String>();
		ordered = false;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void activate() {
		active = true;
	}
	
	public void deactivate() {
		active = false;
	}
	
	public boolean isOrdered() {
		return ordered;
	}
	
	public void setOrdered(boolean state) {
		ordered = state;
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
		map.put("description", description);
		map.put("active", active);
		map.put("ordered", ordered);
		map.put("worlds", worlds.toArray(new String[0]));
		map.put("objectives", objs);
		map.put("rewards", rews);
		map.put("conditions", cons);
		map.put("events", qvts);
		
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
			
			quest.setDescription((String) map.get("description"));
			
			if((Boolean) map.get("active"))
				quest.activate();
			
			if(map.get("ordered") != null)
				quest.setOrdered((Boolean) map.get("ordered"));
			
			if(map.get("worlds") != null) {
				List<String> strs = (List<String>) map.get("worlds");
				for(String s : strs) {
					quest.addWorld(s);
				}
			}
			
			Map<Integer, Objective> objs = new HashMap<Integer, Objective>();
			if(map.get("objectives") != null) {
				objs = (Map<Integer, Objective>) map.get("objectives");
				for(int i=0; i<objs.size(); i++) {
					quest.addObjective(objs.get(i));
				}
			}
			
			Map<Integer, Reward> rews = new HashMap<Integer, Reward>();
			if(map.get("rewards") != null) {
				rews = (Map<Integer, Reward>) map.get("rewards");
				for(int i=0; i<rews.size(); i++) {
					quest.addReward(rews.get(i));
				}
			}
			
			Map<Integer, Condition> cons = new HashMap<Integer, Condition>();
			if(map.get("conditions") != null) {
				cons = (Map<Integer, Condition>) map.get("conditions");
				for(int i=0; i<cons.size(); i++) {
					quest.addCondition(cons.get(i));
				}
			}
			
			Map<Integer, Qevent> qvts = new HashMap<Integer, Qevent>();
			if(map.get("events") != null) {
				qvts = (Map<Integer, Qevent>) map.get("events");
				for(int i=0; i<qvts.size(); i++) {
					quest.addQevent(qvts.get(i));
				}
			}
			
		} catch (Exception e) {
			return null;
		}
		
		
		return quest;
	}
}
