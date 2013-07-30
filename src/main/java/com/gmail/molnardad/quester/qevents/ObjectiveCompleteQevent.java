package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.exceptions.ObjectiveException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.profiles.PlayerProfile;
import com.gmail.molnardad.quester.profiles.ProfileManager;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("OBJCOM")
public final class ObjectiveCompleteQevent extends Qevent {
	
	private final int objective;
	private final boolean runEvents;
	
	public ObjectiveCompleteQevent(final int obj, final boolean runEvents) {
		objective = obj;
		this.runEvents = runEvents;
	}
	
	@Override
	public String info() {
		final String evts = runEvents ? " (-e)" : "";
		return String.valueOf(objective) + evts;
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		try {
			final ProfileManager profMan = plugin.getProfileManager();
			final PlayerProfile prof = profMan.getProfile(player.getName());
			final int[] prog = prof.getProgress().getProgress();
			if(objective >= 0 && objective < prog.length) {
				final int req = prof.getQuest().getObjective(objective).getTargetAmount();
				if(prog[objective] < req) {
					final ActionSource as = ActionSource.eventSource(this);
					if(runEvents) {
						profMan.incProgress(player, as, objective, req - prog[objective]);
					}
					else {
						profMan.setProgress(player.getName(), objective, req);
						profMan.complete(player, as,
								plugin.getLanguageManager().getPlayerLang(player.getName()), false);
					}
				}
				else {
					throw new ObjectiveException("Objective already complete");
				}
			}
			else {
				throw new ObjectiveException("Objective index out of bounds."); // objective does
																				// not exist
			}
		}
		catch (final QuesterException e) {
			Quester.log.info("Event failed to complete objective. Reason: "
					+ ChatColor.stripColor(e.getMessage()));
		}
	}
	
	@QCommand(min = 1, max = 1, usage = "<objective ID> (-e)")
	public static Qevent fromCommand(final QCommandContext context) throws QCommandException {
		final int obj = context.getInt(0);
		if(obj < 0) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_BAD_ID);
		}
		return new ObjectiveCompleteQevent(obj, context.hasFlag('e'));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("objective", objective);
		if(runEvents) {
			key.setBoolean("runevents", runEvents);
		}
	}
	
	protected static Qevent load(final StorageKey key) {
		final int obj = key.getInt("objective", -1);
		if(obj < 0) {
			return null;
		}
		
		return new ObjectiveCompleteQevent(obj, key.getBoolean("runevents", false));
	}
}
