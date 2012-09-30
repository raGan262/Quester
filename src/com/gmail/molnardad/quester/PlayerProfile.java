package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import com.gmail.molnardad.quester.utils.Util;

@SerializableAs("QuesterPlayerProfile")
public class PlayerProfile implements ConfigurationSerializable{

	private final String name;
	private Set<String> completed;
	private int selected;
	private int holder;
	private String quest;
	private List<Integer> progress;
	private int points;
	private String rank;
	
	public PlayerProfile(String player) {
		name = player;
		completed = new HashSet<String>();
		quest = "";
		progress = new ArrayList<Integer>();
		selected = -1;
		holder = -1;
		points = 0;
		rank = "";
	}
	
	public String getName() {
		return name;
	}
	
	public String getCompletedNames() {
		return Util.implode(completed.toArray(new String[0]), ',');
	}
	
	public void addCompleted(String questName) {
		completed.add(questName.toLowerCase());
	}
	
	public Set<String> getCompleted() {
		return completed;
	}
	
	private void setQuest(String questName) {
		quest = questName;
	}
	
	public void setQuest(String questName, int progSize) {
		quest = questName;
		progress.clear();
		for(int i=0; i<progSize; i++) {
			progress.add(0);
		}
	}

	public void unsetQuest() {
		quest = "";
		progress = new ArrayList<Integer>();
	}
	
	public String getQuest() {
		return quest;
	}
	
	public int getSelected() {
		return selected;
	}
	
	public void setSelected(int newSelected) {
		selected = newSelected;
	}
	
	public int getHolderID() {
		return holder;
	}
	
	public void setHolderID(int newID) {
		holder = newID;
	}
	
	private void setProgress(List<Integer> list) {
		progress = list;
	}
	
	public List<Integer> getProgress() {
		return progress;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void addPoints(int pts) {
		points += pts;
	}
	
	public String getRank() {
		return rank;
	}
	
	public void setRank(String newRank) {
		rank = newRank;
	}
	
	public void serialize(ConfigurationSection section) {
		
		section.set("name", name);
		if(points != 0) {
			section.set("points", points);
		}
		
		if(!completed.isEmpty()) {
			section.set("completed", completed.toArray(new String[0]));
		}
		
		if(!quest.isEmpty()) {
			section.set("quest", quest);
			
			section.set("progress", Util.implodeInt(progress.toArray(new Integer[0]), "|"));
		}
	}

	@SuppressWarnings("unchecked")
	public static PlayerProfile deserialize(Map<String, Object> map) {
		PlayerProfile profile;
		String buildName;
		if(map.get("name") != null) {
			buildName = (String) map.get("name");
			profile = new PlayerProfile(buildName);
		} else {
			return null;
		}
		
		if(map.get("points") != null) {
			int pts = (Integer) map.get("points");
			profile.addPoints(pts);
		}
		
		if(map.get("completed") != null) {
			List<String> strings = (List<String>) map.get("completed");
			for(String s : strings) {
				profile.addCompleted(s);
			}
		}
		
		if(map.get("quest") != null) {
			String qst = (String) map.get("quest");
			if(map.get("progress") != null) {
				
				Map<Integer, Integer> intmap;
				try {
					intmap = (Map<Integer, Integer>) map.get("progress");
				} catch (Exception e) {
					return profile;
				}
				
				List<Integer> list = new ArrayList<Integer>();
				for(int i=0; i<intmap.size(); i++) {
					if(intmap.get(i) == null) {
						return profile;
					}
					list.add(intmap.get(i));
				}
				
				profile.setQuest(qst);
				profile.setProgress(list);
			}
		}
		
		return profile;
	}
	
	public static PlayerProfile sectionDeserialize(ConfigurationSection section) {
		PlayerProfile prof = null;
		
		if(section.isString("name")) {
			prof = new PlayerProfile(section.getString("name"));
		}
		else {
			if(QuestData.debug) {
				Quester.log.info("Profile name not found.");
			}
			return null;
		}
		
		if(section.isInt("points")) {
			prof.addPoints(section.getInt("points"));
		}
		
		if(section.isList("completed")) {
			List<String> l = section.getStringList("completed");
			for(String s : l) {
				prof.addCompleted(s);
			}
		}
		
		if(section.isString("quest")) {
			if(section.isString("progress")) {
				try {
					String[] strs = section.getString("progress").split("\\|");
					List<Integer> list = new ArrayList<Integer>();
					for(String s : strs) {
						list.add(Integer.parseInt(s));
					}
					prof.setQuest(section.getString("quest"));
					prof.setProgress(list);
				} catch (Exception e) {
					if(QuestData.debug) {
						Quester.log.info("Invalid progress in profile.");
					}
				}
			}
			else {
				if(QuestData.debug) {
					Quester.log.info("Invalid or missing progress in profile.");
				}
			}
		}
		
		return prof;
	}

	// needs to be here to remain configuration serializable
	@Override
	public Map<String, Object> serialize() {
		return new HashMap<String, Object>();
	}
}
