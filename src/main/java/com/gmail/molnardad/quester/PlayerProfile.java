package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gmail.molnardad.quester.managers.QConfiguration;
import com.gmail.molnardad.quester.storage.StorageKey;
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
	
	public void serialize(StorageKey key) {

		key.setString("name", name);
		
		key.removeKey("points");
		if(points != 0) {
			key.setInt("points", points);
		}
		
		key.removeKey("completed");
		if(!completed.isEmpty()) {
			StorageKey subKey = key.getSubKey("completed");
			for(String name : completed.keySet()) {
				subKey.setInt(name.replaceAll("\\.", "#%#"), completed.get(name));
			}
		}
		
		key.removeKey("active");
		if(quest != null) {
			int index = progresses.indexOf(quest);
			if(index > -1) {
				key.setInt("active", index);
			}
		}
		
		key.removeKey("quests");
		if(!progresses.isEmpty()) {
			StorageKey subKey = key.getSubKey("quests");
			for(Progress prg : progresses) {
				if(prg != null) {
					subKey.setString(prg.quest.replaceAll("\\.", "#%#"), Util.implodeInt(prg.progress.toArray(new Integer[0]), "|"));
				}
			}
		}
	}
	
	public static PlayerProfile deserialize(StorageKey key) {
		PlayerProfile prof = null;
		
		if(key.getString("name") != null) {
			prof = new PlayerProfile(key.getString("name"));
		}
		else {
			if(QConfiguration.debug) {
				Quester.log.info("Profile name not found.");
			}
			return null;
		}
		
		prof.addPoints(key.getInt("points", 0));
		
		if(key.getSubKey("completed").hasSubKeys()) {
			for(StorageKey subKey : key.getSubKey("completed").getSubKeys()) {
				prof.addCompleted(subKey.getName().replaceAll("#%#", "."), subKey.getInt("", 0));
			}
		}
		
		if(key.getString("quest") != null) {
			if(key.getString("progress") != null) {
				try {
					Progress prg = prof.new Progress(key.getString("quest"));
					String[] strs = key.getString("progress", "").split("\\|");
					for(String s : strs) {
						prg.addToProgress(Integer.parseInt(s));
					}
					prof.addQuest(prg);
					prof.setQuest(0);
				} catch (Exception e) {
					if(QConfiguration.verbose) {
						Quester.log.info("Invalid progress in profile.");
					}
				}
			}
			else {
				if(QConfiguration.verbose) {
					Quester.log.info("Invalid or missing progress for quest '" + key.getString("quest") + "' in profile.");
				}
			}
		}
		
		if(key.getSubKey("quests").hasSubKeys()) {
			for(StorageKey subKey : key.getSubKey("quests").getSubKeys()) {
				if(subKey.getString("") != null) {
					try {
						Progress prg = prof.new Progress(subKey.getName().replaceAll("#%#", "."));
						String[] strs = subKey.getString("").split("\\|");
						if(strs.length != 1 || !strs[0].isEmpty()) {
							for(String s : strs) {
								prg.addToProgress(Integer.parseInt(s));
							}
						}		
						prof.addQuest(prg);
					} catch (Exception e) {
						if(QConfiguration.verbose) {
							Quester.log.info("Invalid progress in profile.");
						}
					}
				}
				else {
					if(QConfiguration.verbose) {
						Quester.log.info("Invalid or missing progress for quest '" + key + "' in profile.");
					}
				}
			}
		}
		
		prof.setQuest(key.getInt("active"));
		
		return prof;
	}
}
