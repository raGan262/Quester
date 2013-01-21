package com.gmail.molnardad.quester.objectives;

import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.utils.Util;

@QElement("SHEAR")
public final class ShearObjective extends Objective {

	private final DyeColor color;
	private final int amount;

	public ShearObjective(int amt, DyeColor col) {
		amount = amt;
		color = col;
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}

	@Override
	protected String show(int progress) {
		String strCol = (color == null) ? "any" : color.name().replace('_', ' ').toLowerCase() ;
		return "Shear " + strCol + " sheep - " + (amount - progress) + "x";
	}
	
	@Override
	protected String info() {
		String strCol = (color == null) ? "ANY" : color.name() ;
		return strCol + "; AMT: " + amount;
	}

	// TODO serialization
	public void serialize(ConfigurationSection section) {
		if(color != null)
			section.set("color", Util.serializeColor(color));
		if(amount > 1)
			section.set("amount", amount);
	}
	
	public static Objective deser(ConfigurationSection section) {
		int amt = 1;
		DyeColor col = null;
		col = Util.parseColor(section.getString("color", "default"));
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		if(amt < 1)
			amt = 1;
		return new ShearObjective(amt, col);
	}
	
	//Custom methods
	
	public boolean check(DyeColor col) {
		if(col == color || color == null) {
			return true;	
		}
		return false;
	}
}
