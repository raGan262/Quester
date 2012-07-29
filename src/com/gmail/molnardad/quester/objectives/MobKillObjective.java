package com.gmail.molnardad.quester.objectives;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public final class MobKillObjective implements Objective {

	private static final long serialVersionUID = 13506L;
	private final String TYPE = "MOBKILL";
	private final EntityType entity;
	private final int amount;

	public MobKillObjective(int amt, EntityType ent) {
		entity = ent;
		amount = amt;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}

	@Override
	public boolean isComplete(Player player, int progress) {
		return amount <= progress;
	}

	@Override
	public boolean finish(Player player) {
		return true;
	}

	@Override
	public String progress(int progress) {
		String mob = entity == null ? "any mob" : entity.getName();
		return "Kill " + mob + " - " + amount + "x";
	}
	
	@Override
	public String toString() {
		String entStr = entity == null ? "ANY" : entity.getName();
		return TYPE + ": " + entStr + "; AMT: " + amount ;
	}
	
	public boolean check(EntityType ent) {
		if(entity == null) {
			return true;
		} else {
			return entity.equals(ent);
		}
	}

}
