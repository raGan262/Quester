package com.gmail.molnardad.quester.objectives;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("NPCKILL")
public final class NpcKillObjective extends Objective {
	
	private final String name;
	private final String strName;
	private final int amount;
	
	public NpcKillObjective(final String name, final int amt) {
		this.name = name;
		amount = amt;
		strName = name == null ? "any NPC" : "NPC named " + name;
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(final int progress) {
		return "Kill " + strName + " - " + amount + "x";
	}
	
	@Override
	protected String info() {
		return (name == null ? "ANY" : name) + "; AMT: " + amount;
	}
	
	@QCommand(min = 1, max = 2, usage = "<name> [amount]")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		int amt = 1;
		String name = null;
		if(!context.getString(0).equalsIgnoreCase("ANY")) {
			name = context.getString(0);
		}
		if(context.length() > 1) {
			amt = context.getInt(1);
			if(amt < 1) {
				throw new QCommandException(context.getSenderLang().ERROR_CMD_AMOUNT_POSITIVE);
			}
		}
		return new NpcKillObjective(name, amt);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("name", name);
		if(amount > 1) {
			key.setInt("amount", amount);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		int amt = 1;
		String nm = null;
		amt = key.getInt("amount", 1);
		if(amt < 1) {
			amt = 1;
		}
		nm = key.getString("name", null);
		return new NpcKillObjective(nm, amt);
	}
	
	// Custom methods
	
	public boolean checkNpc(final String npcName) {
		if(name == null) {
			return true;
		}
		return name.equalsIgnoreCase(npcName);
	}
}
