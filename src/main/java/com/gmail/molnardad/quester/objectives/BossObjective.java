package com.gmail.molnardad.quester.objectives;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
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
	
	@QCommand(
			min = 1,
			max = 2,
			usage = "<name> [amount]")
	public static Objective fromCommand(QCommandContext context) throws QCommandException {
		int amt = 1;
		String boss = context.getString(0);
		if(boss.equalsIgnoreCase("ANY")) {
			boss = "";
		}
		if(context.length() > 1) {
			amt = context.getInt(1);
			if(amt < 1) {
				throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_POSITIVE);
			}
		}
		return new BossObjective(boss, amt);
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
