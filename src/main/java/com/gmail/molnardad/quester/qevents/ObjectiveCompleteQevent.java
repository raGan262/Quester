package com.gmail.molnardad.quester.qevents;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.exceptions.ExceptionType;
import com.gmail.molnardad.quester.exceptions.QuesterException;

public final class ObjectiveCompleteQevent extends Qevent {

	public static final String TYPE = "OBJCOM";
	private final int objective;
	
	public ObjectiveCompleteQevent(int occ, int del, int obj) {
		super(occ, del);
		this.objective = obj;
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
		return TYPE + ": " + objective + appendSuper();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("objective", objective);
	}
	
	public static ObjectiveCompleteQevent deser(int occ, int del, ConfigurationSection section) {
		int obj;
		
		if(section.isInt("objective"))
			obj = section.getInt("objective");
		else
			return null;
		
		return new ObjectiveCompleteQevent(occ, del, obj);
	}

	@Override
	void run(Player player) {
		try {
			List<Integer> prog = Quester.qMan.getProfile(player.getName()).getProgress();
			if(objective >= 0 && objective < prog.size()) {
				int req = Quester.qMan.getPlayerQuest(player.getName()).getObjective(objective).getTargetAmount();
				prog.set(objective, req);
			} else {
				throw new QuesterException(ExceptionType.OBJ_NOT_EXIST);
			}
		} catch (QuesterException e) {
			Quester.log.info("Event failed to complete objective. Reason: " + ChatColor.stripColor(e.message()));
		}
	}
}
