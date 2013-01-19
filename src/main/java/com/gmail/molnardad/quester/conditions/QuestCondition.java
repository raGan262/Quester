package com.gmail.molnardad.quester.conditions;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.PlayerProfile;

public final class QuestCondition extends Condition {
	
	public static final String TYPE = "QUEST";
	private final String quest;
	private final int time;
	
	public QuestCondition(String quest, int time) {
		this.quest = quest;
		this.time = time;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isMet(Player player) {
		PlayerProfile profile = plugin.getQuestManager().getProfile(player.getName());
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
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%qst", quest);
		}
		return "Must have done quest '" + quest + "'";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + quest + "; TIME: " + time + coloredDesc().replaceAll("%qst", quest);
	}
	
	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
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
