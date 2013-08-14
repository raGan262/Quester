package com.gmail.molnardad.quester.qevents;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.ActionSource;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("PROGRESS")
public class ProgressQevent extends Qevent {
	
	private final int objective;
	private final int amount;
	
	public ProgressQevent(final int objectiveID, final int amount) {
		objective = objectiveID;
		this.amount = amount;
	}
	
	@Override
	protected String info() {
		return objective + "; AMT: " + amount;
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		plugin.getProfileManager().incProgress(player, ActionSource.eventSource(this), objective,
				amount, false);
	}
	
	@QCommand(min = 1, max = 2, usage = "<obj id> [amount]")
	public static Qevent fromCommand(final QCommandContext context) throws QCommandException {
		final int obj = context.getInt(0);
		int amt = 1;
		if(context.length() > 1) {
			amt = context.getInt(1);
		}
		if(obj < 0) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_BAD_ID"));
		}
		if(amt < 1) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_POSITIVE"));
		}
		return new ProgressQevent(obj, amt);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("objective", objective);
		if(amount != 1) {
			key.setInt("amount", amount);
		}
	}
	
	protected static Qevent load(final StorageKey key) {
		final int amt = key.getInt("amount", 1);
		final int obj = key.getInt("objective", -1);
		if(obj < 0) {
			return null;
		}
		return new ProgressQevent(obj, amt);
	}
	
}
