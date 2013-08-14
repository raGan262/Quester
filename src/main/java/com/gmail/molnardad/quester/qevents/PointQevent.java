package com.gmail.molnardad.quester.qevents;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.profiles.PlayerProfile;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("POINT")
public final class PointQevent extends Qevent {
	
	private final int amount;
	
	public PointQevent(final int amt) {
		amount = amt;
	}
	
	@Override
	public String info() {
		return String.valueOf(amount);
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		final PlayerProfile prof = plugin.getProfileManager().getProfile(player.getName());
		plugin.getProfileManager().addPoints(prof, amount);
	}
	
	@QCommand(min = 1, max = 1, usage = "<amount>")
	public static Qevent fromCommand(final QCommandContext context) throws QCommandException {
		final int amt = context.getInt(0);
		if(amt == 0) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_NONZERO"));
		}
		return new PointQevent(amt);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("amount", amount);
	}
	
	protected static Qevent load(final StorageKey key) {
		final int amt = key.getInt("amount", 0);
		if(amt == 0) {
			return null;
		}
		
		return new PointQevent(amt);
	}
}
