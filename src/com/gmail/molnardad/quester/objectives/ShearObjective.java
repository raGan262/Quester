package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.Util;

public final class ShearObjective extends Objective {

	private final String TYPE = "SHEAR";
	private final DyeColor color;
	private final int amount;

	public ShearObjective(int amt, DyeColor col) {
		amount = amt;
		color = col;
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
		String strCol = (color == null) ? "any" : color.name().replace('_', ' ').toLowerCase() ;
		return "Shear " + strCol + " sheep - " + (amount - progress) + "x";
	}
	
	@Override
	public String toString() {
		String strCol = (color == null) ? "ANY" : color.name() ;
		return TYPE + ": " + strCol + "; AMT: " + amount + coloredDesc() + stringQevents();
	}
	
	public boolean check(DyeColor col) {
		if(col == color || color == null) {
			return true;	
		}
		return false;
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		if(color != null)
			section.set("color", Util.serializeColor(color));
		if(amount > 1)
			section.set("amount", amount);
	}
	
	public static Objective deser(ConfigurationSection section) {
		int amt = 1;
		DyeColor col = null;
		try {
			col = Util.parseColor(section.getString("color"));
		} catch (Exception ignore) {}
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		if(amt < 1)
			amt = 1;
		return new ShearObjective(amt, col);
	}
}
