package com.gmail.molnardad.quester.objectives;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;

@QElement("BOSS")
public final class BossObjective extends Objective {

	private final int amount;
	private final String name;
	
	public BossObjective(String boss, int amt) {
		amount = amt;
		name = boss;
	}
	
	public boolean nameCheck(String boss) {
		if(name.isEmpty()) {
			return true;
		}
		return name.equalsIgnoreCase(boss);
	}

	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(int progress) {
		return "Kill boss named " + name + " - " + (amount - progress) + "x";
	}
	
	@Override
	protected String info() {
		return name + "; AMT: " + amount;
	}

	// TODO serialization

	public void serialize(ConfigurationSection section) {
		section.set("amount", amount);
		section.set("boss", name);
	}
	
	public static Objective deser(ConfigurationSection section) {
		int amt = 0;
		String boss = "";
		boss = section.getString("boss", "");
		if(section.isInt("amount"))
			amt = section.getInt("amount");
		if(amt < 1 || boss.isEmpty())
			return null;
		return new BossObjective(boss, amt);
	}
}
