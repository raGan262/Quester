package com.gmail.molnardad.quester.objectives;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("BOSS")
public final class BossObjective extends Objective {

	/**
	 * @uml.property  name="amount"
	 */
	private final int amount;
	/**
	 * @uml.property  name="name"
	 */
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

	@Override
	protected void save(StorageKey key) {
		key.setInt("amount", amount);
		key.setString("boss", name);
	}
	
	protected static Objective load(StorageKey key) {
		int amt = 0;
		String boss = "";
		boss = key.getString("boss", "");
		amt = key.getInt("amount");
		if(amt < 1 || boss.isEmpty()) {
			return null;
		}
		return new BossObjective(boss, amt);
	}
}
