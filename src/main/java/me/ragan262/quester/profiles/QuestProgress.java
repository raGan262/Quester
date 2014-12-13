package me.ragan262.quester.profiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.google.common.primitives.Ints;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.quests.Quest;

public class QuestProgress {
	
	final Quest quest;
	final List<Integer> progress;
	final ObjectiveStatus[] objectiveStatuses;
	
	public enum ObjectiveStatus {
		INACTIVE, ACTIVE, COMPLETED, DISABLED
	}
	
	QuestProgress(final Quest quest) {
		this(quest, null);
	}
	
	QuestProgress(final Quest quest, final ProgressImage image) {
		if(quest == null) {
			throw new NullPointerException("Cannot create QuestProgress for null Quest.");
		}
		this.quest = quest;
		final int questSize = quest.getObjectives().size();
		if(image != null) {
			progress = new ArrayList<>(image.asList());
			if(questSize != progress.size()) {
				throw new IllegalArgumentException("Number of objectives in quest and in progress not matching.");
			}
		}
		else {
			progress = new ArrayList<>(Collections.nCopies(questSize, 0));
		}
		
		objectiveStatuses = new ObjectiveStatus[questSize];
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
			if(objectives.get(i).isComplete(progress.get(i))) {
				objectiveStatuses[i] = ObjectiveStatus.COMPLETED;
			}
			else {
				objectiveStatuses[i] = ObjectiveStatus.INACTIVE;
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
		return progress.size();
	}
	
	public Quest getQuest() {
		return quest;
	}
	
	boolean setProgress(final int objectiveID, final int newValue) {
		if(objectiveID >= 0 && objectiveID < progress.size()) {
			progress.set(objectiveID, newValue);
			// if objective status went from not complete to complete or vice versa
			if(objectiveStatuses[objectiveID] == ObjectiveStatus.COMPLETED != quest.getObjective(objectiveID).isComplete(newValue)) {
				updateObjectives();
			}
			
			return true;
		}
		
		return false;
	}
	
	public List<Integer> getProgress() {
		return Collections.unmodifiableList(progress);
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
			final QuestProgress prg = (QuestProgress)obj;
			return quest.equals(prg.quest);
		}
		
		return false;
	}
	
	ProgressImage getProgressImage() {
		return new ProgressImage(progress);
	}
	
	public static final class ProgressImage {
		
		private final List<Integer> list;
		
		public ProgressImage(final int[] progress) {
			list = Collections.unmodifiableList(Ints.asList(progress));
		}
		
		public ProgressImage(final List<Integer> list) {
			if(list.contains(null)) {
				throw new IllegalArgumentException("Progress list can't contain null elements.");
			}
			this.list = Collections.unmodifiableList(new ArrayList<>(list));
		}
		
		public List<Integer> asList() {
			return list;
		}
	}
}
