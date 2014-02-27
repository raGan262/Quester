package me.ragan262.quester;

import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.holder.QuestHolder;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

public class ActionSource {
	
	public static final int QUESTER = 0;
	public static final int COMMAND = 1;
	public static final int LISTENER = 2;
	public static final int HOLDER = 3;
	public static final int EVENT = 4;
	public static final int OTHER = 5;
	public static final int ADMIN = 6;
	
	public static final ActionSource BLANKSOURCE = new ActionSource(ActionSource.OTHER, null);
	public static final ActionSource QUESTERSOURCE = new ActionSource(ActionSource.QUESTER, null);
	
	private final Object source;
	private final int type;
	
	private ActionSource(final int type, final Object sourceObject) {
		this.type = type;
		source = sourceObject;
	}
	
	public int getType() {
		return type;
	}
	
	public Object getSourceObject() {
		return source;
	}
	
	public boolean is(final int type) {
		return this.type == type;
	}
	
	public static ActionSource customSource(int type, final Object sourceObject) {
		if(type < 100) {
			type = ActionSource.OTHER;
		}
		return new ActionSource(type, sourceObject);
	}
	
	public static ActionSource commandSource(final CommandSender sender) {
		validate(sender);
		return new ActionSource(ActionSource.COMMAND, sender);
	}
	
	public static ActionSource adminSource(final CommandSender sender) {
		validate(sender);
		return new ActionSource(ActionSource.ADMIN, sender);
	}
	
	public static ActionSource listenerSource(final Event event) {
		validate(event);
		return new ActionSource(ActionSource.LISTENER, event);
	}
	
	public static ActionSource holderSource(final QuestHolder holder) {
		validate(holder);
		return new ActionSource(ActionSource.HOLDER, holder);
	}
	
	public static ActionSource eventSource(final Qevent event) {
		validate(event);
		return new ActionSource(ActionSource.EVENT, event);
	}
	
	public static ActionSource otherSource(final Object sourceObject) {
		return new ActionSource(ActionSource.OTHER, sourceObject);
	}
	
	private static void validate(final Object o) {
		if(o == null) {
			throw new IllegalArgumentException("Source cannot be null.");
		}
	}
}
