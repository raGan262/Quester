package me.ragan262.quester.objectives;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;

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
	
	@Command(min = 1, max = 2, usage = "<amount> [obj name]")
	public static Objective fromCommand(final QuesterCommandContext context) throws CommandException {
		final int id = context.getInt(0);
		if(id < 0) {
			throw new CommandException(context.getSenderLang().get("ERROR_CMD_AMOUNT_POSITIVE"));
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
