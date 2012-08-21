package com.gmail.molnardad.quester;

import java.util.ArrayList;
import java.util.List;

import com.gmail.molnardad.quester.exceptions.ExceptionType;
import com.gmail.molnardad.quester.exceptions.QuesterException;

public class QuestHolder {

	private List<Integer> heldQuests = new ArrayList<Integer>();
	private int selected = -1;
	
	public List<Integer> getQuests() {
		return heldQuests;
	}
	
	public int getSelected() {
		if(heldQuests.size() > selected && selected >= 0)
			return heldQuests.get(selected);
		else
			return -1;
	}
	
	public int getSelectedIndex() {
		return selected;
	}
	
	public void selectNext() throws QuesterException {
		if(heldQuests.isEmpty())
			throw new QuesterException(ExceptionType.Q_NONE);
		if(getSelected() == -1) {
			selected = 0;
			if(Quester.qMan.isQuestActive(heldQuests.get(0)))
				return;
		}
		int i = selected;
		boolean notChosen = true;
		while(notChosen) {
			if(i < heldQuests.size()-1)
				i++;
			else
				i = 0;
			if(Quester.qMan.isQuestActive(heldQuests.get(i))) {
				selected = i;
				notChosen = false;
			} else if(i == selected) {
				throw new QuesterException(ExceptionType.Q_NONE_ACTIVE);
			}
		}
	}
	
	private void checkQuests() {
		QuestManager qm = Quester.qMan;
		for(int i=heldQuests.size()-1; i>=0; i--) {
			if(!qm.isQuest(heldQuests.get(i))) {
				heldQuests.remove(i);
			}
		}
	}
	
	public void addQuest(int questID) {
		if(!heldQuests.contains(questID)) {
			heldQuests.add(questID);
			checkQuests();
		}
	}
	
	public void removeQuest(int questID) {
		for(int i=0; i<heldQuests.size(); i++) {
			if(questID == heldQuests.get(i)) {
				heldQuests.remove(i);
				break;
			}
		}
		checkQuests();
	}
	
	public String serialize() {
		String result = "";
		boolean first = true;
		for(int i : heldQuests) {
			if(first) {
				result += String.valueOf(i);
				first = false;
			} else 
				result += "," + i;
		}
		
		
		return result;
	}
	
	public static QuestHolder deserialize(String str) {
		QuestHolder qHolder = new QuestHolder();
		
		try{
			String[] split = str.split(",");
			
			int i;
			for(String s : split) {
				try {
					i = Integer.parseInt(s);
					qHolder.addQuest(i);
				} catch (NumberFormatException f) {
				}
			}
			
		} catch (Exception e) {
		}
		
		qHolder.checkQuests();
		return qHolder;
	}
	
}
