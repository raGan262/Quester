package me.ragan262.quester.profiles;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;

import me.ragan262.quester.profiles.ProfileImage.ProfileImageBuilder;
import me.ragan262.quester.profiles.QuestProgress.ProgressImage;
import me.ragan262.quester.quests.Quest;
import me.ragan262.quester.quests.QuestManager;
import me.ragan262.quester.utils.Ql;
import org.bukkit.OfflinePlayer;

public class PlayerProfile {
	
	private final String name;
	private final UUID uid;
	private final Map<String, Integer> completed = new HashMap<>();
	private WeakReference<Quest> selected = new WeakReference<>(null);
	private int holder = -1;
	private QuestProgress current = null;
	private final List<QuestProgress> progresses = new ArrayList<>();
	private int points = 0;
	private String rank = "";
	private String language = "";
	
	private boolean dirty = true;
	private long lastTouch = System.currentTimeMillis();
	
	PlayerProfile(final OfflinePlayer player) {
		name = player.getName();
		uid = player.getUniqueId();
	}
	
	PlayerProfile(final ProfileImage image, final QuestManager qMan) {
		name = image.name;
		uid = image.uid;
		language = image.language;
		final Integer i = image.reputation.get("default");
		if(i != null) {
			points = i;
		}
		
		for(final Entry<String, Integer> e : image.completed.entrySet()) {
			completed.put(e.getKey(), e.getValue());
		}
		
		for(final Entry<Integer, ProgressImage> e : image.progresses.entrySet()) {
			final Quest q = qMan.getQuest(e.getKey());
			try {
				final QuestProgress prog = new QuestProgress(q, e.getValue());
				progresses.add(prog);
				if(image.active == e.getKey()) {
					current = prog;
				}
			}
			catch(final Exception ex) {
				if(q == null) {
					Ql.debug("Quest (" + e.getKey() + ") to create progress for in profile " + name
							+ " no longer exists.");
				}
				else {
					Ql.debug("Progress for quest '" + q.getName() + "' (" + e.getKey()
							+ ") in profile " + name + " is not matching the quest.");
				}
			}
		}
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	void setDirty(final boolean value) {
		dirty = value;
		if(dirty) {
			lastTouch = System.currentTimeMillis();
		}
	}
	
	public long getAge() {
		return System.currentTimeMillis() - lastTouch;
	}
	
	public String getName() {
		return name;
	}
	
	public UUID getId() {
		return uid;
	}
	
	void addCompleted(final String questName) {
		addCompleted(questName.toLowerCase(), 0);
		setDirty(true);
	}
	
	void addCompleted(final String questName, final int time) {
		completed.put(questName.toLowerCase(), time);
		setDirty(true);
	}
	
	void removeCompleted(final String questName) {
		if(completed.remove(questName.toLowerCase()) != null) {
			setDirty(true);
		}
	}
	
	public String[] getCompletedQuests() {
		Set<String> compl = completed.keySet();
		return compl.toArray(new String[compl.size()]);
	}
	
	public boolean isCompleted(final String questName) {
		return completed.containsKey(questName.toLowerCase());
	}
	
	public int getCompletionTime(final String questName) {
		final Integer time = completed.get(questName.toLowerCase());
		if(time == null) {
			return 0;
		}
		return time;
	}
	
	public int getQuestAmount() {
		return progresses.size();
	}
	
	public boolean hasQuest(final String questName) {
		return getQuestProgressIndex(questName) != -1;
	}
	
	public boolean hasQuest(final Quest quest) {
		return getQuestProgressIndex(quest) != -1;
	}
	
	boolean setActiveQuest(final int index) {
		try {
			current = progresses.get(index);
			setDirty(true);
		}
		catch(final Exception e) {
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
	
	public int getQuestProgressIndex(final Quest quest) {
		for(int i = 0; i < progresses.size(); i++) {
			if(progresses.get(i).quest.equals(quest)) {
				return i;
			}
		}
		return -1;
	}
	
	public int getQuestProgressIndex(final String questName) {
		for(int i = 0; i < progresses.size(); i++) {
			if(progresses.get(i).quest.getName().equalsIgnoreCase(questName)) {
				return i;
			}
		}
		return -1;
	}
	
	void setSelected(final Quest newSelected) {
		selected = new WeakReference<>(newSelected);
	}
	
	public Quest getSelected() {
		return selected.get();
	}
	
	void setHolderID(final int newID) {
		holder = newID;
	}
	
	public int getHolderID() {
		return holder;
	}
	
	int addPoints(final int pts) {
		points += pts;
		setDirty(true);
		return points;
	}
	
	public int getPoints() {
		return points;
	}
	
	void setRank(final String newRank) {
		rank = newRank;
	}
	
	public String getRank() {
		return rank;
	}
	
	void addQuest(final Quest quest) {
		final QuestProgress prg = new QuestProgress(quest);
		if(!progresses.contains(prg)) {
			progresses.add(prg);
			setActiveQuest(progresses.size() - 1);
			setDirty(true);
		}
	}
	
	void unsetQuest() {
		if(progresses.remove(current)) {
			current = null;
			setDirty(true);
		}
	}
	
	void unsetQuest(final int index) {
		if(index < 0 || index >= progresses.size()) {
			return;
		}
		final QuestProgress removed = progresses.remove(index);
		if(removed != null) {
			setDirty(true);
			if(removed.equals(current)) {
				current = null;
			}
		}
	}
	
	public Quest getQuest() {
		if(current == null) {
			return null;
		}
		return current.quest;
	}
	
	public Quest getQuest(final int index) {
		if(getProgress(index) == null) {
			return null;
		}
		return progresses.get(index).quest;
	}
	
	public QuestProgress getProgress() {
		return current;
	}
	
	public QuestProgress getProgress(final int index) {
		if(index >= 0 && index < progresses.size()) {
			return progresses.get(index);
		}
		return null;
	}
	
	public QuestProgress[] getProgresses() {
		return progresses.toArray(new QuestProgress[progresses.size()]);
	}
	
	public String getLanguage() {
		return language;
	}
	
	void setLanguage(final String newLang) {
		if(newLang != null) {
			language = newLang;
			setDirty(true);
		}
	}
	
	public ProfileImage getProfileImage() {
		final ProfileImageBuilder builder = new ProfileImageBuilder(uid);
		builder.setName(name);
		if(!language.isEmpty()) {
			builder.setLanguage(language);
		}
		
		if(current != null) {
			builder.setActive(current.getQuest().getID());
		}
		
		builder.putReputation("default", points);
		for(final Entry<String, Integer> e : completed.entrySet()) {
			builder.putCompleted(e.getKey(), e.getValue());
		}
		
		for(final QuestProgress prog : progresses) {
			builder.putProgress(prog.getQuest().getID(), prog.getProgressImage());
		}
		
		return builder.build();
	}
}
