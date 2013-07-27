package com.gmail.molnardad.quester.qevents;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("POINT")
public final class PointQevent extends Qevent {

	private final int amount;
	
	public PointQevent(int amt) {
		this.amount = amt;
	}
	
	@Override
	public String info() {
		return String.valueOf(amount);
	}

	@Override
	protected void run(Player player, Quester plugin) {
		plugin.getProfileManager().addPoints(player.getName(), amount);
	}

	@QCommand(
			min = 1,
			max = 1,
			usage = "<amount>")
	public static Qevent fromCommand(QCommandContext context) throws QCommandException {
		int amt = context.getInt(0);
		if(amt == 0) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_NONZERO);
		}
		return new PointQevent(amt);
	}
	
	@Override
	protected void save(StorageKey key) {
		key.setInt("amount", amount);
	}
	
	protected static Qevent load(StorageKey key) {
		int amt = key.getInt("amount", 0);
		if(amt == 0) {
			return null;
		}
		
		return new PointQevent(amt);
	}
}
