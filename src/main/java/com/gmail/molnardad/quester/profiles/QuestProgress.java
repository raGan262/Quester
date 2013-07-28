package com.gmail.molnardad.quester.profiles;

import java.util.Arrays;
import java.util.List;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.quests.Quest;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

public class QuestProgress {
	
	final Quest quest;
	int[] progress;
	ObjectiveStatus[] objectiveStatuses;
	
	public enum ObjectiveStatus {
		INACTIVE, ACTIVE, COMPLETED, DISABLED;
	}
	
	QuestProgress(final Quest quest) {
		if(quest == null) {
			throw new NullPointerException("Cannot create QuestProgress for null Quest.");
		}
		this.quest = quest;
		progress = new int[quest.getObjectives().size()];
		objectiveStatuses = new ObjectiveStatus[quest.getObjectives().size()];
		for(int i = 0; i < objectiveStatuses.length; i++) {
			objectiveStatuses[i] = ObjectiveStatus.INACTIVE;
		}
		updateObjectives();
	}
	
	public ObjectiveStatus[] getObjectiveStatuses() {
		return Arrays.copyOf(objectiveStatuses, objectiveStatuses.length);
	}
	
	public ObjectiveStatus getObjectiveStatus(final int objectiveID) {
		if(objectiveID < 0 || objectiveID >= objectiveStatuses.length) {
			return null;
		}
		return objectiveStatuses[objectiveID];
	}
	
	private void updateObjectives() {
		final List<Objective> objectives = quest.getObjectives();
		for(int i = 0; i < objectives.size(); i++) {
			if(objectives.get(i).isComplete(progress[i])) {
				objectiveStatuses[i] = ObjectiveStatus.COMPLETED;
				continue;
			}
		}
		objectives:
		for(int i = 0; i < objectives.size(); i++) {
			if(objectiveStatuses[i] != ObjectiveStatus.COMPLETED) {
				for(final int p : objectives.get(i).getPrerequisites()) {
					if(objectiveStatuses[p] != ObjectiveStatus.COMPLETED) {
						objectiveStatuses[i] = ObjectiveStatus.INACTIVE;
						continue objectives;
					}
				}
				objectiveStatuses[i] = ObjectiveStatus.ACTIVE;
			}
		}
	}
	
	public int getSize() {
		return progress.length;
	}
	
	public Quest getQuest() {
		return quest;
	}
	
	void setProgressWithoutUpdate(final int objectiveID, final int newValue) {
		if(objectiveID >= 0 && objectiveID < progress.length) {
			progress[objectiveID] = newValue;
		}
	}
	
	public boolean setProgress(final int objectiveID, final int newValue) {
		if(objectiveID >= 0 && objectiveID < progress.length) {
			progress[objectiveID] = newValue;
			// if objective status went from not complete to complete or vice versa
			if(objectiveStatuses[objectiveID] == ObjectiveStatus.COMPLETED != quest.getObjective(
					objectiveID).isComplete(newValue)) {
				updateObjectives();
			}
			return true;
		}
		return false;
	}
	
	public int[] getProgress() {
		return progress;
	}
	
	public int getCurrentObjectiveID() {
		for(int i = 0; i < objectiveStatuses.length; i++) {
			if(objectiveStatuses[i] == ObjectiveStatus.ACTIVE) {
				return i;
			}
		}
		return -1;
	}
	
	public static QuestProgress getEmptyProgress(final Quest quest) {
		return new QuestProgress(quest);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(obj != null && obj instanceof QuestProgress) {
			final QuestProgress prg = (QuestProgress) obj;
			return quest.equals(prg.quest);
		}
		return false;
	}
	
	void serialize(final StorageKey key) {
		key.setString("progress", Util.implodeInt(progress, "|"));
	}
	
	static QuestProgress deserialize(final StorageKey key, final Quest quest) {
		String progressString = null;
		if(!key.getSubKeys().isEmpty()) {
			progressString = key.getString("progress");
		}
		else if(key.getString("") != null) { // older format
			progressString = key.getString("");
		}
		
		QuestProgress prog = null;
		try {
			prog = new QuestProgress(quest);
			String[] strs = progressString.split("\\|");
			if(strs[0].isEmpty()) {
				strs = new String[0];
			}
			if(strs.length == prog.getSize()) {
				for(int i = 0; i < strs.length; i++) {
					prog.setProgressWithoutUpdate(i, Integer.parseInt(strs[i]));
				}
				prog.updateObjectives();
			}
			else {
				throw new Exception("Number of objectives in quest and in progress not matching.");
			}
		}
		catch (final Exception e) {
			if(QConfiguration.verbose) {
				Quester.log
						.info("Invalid or missing progress for quest '" + key.getName() + "' in profile.");
			}
			if(QConfiguration.debug) {
				e.printStackTrace();
			}
			return null;
		}
		
		return prog;
	}
}
