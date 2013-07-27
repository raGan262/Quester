package com.gmail.molnardad.quester.qevents;

import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.ExpManager;

@QElement("EXP")
public final class ExperienceQevent extends Qevent {

	private final int amount;
	private final boolean isLevel;
	
	public ExperienceQevent(int amt, boolean isLevel) {
		this.amount = amt;
		this.isLevel = isLevel;
	}
	
	@Override
	public String info() {
		String lvl = isLevel ? " (-l)" : "";
		return String.valueOf(amount) + lvl;
	}

	@Override
	protected void run(Player player, Quester plugin) {
 		ExpManager expMan = new ExpManager(player);
		if(isLevel) {
			int lvl = expMan.getLevelForExp(expMan.getCurrentExp());
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

	@QCommand(
			min = 1,
			max = 1,
			usage = "<amount> (-l)")
	public static Qevent fromCommand(QCommandContext context) {
		return new ExperienceQevent(context.getInt(0), context.hasFlag('l'));
	}
	
	@Override
	protected void save(StorageKey key) {
		key.setInt("amount", amount);
		if(isLevel) {
			key.setBoolean("islevel", isLevel);
		}
	}
	
	protected static Qevent load(StorageKey key) {
		int amt;
		amt = key.getInt("amount", 0);
		if(amt == 0) {
			return null;
		}
		
		return new ExperienceQevent(amt, key.getBoolean("islevel", false));
	}
}
