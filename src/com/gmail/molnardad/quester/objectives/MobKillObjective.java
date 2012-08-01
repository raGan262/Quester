package com.gmail.molnardad.quester.objectives;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@SerializableAs("QuesterMobKillObjective")
public final class MobKillObjective implements Objective {

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
		return "Kill " + mob + " - " + (amount - progress) + "x";
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

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("amount", amount);
		if(entity == null)
			map.put("entity", -1);
		else
			map.put("entity", entity.getTypeId());
		
		return map;
	}

	public static MobKillObjective deserialize(Map<String, Object> map) {
		int amt;
		EntityType ent = null;
		try {
			amt = (Integer) map.get("amount");
			if(amt < 1)
				return null;
			ent = EntityType.fromId((Integer) map.get("entity"));
			
			return new MobKillObjective(amt, ent);
		} catch (Exception e) {
			return null;
		}
	}
}
