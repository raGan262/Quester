package com.gmail.molnardad.quester.conditions;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.profiles.PlayerProfile;
import com.gmail.molnardad.quester.storage.StorageKey;

/* DEPRECATED - use inverted QUEST condition instead */

@QElement("QUESTNOT")
public final class QuestNotCondition extends Condition {

	private final String quest;
	private final int time;
	private final boolean running;
	
	public QuestNotCondition(String quest, int time, boolean running) {
		this.quest = quest;
		if(running) {
			this.time = 0;
		}
		else {
			this.time = time;
		}
		this.running = running;
	}
	
	@Override
	protected String parseDescription(String description) {
		return description.replaceAll("%qst", quest);
	}

	@Override
	public boolean isMet(Player player, Quester plugin) {
		PlayerProfile profile = plugin.getProfileManager().getProfile(player.getName());
		if(running) {
			return !profile.hasQuest(quest);
		}
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
		String status = running ? "not be doing" : "not have done";
		return "Must " + status + " quest '" + quest + "'.";
	}
	
	@Override
	public String info() {
		String tm = (time > 0) ? "; TIME: " + time : "";
		String run = running ? "; (-r)" : "";
		return quest + tm + run;
	}
	
	@QCommand(
			min = 1,
			max = 2,
			usage = "<quest name> [time in seconds] (-r)")
	public static Condition fromCommand(QCommandContext context) throws QCommandException {
		String qst = context.getString(0);
		int t = 0;
		if(context.length() > 1) {
			t = context.getInt(1, 0);
		}
		return new QuestCondition(qst, t, context.hasFlag('r'), true);
	}

	@Override
	protected void save(StorageKey key) {
		key.setString("quest", quest);
		if(time != 0) {
			key.setInt("time", time);
		}
		if(running) {
			key.setBoolean("running", running);
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
		
		return new QuestCondition(qst, time, key.getBoolean("running", false), true);
	}
}
