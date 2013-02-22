package com.gmail.molnardad.quester.conditions;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.PlayerProfile;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("QUESTNOT")
public final class QuestNotCondition extends Condition {

	private final String quest;
	private final int time;
	
	public QuestNotCondition(String quest, int time) {
		this.quest = quest;
		this.time = time;
	}
	
	@Override
	protected String parseDescription(String description) {
		return description.replaceAll("%qst", quest);
	}

	@Override
	public boolean isMet(Player player, Quester plugin) {
		PlayerProfile profile = plugin.getProfileManager().getProfile(player.getName());
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
		return "Must not have done quest '" + quest + "'";
	}
	
	@Override
	public String info() {
		return quest + "; TIME: " + time;
	}
	
	@QCommand(
			min = 1,
			max = 2,
			usage = "<quest name> [time in seconds]")
	public static Condition fromCommand(QCommandContext context) throws QCommandException {
		String qst = context.getString(0);
		int t = 0;
		if(context.length() > 1) {
			t = context.getInt(1, 0);
		}
		return new QuestNotCondition(qst, t);
	}
	
	protected void save(StorageKey key) {
		key.setString("quest", quest);
		if(time != 0) {
			key.setInt("time", time);
		}
	}

	protected static Condition load(StorageKey key) {
		String qst;
		int time;
		
		if(key.getString("quest") != null) {
			qst = key.getString("quest");
		}	
		else {
			return null;
		}
		
		time = key.getInt("time");
		
		return new QuestNotCondition(qst, time);
	}
}
