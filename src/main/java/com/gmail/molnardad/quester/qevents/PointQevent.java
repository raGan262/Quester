package com.gmail.molnardad.quester.qevents;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.PlayerProfile;
import com.gmail.molnardad.quester.QuestManager;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;

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
	protected void run(Player player) {
		QuestManager qMan = QuestManager.getInstance();
		PlayerProfile prof = qMan.getProfile(player.getName());
		prof.addPoints(amount);
		qMan.checkRank(prof);
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("amount", amount);
	}
	
	public static PointQevent deser(ConfigurationSection section) {
		int amt;
		
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		else
			return null;
		
		return new PointQevent(amt);
	}
}
