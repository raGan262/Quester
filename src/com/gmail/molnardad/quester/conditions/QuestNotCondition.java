package com.gmail.molnardad.quester.conditions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

@SerializableAs("QuesterQuestNotCondition")
public final class QuestNotCondition implements Condition {

	private final String TYPE = "QUESTNOT";
	private final String quest;
	
	public QuestNotCondition(String quest) {
		this.quest = quest;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isMet(Player player) {
		return !Quester.qMan.getProfile(player.getName()).getCompleted().contains(quest.toLowerCase());
	}
	
	@Override
	public String show() {
		return "Must not have done quest '" + quest + "'";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + quest;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("quest", quest);
		
		return map;
	}

	public static QuestNotCondition deserialize(Map<String, Object> map) {
		String qst;
		try {
			qst = (String) map.get("quest");
		} catch (Exception e) {
			return null;
		}
		
		return new QuestNotCondition(qst);
	}
}
