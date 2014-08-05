package me.ragan262.quester.elements;

import java.util.ArrayList;
import java.util.List;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;
import org.apache.commons.lang.SerializationException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class Trigger extends Element {
	
	private final List<Condition> conditions = new ArrayList<Condition>();
	
	private boolean global = false;
	
	public void setGlobal(final boolean value) {
		global = value;
	}
	
	public boolean isGlobal() {
		return global;
	}
	
	protected abstract String info();
	
	protected abstract boolean evaluate0(Player player, TriggerContext context);
	
	public String inInfo() {
		final String globalColor = global ? String.valueOf(ChatColor.GOLD) : "";
		return globalColor + getType() + ChatColor.RESET + ": " + info();
	}
	
	public void addCondition(final Condition con) {
		if(con != null) {
			conditions.add(con);
		}
	}
	
	public boolean evaluate(final Player player, TriggerContext context) {
		for(final Condition c : conditions) {
			if(!c.isMet(player)) {
				return false;
			}
		}
		if(context == null) {
			context = TriggerContext.EMPTY;
		}
		return evaluate0(player, context);
	}
	
	@Override
	public final String toString() {
		return "Trigger (type=" + getType() + ")";
	}
	
	protected abstract void save(StorageKey key);
	
	public final void serialize(final StorageKey key) throws SerializationException {
		final String type = getType();
		if(type.isEmpty()) {
			throw new SerializationException("Unknown type");
		}
		save(key);
		key.setString("type", type);
		if(global) {
			key.setBoolean("global", global);
		}
		if(!conditions.isEmpty()) {
			final StorageKey subKey = key.getSubKey("conditions");
			for(int i = 0; i < conditions.size(); i++) {
				conditions.get(i).serialize(subKey.getSubKey(String.valueOf(i)));
			}
		}
	}
	
	public static final Trigger deserialize(final StorageKey key) {
		if(!key.hasSubKeys()) {
			Ql.severe("Trigger deserialization error: no subkeys");
			return null;
		}
		
		final String type = key.getString("type");
		if(type == null) {
			Ql.severe("Trigger type missing.");
			return null;
		}
		
		final ElementManager eMan = ElementManager.getInstance();
		if(!eMan.elementExists(Element.TRIGGER, type)) {
			Ql.severe("Unknown trigger type: '" + type + "'");
			return null;
		}
		
		try {
			final Trigger trig = eMan.invokeLoadMethod(Element.TRIGGER, type, key);
			if(trig == null) {
				return null;
			}
			
			trig.global = key.getBoolean("global", false);
			
			Condition con = null;
			if(key.getSubKey("conditions").hasSubKeys()) {
				final StorageKey subKey = key.getSubKey("conditions");
				final List<StorageKey> keys = subKey.getSubKeys();
				for(int i = 0; i < keys.size(); i++) {
					con = Condition.deserialize(subKey.getSubKey(String.valueOf(i)));
					if(con != null) {
						trig.addCondition(con);
					}
					else {
						Ql.severe("Error occured when deserializing condition ID " + i
								+ "in trigger " + key.getName() + "'.");
					}
				}
			}
			
			return trig;
		}
		catch(final Exception e) {
			Ql.severe("Error when deserializing trigger " + type
					+ ". Method load() missing or invalid. " + e.getClass().getName());
			Ql.debug("Exception follows", e);
			return null;
		}
	}
}
