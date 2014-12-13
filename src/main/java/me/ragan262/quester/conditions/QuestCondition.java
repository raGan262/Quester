package me.ragan262.quester.conditions;

import java.util.concurrent.TimeUnit;
import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Condition;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.storage.StorageKey;
import org.bukkit.entity.Player;

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
	protected String parseDescription(final Player player, final String description) {
		long hours = 0;
		long minutes = 0;
		long seconds = 0;
		if(player != null && time > 0) {
			final PlayerProfile profile = Quester.getInstance().getProfileManager().getProfile(player);
			final long elapsed = System.currentTimeMillis() / 1000
					- profile.getCompletionTime(quest);
			
			final long toCount = time - elapsed;
			
			if(toCount >= 0) {
				hours = TimeUnit.SECONDS.toHours(toCount);
				minutes = TimeUnit.SECONDS.toMinutes(toCount) - TimeUnit.HOURS.toMinutes(hours);
				seconds = toCount - TimeUnit.MINUTES.toSeconds(minutes)
						- TimeUnit.HOURS.toSeconds(hours);
				
			}
		}
		return description.replaceAll("%qst", quest).replaceAll("%h", String.valueOf(hours)).replaceAll("%m", String.valueOf(minutes)).replaceAll("%s", String.valueOf(seconds));
	}
	
	@Override
	public boolean isMet(final Player player) {
		final PlayerProfile profile = Quester.getInstance().getProfileManager().getProfile(player);
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
	
	@Command(min = 1, max = 2, usage = "<quest name> [time in seconds] (-ri)")
	public static Condition fromCommand(final QuesterCommandContext context) {
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
			key.setBoolean("running", true);
		}
		if(inverted) {
			key.setBoolean("inverted", true);
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
		
		return new QuestCondition(qst, time, key.getBoolean("running", false), key.getBoolean("inverted", false));
	}
}
