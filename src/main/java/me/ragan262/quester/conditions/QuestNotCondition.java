package me.ragan262.quester.conditions;

import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.storage.StorageKey;

import org.bukkit.entity.Player;

/* DEPRECATED - use inverted QUEST condition instead */

@QElement("QUESTNOT")
public final class QuestNotCondition extends Condition {
	
	private final String quest;
	private final int time;
	private final boolean running;
	
	public QuestNotCondition(final String quest, final int time, final boolean running) {
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
	protected String parseDescription(final Player player, final String description) {
		return description.replaceAll("%qst", quest);
	}
	
	@Override
	public boolean isMet(final Player player, final Quester plugin) {
		final PlayerProfile profile = plugin.getProfileManager().getProfile(player);
		if(running) {
			return !profile.hasQuest(quest);
		}
		if(!profile.isCompleted(quest)) {
			return true;
		}
		else {
			if(time == 0) {
				return false;
			}
			else {
				return System.currentTimeMillis() / 1000 - profile.getCompletionTime(quest) > time;
			}
		}
	}
	
	@Override
	public String show() {
		final String status = running ? "not be doing" : "not have done";
		return "Must " + status + " quest '" + quest + "'.";
	}
	
	@Override
	public String info() {
		final String tm = time > 0 ? "; TIME: " + time : "";
		final String run = running ? "; (-r)" : "";
		return quest + tm + run;
	}
	
	@QCommand(min = 1, max = 2, usage = "<quest name> [time in seconds] (-r)")
	public static Condition fromCommand(final QCommandContext context) throws QCommandException {
		final String qst = context.getString(0);
		int t = 0;
		if(context.length() > 1) {
			t = context.getInt(1, 0);
		}
		return new QuestCondition(qst, t, context.hasFlag('r'), true);
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
		
		return new QuestCondition(qst, time, key.getBoolean("running", false), true);
	}
}
