package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
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
	
	public ObjectiveCompleteQevent(int obj, boolean runEvents) {
		this.objective = obj;
		this.runEvents = runEvents;
	}
	
	@Override
	public String info() {
		String evts = runEvents ? " (-e)" : "";
		return String.valueOf(objective) + evts;
	}

	@Override
	protected void run(Player player, Quester plugin) {
		try {
			ProfileManager profMan = plugin.getProfileManager();
			PlayerProfile prof = profMan.getProfile(player.getName());
			int[] prog = prof.getProgress().getProgress();
			if(objective >= 0 && objective < prog.length) {
				int req = prof.getQuest().getObjective(objective).getTargetAmount();
				if(prog[objective] < req) {
					ActionSource as = ActionSource.eventSource(this);
					if(runEvents) {
						profMan.incProgress(player, as, objective, req - prog[objective]);
					}
					else {
						profMan.setProgress(player.getName(), objective, req);
						profMan.complete(player, as, plugin.getLanguageManager().getPlayerLang(player.getName()), false);
					}
				}
				else {
					throw new ObjectiveException("Objective already complete");
				}
			} else {
				throw new ObjectiveException("Objective index out of bounds."); // objective does not exist
			}
		} catch (QuesterException e) {
			Quester.log.info("Event failed to complete objective. Reason: " + ChatColor.stripColor(e.getMessage()));
		}
	}

	@QCommand(
			min = 1,
			max = 1,
			usage = "<objective ID>")
	public static Qevent fromCommand(QCommandContext context) {
		return new ObjectiveCompleteQevent(context.getInt(0), context.hasFlag('e'));
	}

	@Override
	protected void save(StorageKey key) {
		key.setInt("objective", objective);
		if(runEvents) {
			key.setBoolean("runevents", runEvents);
		}
	}
	
	protected static Qevent load(StorageKey key) {
		int obj;
		boolean run = key.getBoolean("runevents", false);
		obj = key.getInt("objective", -1);
		if(obj < 0) {
			return null;
		}
		
		return new ObjectiveCompleteQevent(obj, run);
	}
}
