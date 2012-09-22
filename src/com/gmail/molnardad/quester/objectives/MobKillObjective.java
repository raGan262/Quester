package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.Util;

public final class MobKillObjective extends Objective {

	public static final String TYPE = "MOBKILL";
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
		return TYPE + ": " + entStr + "; AMT: " + amount + coloredDesc() + stringQevents();
	}
	
	public boolean check(EntityType ent) {
		if(entity == null) {
			return true;
		} else {
			return (entity.getTypeId() == ent.getTypeId());
		}
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		if(amount > 1)
			section.set("amount", amount);
		if(entity != null)
			section.set("entity","" + entity.getTypeId());
	}
	
	public static Objective deser(ConfigurationSection section) {
		int amt = 1;
		EntityType ent = null;
		try {
			ent = Util.parseEntity(section.getString("entity"));
		} catch (Exception ignore) {}
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		if(amt < 1)
			amt = 1;
		return new MobKillObjective(amt, ent);
	}
}
