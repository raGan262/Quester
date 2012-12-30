package com.gmail.molnardad.quester.qevents;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.ExpManager;

public final class ExperienceQevent extends Qevent {

	public static final String TYPE = "EXP";
	private final int amount;
	
	public ExperienceQevent(int occ, int del, int amt) {
		super(occ, del);
		this.amount = amt;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public int getOccasion() {
		return occasion;
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + amount + appendSuper();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("amount", amount);
	}
	
	public static ExperienceQevent deser(int occ, int del, ConfigurationSection section) {
		int amt;
		
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		else
			return null;
		
		return new ExperienceQevent(occ, del, amt);
	}

	@Override
	public void run(Player player) {
		ExpManager expMan = new ExpManager(player);
		expMan.changeExp(amount);
	}
}
