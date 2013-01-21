package com.gmail.molnardad.quester.conditions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.PlayerProfile;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.QElement;

@QElement("QUEST")
public final class QuestCondition extends Condition {
	
	private final String quest;
	private final int time;
	
	public QuestCondition(String quest, int time) {
		this.quest = quest;
		this.time = time;
	}
	
	@Override
	protected String parseDescription(String description) {
		return description.replaceAll("%qst", quest);
	}

	@Override
	public boolean isMet(Player player) {
		PlayerProfile profile = QuestManager.getInstance().getProfile(player.getName());
		if (!profile.isCompleted(quest)) {
			return false;
		}
		else {
			if(time == 0) {
				return true;
			}
			else {
				return ((System.currentTimeMillis() / 1000) - profile.getCompletionTime(quest)) < time;
			}
		}
	}
	
	@Override
	public String show() {
		return "Must have done quest '" + quest + "'";
	}
	
	@Override
	public String info() {
		return quest + "; TIME: " + time;
	}
	
	// TODO serialization

	public void serialize(ConfigurationSection section) {
		section.set("quest", quest);
		if(time != 0) {
			section.set("time", time);
		}
	}

	public static QuestCondition deser(ConfigurationSection section) {
		String qst;
		int time;
		
		if(section.isString("quest"))
			qst = section.getString("quest");
		else
			return null;
		
		time = section.getInt("time", 0);
		
		return new QuestCondition(qst, time);
	}
}
