package me.ragan262.quester.elements;

import java.lang.reflect.Method;

import me.ragan262.quester.Quester;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.lang.QuesterLang;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;

import org.apache.commons.lang.SerializationException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class Condition extends Element {
	
	private String desc = "";
	private boolean isCustomMessage = false;
	
	private String coloredDesc(final LanguageManager langMan) {
		String result = "";
		if(!desc.isEmpty()) {
			result += "\n  - ";
			if(isCustomMessage) {
				if(langMan != null) {
					if(langMan.customMessageExists(desc)) {
						result += ChatColor.GREEN;
					}
					else {
						result += ChatColor.RED;
					}
				}
				result += "LANG:";
			}
			result += ChatColor.translateAlternateColorCodes('&', desc) + ChatColor.RESET;
		}
		return result;
	}
	
	public final void addDescription(final String msg) {
		final boolean doCheck = desc.isEmpty();
		desc += (" " + msg).trim();
		if(doCheck) {
			customMessageCheck();
		}
	}
	
	public final void removeDescription() {
		desc = "";
	}
	
	private void customMessageCheck() {
		final String langMsg = LanguageManager.getCustomMessageKey(desc);
		if(langMsg != null) {
			desc = langMsg;
			isCustomMessage = true;
		}
		else {
			isCustomMessage = false;
		}
	}
	
	protected abstract String parseDescription(Player player, String description);
	
	protected abstract String show();
	
	protected abstract String info();
	
	public abstract boolean isMet(Player player, Quester plugin);
	
	public String inShow(final Player player, final QuesterLang lang) {
		if(!desc.isEmpty()) {
			final String actualDesc;
			if(isCustomMessage) {
				actualDesc = lang.getCustom(desc);
			}
			else {
				actualDesc = desc;
			}
			return ChatColor
					.translateAlternateColorCodes('&', parseDescription(player, actualDesc));
		}
		return show();
	}
	
	public String inInfo(final LanguageManager langMan) {
		return getType() + ": " + info() + coloredDesc(langMan);
	}
	
	@Override
	public final String toString() {
		return "Condition (type=" + getType() + ")";
	}
	
	protected abstract void save(StorageKey key);
	
	public final void serialize(final StorageKey key) throws SerializationException {
		final String type = getType();
		if(type.isEmpty()) {
			throw new SerializationException("Unknown type");
		}
		save(key);
		key.setString("type", type);
		if(!desc.isEmpty()) {
			if(isCustomMessage) {
				key.setString("description", "LANG:" + desc);
			}
			else {
				key.setString("description", desc);
			}
		}
	}
	
	public static final Condition deserialize(final StorageKey key) {
		if(!key.hasSubKeys()) {
			Ql.severe("Condition deserialization error: no subkeys");
			return null;
		}
		
		final String type = key.getString("type");
		if(type == null) {
			Ql.severe("Condition type missing.");
			return null;
		}
		
		Condition con = null;
		
		final Class<? extends Condition> c = ElementManager.getInstance().getConditionClass(type);
		if(c != null) {
			try {
				final Method load = c.getDeclaredMethod("load", StorageKey.class);
				load.setAccessible(true);
				con = (Condition) load.invoke(null, key);
				if(con == null) {
					return null;
				}
				
				final String des = key.getString("description");
				if(des != null) {
					con.addDescription(des);
				}
			}
			catch (final Exception e) {
				Ql.severe("Error when deserializing " + c.getSimpleName()
						+ ". Method load() missing or invalid. " + e.getClass().getName());
				Ql.debug("Exception follows", e);
				return null;
			}
		}
		else {
			Ql.severe("Unknown condition type: '" + type + "'");
		}
		
		return con;
	}
}
