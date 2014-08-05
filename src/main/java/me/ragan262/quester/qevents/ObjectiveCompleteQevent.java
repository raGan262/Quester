package me.ragan262.quester.qevents;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.ActionSource;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.exceptions.ObjectiveException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.profiles.PlayerProfile;
import me.ragan262.quester.profiles.ProfileManager;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
			final PlayerProfile prof = profMan.getProfile(player);
			final int[] prog = prof.getProgress().getProgress();
			if(objective >= 0 && objective < prog.length) {
				final int req = prof.getQuest().getObjective(objective).getTargetAmount();
				if(prog[objective] < req) {
					final ActionSource as = ActionSource.eventSource(this);
					if(runEvents) {
						profMan.incProgress(player, as, objective, req - prog[objective]);
					}
					else {
						profMan.setProgress(prof, objective, req);
						profMan.complete(player, as, plugin.getLanguageManager().getLang(prof.getLanguage()), false);
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
		catch(final QuesterException e) {
			Ql.warning("Event failed to complete objective. Reason: "
					+ ChatColor.stripColor(e.getMessage()));
		}
	}
	
	@Command(min = 1, max = 1, usage = "<objective ID> (-e)")
	public static Qevent fromCommand(final QuesterCommandContext context) throws CommandException {
		final int obj = context.getInt(0);
		if(obj < 0) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_BAD_ID"));
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
