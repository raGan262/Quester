package com.gmail.molnardad.quester.qevents;

import org.bukkit.configuration.ConfigurationSection;
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
	
	public ExperienceQevent(int amt) {
		this.amount = amt;
	}
	
	@Override
	public String info() {
		return String.valueOf(amount);
	}

	@Override
	protected void run(Player player, Quester plugin) {
		ExpManager expMan = new ExpManager(player);
		expMan.changeExp(amount);
	}

	@QCommand(
			min = 1,
			max = 1,
			usage = "<amount>")
	public static Qevent fromCommand(QCommandContext context) {
		return new ExperienceQevent(context.getInt(0));
	}
	
	@Override
	protected void save(StorageKey key) {
		key.setInt("amount", amount);
	}
	
	protected static Qevent load(ConfigurationSection section) {
		int amt;
		
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		else
			return null;
		
		return new ExperienceQevent(amt);
	}
}
