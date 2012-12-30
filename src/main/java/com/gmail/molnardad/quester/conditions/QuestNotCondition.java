package com.gmail.molnardad.quester.conditions;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.PlayerProfile;
import com.gmail.molnardad.quester.Quester;

public final class QuestNotCondition extends Condition {

	public static final String TYPE = "QUESTNOT";
	private final String quest;
	private final int time;
	
	public QuestNotCondition(String quest, int time) {
		this.quest = quest;
		this.time = time;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isMet(Player player) {
		PlayerProfile profile = Quester.qMan.getProfile(player.getName());
		if (!profile.isCompleted(quest)) {
			return true;
		}
		else {
			if(time == 0) {
				return false;
			}
			else {
				return ((System.currentTimeMillis() / 1000) - profile.getCompletionTime(quest)) > time;
			}
		}
	}
	
	@Override
	public String show() {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%qst", quest);
		}
		return "Must not have done quest '" + quest + "'";
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

	public static QuestNotCondition deser(ConfigurationSection section) {
		String qst;
		int time;
		
		if(section.isString("quest"))
			qst = section.getString("quest");
		else
			return null;
		
		time = section.getInt("time", 0);
		
		return new QuestNotCondition(qst, time);
	}
}
