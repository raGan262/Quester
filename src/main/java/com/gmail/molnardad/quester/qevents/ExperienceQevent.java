package com.gmail.molnardad.quester.qevents;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.ExpManager;

@QElement("EXP")
public final class ExperienceQevent extends Qevent {
	
	private final int amount;
	private final boolean isLevel;
	
	public ExperienceQevent(final int amt, final boolean isLevel) {
		amount = amt;
		this.isLevel = isLevel;
	}
	
	@Override
	public String info() {
		final String lvl = isLevel ? " (-l)" : "";
		return String.valueOf(amount) + lvl;
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		final ExpManager expMan = new ExpManager(player);
		if(isLevel) {
			final int lvl = expMan.getLevelForExp(expMan.getCurrentExp());
			if(lvl <= -amount) {
				expMan.setExp(0);
			}
			else {
				expMan.setExp(expMan.getXpForLevel(lvl + amount));
			}
		}
		else {
			expMan.changeExp(amount);
		}
	}
	
	@QCommand(min = 1, max = 1, usage = "<amount> (-l)")
	public static Qevent fromCommand(final QCommandContext context) throws QCommandException {
		final int amt = context.getInt(0);
		if(amt == 0) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_NONZERO"));
		}
		return new ExperienceQevent(amt, context.hasFlag('l'));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("amount", amount);
		if(isLevel) {
			key.setBoolean("islevel", isLevel);
		}
	}
	
	protected static Qevent load(final StorageKey key) {
		final int amt = key.getInt("amount", 0);
		if(amt == 0) {
			return null;
		}
		
		return new ExperienceQevent(amt, key.getBoolean("islevel", false));
	}
}
