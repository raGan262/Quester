package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.managers.DataManager;
import com.gmail.molnardad.quester.utils.Util;

public class PlayerProfile {

	private final String name;
	private Map<String, Integer> completed;
	private int selected;
	private int holder;
	private Progress quest;
	private List<Progress> progresses;
	private int points;
	private String rank;
	
	private class Progress {
		String quest = "";
		List<Integer> progress;
		
		Progress(String quest) {
			if(quest != null) {
				this.quest = quest;
			}
			progress = new ArrayList<Integer>();
		}
		
		Progress(String quest, int numObjs) {
			if(quest != null) {
				this.quest = quest;
			}
			if(numObjs > 0) {
				progress = new ArrayList<Integer>();
				for(int i=0; i<numObjs; i++) {
					progress.add(0);
				}
			}
			else {
				progress = new ArrayList<Integer>();
			}
		}
		
		void addToProgress(int value) {
			progress.add(value);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj != null && obj instanceof Progress) {
				Progress prg = (Progress) obj;
				return this.quest.equalsIgnoreCase(prg.quest);
			}
			return false;
		}
	}
	
	public PlayerProfile(String player) {
		name = player;
		completed = new HashMap<String, Integer>();
		quest = null;
		progresses = new ArrayList<Progress>();
		selected = -1;
		holder = -1;
		points = 0;
		rank = "";
	}
	
	public String getName() {
		return name;
	}
	
	public String getCompletedNames() {
		return Util.implode(completed.keySet().toArray(new String[0]), ',');
	}
	
	public void addCompleted(String questName) {
		addCompleted(questName.toLowerCase(), 0);
	}
	
	public void addCompleted(String questName, int time) {
		completed.put(questName.toLowerCase(), time);
	}
	
	public boolean isCompleted(String questName) {
		return completed.containsKey(questName.toLowerCase());
	}
	
	public int getCompletionTime(String questName) {
		Integer time = completed.get(questName.toLowerCase());
		if(time == null) {
			return 0;
		}
		return time;
	}
	
	public int getQuestAmount() {
		return progresses.size();
	}
	
	public boolean setQuest(int index) {
		try {
			quest = progresses.get(index);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public boolean setQuest(String questName) {
		for(int i=0; i<progresses.size(); i++) {
			if(progresses.get(i) != null && questName.equalsIgnoreCase(progresses.get(i).quest)) {
				quest = progresses.get(i);
				return true;
			}
		}
		return false;
	}
	
	public void refreshActive() {
		if(quest == null) {
			setQuest(0);
		}
	}
	
	public void addQuest(String questName, int progSize) {
		Progress prg = new Progress(questName, progSize);
		if(!progresses.contains(prg)) {
			progresses.add(prg);
			setQuest(progresses.size()-1);
		}
	}
	
	private void addQuest(Progress prg) {
		if(!progresses.contains(prg)) {
			progresses.add(prg);
		}
	}

	public void unsetQuest() {
		try {
			progresses.remove(quest);
			quest = null;
		} catch (Exception ignore) {}
	}
	
	public void unsetQuest(int index) {
		try {
			if(progresses.get(index).equals(quest)) {
				quest = null;
			}
			progresses.remove(index);
		} catch (Exception ignore) {}
	}
	
	public void unsetQuest(String questName) {
		try {
			Progress prg = new Progress(questName);
			progresses.remove(prg);
			if(prg.equals(quest)) {
				quest = null;
			}
		} catch (Exception ignore) {}
	}
	
	public String getQuest() {
		try {
			return quest.quest;
		} catch (Exception ignore) {}
		return "";
	}
	
	public String getQuest(int index) {
		try {
			return progresses.get(index).quest;
		} catch (Exception ignore) {}
		return "";
	}
	
	public boolean hasQuest(String questName) {
		for(int i=0; i<progresses.size(); i++) {
			if(progresses.get(i) != null && questName.equalsIgnoreCase(progresses.get(i).quest)) {
				return true;
			}
		}
		return false;
	}
	
	public int getActiveIndex() {
		return progresses.indexOf(quest);
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
	
	public List<Integer> getProgress() {
		if(quest == null) {
			return null;
		}
		return quest.progress;
	}
	
	public List<Integer> getProgress(int index) {
		try {
			return progresses.get(index).progress;
		} catch (Exception ignore) {}
		return null;
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
		
		section.set("points", null);
		if(points != 0) {
			section.set("points", points);
		}
		
		section.set("completed", null);
		if(!completed.isEmpty()) {
			ConfigurationSection subsection = section.createSection("completed");
			for(String name : completed.keySet()) {
				subsection.set(name, completed.get(name));
			}
		}
		
		section.set("active", null);
		if(quest != null) {
			int index = progresses.indexOf(quest);
			if(index > -1) {
				section.set("active", index);
			}
		}
		
		section.set("quests", null);
		if(!progresses.isEmpty()) {
			ConfigurationSection prgs = section.createSection("quests");
			for(Progress prg : progresses) {
				if(prg != null) {
					prgs.set(prg.quest, Util.implodeInt(prg.progress.toArray(new Integer[0]), "|"));
				}
			}
		}
	}
	
	public static PlayerProfile deserialize(ConfigurationSection section) {
		PlayerProfile prof = null;
		DataManager data = DataManager.getInstance();
		
		if(section.isString("name")) {
			prof = new PlayerProfile(section.getString("name"));
		}
		else {
			if(data.debug) {
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
		else if(section.isConfigurationSection("completed")) {
			ConfigurationSection subsection = section.getConfigurationSection("completed");
			for(String key : subsection.getKeys(false)) {
				prof.addCompleted(key, subsection.getInt(key, 0));
			}
		}
		
		if(section.isString("quest")) {
			if(section.isString("progress")) {
				try {
					Progress prg = prof.new Progress(section.getString("quest"));
					String[] strs = section.getString("progress", "").split("\\|");
					for(String s : strs) {
						prg.addToProgress(Integer.parseInt(s));
					}
					prof.addQuest(prg);
					prof.setQuest(0);
				} catch (Exception e) {
					if(data.verbose) {
						Quester.log.info("Invalid progress in profile.");
					}
				}
			}
			else {
				if(data.verbose) {
					Quester.log.info("Invalid or missing progress for quest '" + section.getString("quest", "non-existant") + "' in profile.");
				}
			}
		}
		
		if(section.isConfigurationSection("quests")) {
			ConfigurationSection subsection = section.getConfigurationSection("quests");
			for(String key : subsection.getKeys(false)) {
				if(subsection.isString(key)) {
					try {
						Progress prg = prof.new Progress(key);
						String[] strs = subsection.getString(key).split("\\|");
						if(strs.length != 1 || !strs[0].isEmpty()) {
							for(String s : strs) {
								prg.addToProgress(Integer.parseInt(s));
							}
						}		
						prof.addQuest(prg);
					} catch (Exception e) {
						if(data.verbose) {
							Quester.log.info("Invalid progress in profile.");
						}
					}
				}
				else {
					if(data.verbose) {
						Quester.log.info("Invalid or missing progress for quest '" + key + "' in profile.");
					}
				}
			}
		}
		
		prof.setQuest(section.getInt("active"));
		
		return prof;
	}
}
