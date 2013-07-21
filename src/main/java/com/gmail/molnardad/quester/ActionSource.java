package com.gmail.molnardad.quester;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.holder.QuestHolder;

public class ActionSource {
	
	public static final int QUESTER = 0;
	public static final int COMMAND = 1;
	public static final int LISTENER = 2;
	public static final int HOLDER = 3;
	public static final int EVENT = 4;
	public static final int OTHER = 5;

	public static final ActionSource BLANKSOURCE = new ActionSource(ActionSource.OTHER, null);
	public static final ActionSource QUESTERSOURCE = new ActionSource(ActionSource.QUESTER, null);
	
	
	private final Object source;
	private final int type;
	
	private ActionSource(int type, Object sourceObject) {
		this.type = type;
		source = sourceObject;
	}
	
	public int getType() {
		return type;
	}
	
	public Object getSourceObject() {
		return source;
	}
	
	public boolean is(int type) {
		return this.type == type;
	}
	
	public static ActionSource customSource(int type, Object sourceObject) {
		if(type < 100) {
			type = ActionSource.OTHER;
		}
		return new ActionSource(type, sourceObject);
	}
	
	public static ActionSource commandSource(CommandSender sender) {
		validate(sender);
		return new ActionSource(ActionSource.COMMAND, sender);
	}
	
	public static ActionSource listenerSource(Event event) {
		validate(event);
		return new ActionSource(ActionSource.LISTENER, event);
	}
	
	public static ActionSource holderSource(QuestHolder holder) {
		validate(holder);
		return new ActionSource(ActionSource.HOLDER, holder);
	}
	
	public static ActionSource eventSource(Qevent event) {
		validate(event);
		return new ActionSource(ActionSource.EVENT, event);
	}
	
	public static ActionSource otherSource(Object sourceObject) {
		return new ActionSource(ActionSource.OTHER, sourceObject);
	}
	
	private static void validate(Object o) {
		if(o == null) {
			throw new IllegalArgumentException("Source cannot be null.");
		}
	}
}
