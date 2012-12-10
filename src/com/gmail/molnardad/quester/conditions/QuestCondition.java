package com.gmail.molnardad.quester.conditions;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

public final class QuestCondition extends Condition {
	
	public static final String TYPE = "QUEST";
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
		return Quester.qMan.getProfile(player.getName()).isCompleted(quest);
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
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("quest", quest);
	}

	public static QuestCondition deser(ConfigurationSection section) {
		String qst;
		
		if(section.isString("quest"))
			qst = section.getString("quest");
		else
			return null;
		
		return new QuestCondition(qst);
	}
}
