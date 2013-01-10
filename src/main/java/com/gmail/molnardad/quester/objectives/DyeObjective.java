package com.gmail.molnardad.quester.objectives;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.Util;

public final class DyeObjective extends Objective {

	public static final String TYPE = "DYE";
	private final int amount;
	private final DyeColor color;
	private final String colorName;
	
	public DyeObjective(int amt, DyeColor col) {
		amount = amt;
		color = col;
		if(col != null) {
			colorName = " " + col.name().toLowerCase().replaceAll("_", " ");
		} else {
			colorName = "";
		}
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
		return progress >= amount;
	}
	
	@Override
	public String progress(int progress) {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%r", String.valueOf(amount - progress)).replaceAll("%t", String.valueOf(amount));
		}
		return "Dye sheep" +  colorName + " - " + (amount - progress) + "x";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + amount + "; COLOR:" + (colorName.isEmpty() ? " ANY" : colorName + "(" + (15 - color.getDyeData()) + ")") + coloredDesc();
	}

	public boolean checkDye(int data) {
		return (color == null) || (color.getDyeData() == data);
	}
	
	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		
		if(amount > 1)
			section.set("amount", amount);
		if(color != null) {
			section.set("color", Util.serializeColor(color));
		}
	}
	
	public static Objective deser(ConfigurationSection section) {
		int amt = 1;
		DyeColor col = null;
		amt = section.getInt("amount", 1);
		if(amt < 1)
			return null;
		col = Util.parseColor(section.getString("color", "default"));
		return new DyeObjective(amt, col);
	}
}
