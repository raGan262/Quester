package com.gmail.molnardad.quester.objectives;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;

@QElement("NPCKILL")
public final class NpcKillObjective extends Objective {

	private final String name;
	private final String strName;
	private final int amount;
	
	public NpcKillObjective(String name, int amt) {
		this.name = name;
		amount = amt;
		strName = name == null ? "any NPC" : "NPC named " + name;
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(int progress) {
		return "Kill " + strName + " - " + amount + "x";
	}
	
	@Override
	protected String info() {
		return (name == null ? "ANY" : name) + "; AMT: " + amount;
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("name", name);
		if(amount > 1) {
			section.set("amount", amount);
		}
	}
	
	public static Objective deser(ConfigurationSection section) {
		int amt = 1;
		String nm = null;
		amt = section.getInt("amount", 1);
		if(amt < 1)
			return null;
		nm = section.getString("name", null);
		return new NpcKillObjective(nm, amt);
	}
	
	//Custom methods
	
	public boolean checkNpc(String npcName) {
		if(name == null) {
			return true;
		}
		return name.equalsIgnoreCase(npcName);
	}
}
