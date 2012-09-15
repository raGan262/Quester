package com.gmail.molnardad.quester.qevents;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

public final class PointQevent extends Qevent {

	public static final String TYPE = "POINT";
	private final int amount;
	
	public PointQevent(int occ, int del, int amt) {
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
		return TYPE + ": AMT: " + amount;
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section);
		section.set("type", TYPE);
		section.set("amount", amount);
	}
	
	public static PointQevent deser(int occ, int del, ConfigurationSection section) {
		int amt;
		
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		else
			return null;
		
		return new PointQevent(occ, del, amt);
	}

	@Override
	public void run(Player player) {
		Quester.qMan.getProfile(player.getName()).addPoints(amount);
		Quester.qMan.checkRank(Quester.qMan.getProfile(player.getName()));
	}
}
