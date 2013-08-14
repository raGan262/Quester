package com.gmail.molnardad.quester.objectives;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Objective;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("DUMMY")
public class DummyObjective extends Objective {
	
	private final int amount;
	private final String name;
	
	public DummyObjective(final int amount, final String name) {
		this.amount = amount;
		this.name = name;
	}
	
	@Override
	public int getTargetAmount() {
		return amount;
	}
	
	@Override
	protected String show(final int progress) {
		final String innerName = name.isEmpty() ? "Dummy objective" : name;
		return innerName + " - " + progress + "/" + amount;
	}
	
	@Override
	protected String info() {
		return amount + " - " + name;
	}
	
	@QCommand(min = 1, max = 2, usage = "<amount> [obj name]")
	public static Objective fromCommand(final QCommandContext context) throws QCommandException {
		final int id = context.getInt(0);
		if(id < 0) {
			throw new QCommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_POSITIVE"));
		}
		return new DummyObjective(id, context.getString(1, ""));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setInt("amount", amount);
		if(!name.isEmpty()) {
			key.setString("name", name);
		}
		
	}
	
	protected static Objective load(final StorageKey key) {
		final int amt = key.getInt("amount");
		if(amt < 1) {
			return null;
		}
		return new DummyObjective(amt, key.getString("name", ""));
	}
	
}
