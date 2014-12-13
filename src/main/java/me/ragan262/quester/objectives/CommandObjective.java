package me.ragan262.quester.objectives;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;

@QElement("CMD")
public class CommandObjective extends Objective {
	
	private final String index;
	
	public CommandObjective(final String index) {
		this.index = String.valueOf(index); // eliminate null
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(final int progress) {
		return "run runaction command with index " + index;
	}
	
	@Override
	protected String info() {
		return index;
	}
	
	@Command(min = 1, max = 1, usage = "<index>")
	public static Objective fromCommand(final QuesterCommandContext context) {
		return new CommandObjective(context.getString(0));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("index", index);
	}
	
	protected static Objective load(final StorageKey key) {
		return new CommandObjective(key.getString("index"));
	}
	
	public boolean evaluate0(final String index) {
		return index.equals(index);
	}
	
}
