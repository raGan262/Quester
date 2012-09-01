package com.gmail.molnardad.quester.conditions;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

@SerializableAs("QuesterQuestCondition")
public final class QuestCondition extends Condition {
	
	private final String TYPE = "QUEST";
	private final String quest;
	
	public QuestCondition(String quest) {
		this.quest = quest;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isMet(Player player) {
		return Quester.qMan.getProfile(player.getName()).getCompleted().contains(quest.toLowerCase());
	}
	
	@Override
	public String show() {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%qst", quest);
		}
		return "Must have done quest '" + quest + "'";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + quest + coloredDesc().replaceAll("%qst", quest);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		
		map.put("quest", quest);
		
		return map;
	}

	public static QuestCondition deserialize(Map<String, Object> map) {
		String qst;
		try {
			qst = (String) map.get("quest");
		} catch (Exception e) {
			return null;
		}
		
		QuestCondition con = new QuestCondition(qst);
		con.loadSuper(map);
		return con;
	}
}
