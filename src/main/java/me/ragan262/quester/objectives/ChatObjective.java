package me.ragan262.quester.objectives;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.Objective;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.storage.StorageKey;

@QElement("CHAT")
public final class ChatObjective extends Objective {
	
	private final String regex;
	private final boolean inverted;
	
	public ChatObjective(final String regex, final boolean inverted) {
		this.regex = regex;
		this.inverted = inverted;
	}
	
	@Override
	public int getTargetAmount() {
		return 1;
	}
	
	@Override
	protected String show(final int progress) {
		final String type = inverted ? "doesn't match" : "matches";
		return "Say anything that " + type + " \"" + regex + "\".";
	}
	
	@Override
	protected String info() {
		final String flags = inverted ? " (-i)" : "";
		return "\"" + regex + "\" " + flags;
	}
	
	@Command(min = 1, max = 1, usage = "<regex> (-i)")
	public static Objective fromCommand(final QuesterCommandContext context) {
		
		return new ChatObjective(context.getString(0), context.hasFlag('i'));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("regex", regex);
		if(inverted) {
			key.setBoolean("inverted", true);
		}
	}
	
	protected static Objective load(final StorageKey key) {
		final String regex = key.getString("regex", null);
		if(regex == null) {
			return null;
		}
		return new ChatObjective(regex, key.getBoolean("inverted", false));
	}
	
	public boolean matches(final String message) {
		return message.matches(regex) != inverted;
	}
}
