package com.gmail.molnardad.quester.profiles;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.quests.QuestManager;
import com.gmail.molnardad.quester.storage.StorageKey;

public class PlayerProfile {

	private final String name;
	private Map<String, Integer> completed;
	private WeakReference<Quest> selected;
	private int holder;
	private QuestProgress current;
	private List<QuestProgress> progresses;
	private int points;
	private String rank;
	private boolean changed;
	
	PlayerProfile(String player) {
		name = player;
		completed = new HashMap<String, Integer>();
		current = null;
		progresses = new ArrayList<QuestProgress>();
		selected = new WeakReference<Quest>(null);
		holder = -1;
		points = 0;
		rank = "";
		changed = false;
	}
	
	public String getName() {
		return name;
	}

	void addCompleted(String questName) {
		addCompleted(questName.toLowerCase(), 0);
	}

	void addCompleted(String questName, int time) {
		completed.put(questName.toLowerCase(), time);
		changed = true;
	}

	public String[] getCompletedQuests() {
		return completed.keySet().toArray(new String[0]);
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
	
	public boolean hasQuest(String questName) {
		return getQuestProgressIndex(questName) != -1;
	}
	
	public boolean hasQuest(Quest quest) {
		return getQuestProgressIndex(quest) != -1;
	}

	boolean setActiveQuest(int index) {
		try {
			current = progresses.get(index);
			changed = true;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	void refreshActive() {
		if(current == null) {
			setActiveQuest(0);
		}
	}

	public int getQuestProgressIndex() {
		return progresses.indexOf(current);
	}

	public int getQuestProgressIndex(Quest quest) {
		for(int i=0; i<progresses.size(); i++) {
			if(progresses.get(i).quest.equals(quest)) {
				return i;
			}
		}
		return -1;
	}
	
	public int getQuestProgressIndex(String questName) {
		for(int i=0; i<progresses.size(); i++) {
			if(progresses.get(i).quest.getName().equalsIgnoreCase(questName)) {
				return i;
			}
		}
		return -1;
	}

	void setSelected(Quest newSelected) {
		selected = new WeakReference<Quest>(newSelected);
	}

	public Quest getSelected() {
		return selected.get();
	}

	void setHolderID(int newID) {
		holder = newID;
	}

	public int getHolderID() {
		return holder;
	}

	int addPoints(int pts) {
		changed = true;
		points += pts;
		return points;
	}

	public int getPoints() {
		return points;
	}

	void setRank(String newRank) {
		rank = newRank;
	}

	public String getRank() {
		return rank;
	}

	void addQuest(Quest quest) {
		QuestProgress prg = new QuestProgress(quest);
		if(!progresses.contains(prg)) {
			progresses.add(prg);
			setActiveQuest(progresses.size()-1);
			changed = true;
		}
	}

	void unsetQuest() {
		try {
			progresses.remove(current);
			current = null;
			changed = true;
		} catch (Exception ignore) {}
	}

	void unsetQuest(int index) {
		try {
			if(progresses.get(index).equals(current)) {
				current = null;
			}
			progresses.remove(index);
			changed = true;
		} catch (Exception ignore) {}
	}

	public Quest getQuest() {
		if(current == null) {
			return null;
		}
		return current.quest;
	}

	public Quest getQuest(int index) {
		if(getProgress(index) == null) {
			return null;
		}
		return progresses.get(index).quest;
	}
	
	public QuestProgress getProgress() {
		return current;
	}

	public QuestProgress getProgress(int index) {
		if(index >= 0 && index < progresses.size()) {
			return progresses.get(index);
		}
		return null;
	}

	public QuestProgress[] getProgresses() {
		return progresses.toArray(new QuestProgress[0]);
	}

	private void addQuestProgress(QuestProgress prg) {
		if(!progresses.contains(prg)) {
			progresses.add(prg);
			changed = true;
		}
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	void setUnchanged() {
		changed = false;
	}

	void serialize(StorageKey key) {

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
		if(current != null) {
			int index = progresses.indexOf(current);
			if(index > -1) {
				key.setInt("active", index);
			}
		}
		
		key.removeKey("quests");
		if(!progresses.isEmpty()) {
			StorageKey subKey = key.getSubKey("quests");
			for(QuestProgress prg : progresses) {
				if(prg != null) {
					//subKey.setString(prg.quest.getName().replaceAll("\\.", "#%#"), Util.implodeInt(prg.progress.toArray(new Integer[0]), "|"));
					prg.serialize(subKey.getSubKey(String.valueOf(prg.quest.getID())));
				}
			}
		}
	}
	
	static PlayerProfile deserialize(StorageKey key, QuestManager qMan) {
		PlayerProfile prof = null;
		
		if(key.getString("name") != null) {
			prof = new PlayerProfile(key.getString("name"));
		}
		else {
			if(QConfiguration.verbose) {
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
		
//		OLD OLD FORMAT
//		if(key.getString("quest") != null) {
//			if(key.getString("progress") != null) {
//				try {
//					QuestProgress prg = prof.new QuestProgress(key.getString("quest"));
//					String[] strs = key.getString("progress", "").split("\\|");
//					for(int i=0; i < strs.length; i++) {
//						prg.addToProgress(Integer.parseInt(strs[i]));
//					}
//					prof.addQuestProgress(prg);
//					prof.setActiveQuest(0);
//				} catch (Exception e) {
//					if(QConfiguration.verbose) {
//						Quester.log.info("Invalid progress in profile.");
//					}
//				}
//			}
//			else {
//				if(QConfiguration.verbose) {
//					Quester.log.info("Invalid or missing progress for quest '" + key.getString("quest") + "' in profile.");
//				}
//			}
//		}
		
		if(key.getSubKey("quests").hasSubKeys()) {
			for(StorageKey subKey : key.getSubKey("quests").getSubKeys()) {
				Quest quest = null;
				try {
					quest = qMan.getQuest(Integer.parseInt(subKey.getName()));
				}
				catch (NumberFormatException e) {
					// compatibility with older format
					quest = qMan.getQuest(subKey.getName().replaceAll("#%#", "."));
				}
				if(quest == null) {
					continue;
				}
				QuestProgress prog = QuestProgress.deserialize(subKey, quest);
				if(prog == null) {
					Quester.log.info("Invalid quest progress in profile '" + prof.getName() + "'.");
				}
				else {
					prof.addQuestProgress(prog);
				}
			}
		}
		
		prof.setActiveQuest(key.getInt("active", 0));
		
		return prof;
	}
}
