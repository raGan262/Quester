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

@QElement("QUEST")
public final class QuestCondition extends Condition {
	
	private final String quest;
	private final int time;
	private final boolean running;
	private final boolean inverted;
	
	QuestCondition(final String quest, final int time, final boolean running, final boolean invert) {
		this.quest = quest;
		if(running) {
			this.time = 0;
		}
		else {
			this.time = time;
		}
		this.running = running;
		inverted = invert;
	}
	
	@Override
	protected String parseDescription(final String description) {
		return description.replaceAll("%qst", quest);
	}
	
	@Override
	public boolean isMet(final Player player, final Quester plugin) {
		final PlayerProfile profile = plugin.getProfileManager().getProfile(player.getName());
		if(running) {
			return profile.hasQuest(quest) != inverted;
		}
		if(time == 0) {
			return profile.isCompleted(quest) != inverted;
		}
		else {
			// QUEST: elapsed < time
			// QUESTNOT: elapsed >= time
			return System.currentTimeMillis() / 1000 - profile.getCompletionTime(quest) < time != inverted;
		}
	}
	
	@Override
	public String show() {
		final String type = inverted ? "not " : "";
		final String status = running ? "be doing" : "have done";
		final StringBuilder period = new StringBuilder();
		if(!running && time != 0) {
			if(inverted) {
				period.append(" for ").append(time).append(" seconds.");
			}
			else {
				period.append(" at most ").append(time).append(" seconds ago.");
			}
		}
		return "Must " + type + status + " quest '" + quest + "'" + period.toString() + ".";
	}
	
	@Override
	public String info() {
		final String tm = time > 0 ? "; TIME: " + time : "";
		final StringBuilder run = new StringBuilder();
		if(inverted || running) {
			run.append(" (-");
			if(running) {
				run.append('r');
			}
			if(inverted) {
				run.append('i');
			}
			run.append(')');
		}
		return quest + tm + run;
	}
	
	@QCommand(min = 1, max = 2, usage = "<quest name> [time in seconds] (-ri)")
	public static Condition fromCommand(final QCommandContext context) throws QCommandException {
		final String qst = context.getString(0);
		int t = 0;
		if(context.length() > 1) {
			t = context.getInt(1, 0);
		}
		return new QuestCondition(qst, t, context.hasFlag('r'), context.hasFlag('i'));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("quest", quest);
		if(time != 0) {
			key.setInt("time", time);
		}
		if(running) {
			key.setBoolean("running", running);
		}
		if(inverted) {
			key.setBoolean("inverted", inverted);
		}
	}
	
	protected static Condition load(final StorageKey key) {
		String qst;
		int time;
		
		if(key.getString("quest") != null) {
			qst = key.getString("quest");
		}
		else {
			return null;
		}
		
		time = key.getInt("time");
		
		return new QuestCondition(qst, time, key.getBoolean("running", false), key.getBoolean(
				"inverted", false));
	}
}
