package com.gmail.molnardad.quester.objectives;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@SerializableAs("QuesterMobKillObjective")
public final class MobKillObjective extends Objective {

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
	public String progress(int progress) {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%r", String.valueOf(amount - progress)).replaceAll("%t", String.valueOf(amount));
		}
		String mob = entity == null ? "any mob" : entity.getName();
		return "Kill " + mob + " - " + (amount - progress) + "x";
	}
	
	@Override
	public String toString() {
		String entStr = entity == null ? "ANY" : entity.getName();
		return TYPE + ": " + entStr + "; AMT: " + amount + stringQevents();
	}
	
	public boolean check(EntityType ent) {
		if(entity == null) {
			return true;
		} else {
			return (entity.getTypeId() == ent.getTypeId());
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();
		
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
			
			MobKillObjective obj = new MobKillObjective(amt, ent);
			obj.loadSuper(map);
			return obj;
		} catch (Exception e) {
			return null;
		}
	}
}
