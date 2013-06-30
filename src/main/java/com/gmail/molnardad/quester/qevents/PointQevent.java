package com.gmail.molnardad.quester.qevents;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
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
	public static Qevent fromCommand(QCommandContext context) {
		return new PointQevent(context.getInt(0));
	}
	
	@Override
	protected void save(StorageKey key) {
		key.setInt("amount", amount);
	}
	
	protected static Qevent load(StorageKey key) {
		int amt;
		
		amt = key.getInt("amount", 0);
		if(amt == 0) {
			return null;
		}
		
		return new PointQevent(amt);
	}
}
