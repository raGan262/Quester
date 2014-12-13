package me.ragan262.quester.elements;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.Validate;

public class TriggerContext {
	
	public static final TriggerContext EMPTY = new EmptyTriggerContext();
	
	private final String type;
	protected final Map<String, Object> map = new HashMap<>();
	
	public TriggerContext(final String type) {
		Validate.notNull(type, "Trigger context type can't be null.");
		this.type = type.toUpperCase();
	}
	
	public String getType() {
		return type;
	}
	
	public void put(final String key, final Object value) {
		map.put(key.toUpperCase(), value);
	}
	
	public Object get(final String key) {
		return map.get(key.toUpperCase());
	}
	
	private static class EmptyTriggerContext extends TriggerContext {
		
		public EmptyTriggerContext() {
			super("EMPTY");
		}
		
		@Override
		public void put(final String key, final Object value) {
			// nada
		}
	}
}
