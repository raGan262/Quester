package com.gmail.molnardad.quester.objectives;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class BreakObjective implements Objective {

	private static final long serialVersionUID = 13500L;
	private final String TYPE = "BREAK";
	private final Material material;
	private final byte data;
	private final int amount;
	
	public BreakObjective(int amt, Material mat, byte dat) {
		amount = amt;
		material = mat;
		data = dat;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public byte getData() {
		return data;
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
		String datStr = data < 0 ? " of any type " : " of given type(" + data + ") ";
		return "Break " + material.name().toLowerCase() + datStr + "- " + (amount - progress) + "x.";
	}
	
	@Override
	public String toString() {
		String dataStr = (data < 0 ? "ANY" : String.valueOf(data));
		return TYPE + ": " + material.name() + "[" + material.getId() + "] DATA: " + dataStr + "; AMT: " + amount;
	}

	@Override
	public boolean finish(Player player) {
		return true;
	}

}
