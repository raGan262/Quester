package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import com.gmail.molnardad.quester.conditions.Condition;
import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.rewards.Reward;

@SerializableAs("QeusterQuest")
public class Quest implements ConfigurationSerializable{

	private ArrayList<Objective> objectives = null;
	private ArrayList<Reward> rewards = null;
	private ArrayList<Condition> conditions = null;
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
	
	public ArrayList<Objective> getObjectives() {
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
	
	public ArrayList<Reward> getRewards() {
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
	
	public ArrayList<Condition> getConditions() {
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
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Integer, Objective> objs = new HashMap<Integer, Objective>();
		Map<Integer, Reward> rews = new HashMap<Integer, Reward>();
		Map<Integer, Condition> cons = new HashMap<Integer, Condition>();
		
		for(int i=0; i<objectives.size(); i++) {
			objs.put(i, objectives.get(i));
		}
		for(int i=0; i<rewards.size(); i++) {
			rews.put(i, rewards.get(i));
		}
		for(int i=0; i<conditions.size(); i++) {
			cons.put(i, conditions.get(i));
		}
		
		map.put("name", name);
		map.put("description", description);
		map.put("active", active);
		map.put("ordered", ordered);
		map.put("objectives", objs);
		map.put("rewards", rews);
		map.put("conditions", cons);
		
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
			
		} catch (Exception e) {
			return null;
		}
		
		
		return quest;
	}
}
