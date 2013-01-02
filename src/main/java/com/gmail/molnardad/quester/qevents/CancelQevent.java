package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.QuesterException;

public final class CancelQevent extends Qevent {

	public static final String TYPE = "CANCEL";
	
	public CancelQevent(int occ, int del){
		super(occ, del);
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
		return TYPE + appendSuper();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
	}
	
	public static Qevent deser(int occ, int del, ConfigurationSection section) {	
		return new CancelQevent(occ, del);
	}

	@Override
	void run(Player player) {
		try {
			Quester.qMan.cancelQuest(player, false);
		} catch (QuesterException e) {
			Quester.log.info("Event failed to cancel " + player.getName() + "'s quest. Reason: " + ChatColor.stripColor(e.message()));
		}
	}
}
