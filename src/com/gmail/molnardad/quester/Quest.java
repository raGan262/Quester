package com.gmail.molnardad.quester;

import java.io.Serializable;
import java.util.ArrayList;

import com.gmail.molnardad.quester.objectives.Objective;
import com.gmail.molnardad.quester.rewards.Reward;

public class Quest implements Serializable{

	private static final long serialVersionUID = 3193159778020746517L;
	private ArrayList<Objective> objectives = null;
	private ArrayList<Reward> rewards = null;
	private String description = null;
	private String name = null;
	private boolean active = false;
	private String permission = "";
	
	
	public Quest(String name) {
		this.name = name;
		description = "";
		objectives = new ArrayList<Objective>();
		rewards = new ArrayList<Reward>();
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
	
	public String getPermission() {
		//TODO
		return permission;
	}
}
