package com.gmail.molnardad.quester.elements;

import java.lang.reflect.Method;

import org.apache.commons.lang.SerializationException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.storage.StorageKey;

public abstract class Condition extends Element {
	
	private String desc = "";
	
	private String coloredDesc() {
		String des = "";
		if(!desc.isEmpty()) {
			des = "\n  - " + ChatColor.translateAlternateColorCodes('&', desc) + ChatColor.RESET;
		}
		return des;
	}
	public final void addDescription(String msg) {
		this.desc += (" " + msg).trim();
	}
	
	public final void removeDescription() {
		this.desc = "";
	}
	
	protected abstract String parseDescription(String description);
	protected abstract String show();
	protected abstract String info();
	public abstract boolean isMet(Player player, Quester plugin);
	
	public String inShow() {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', parseDescription(desc));
		}
		return show();
	}
	
	public String inInfo() {
		return getType() + ": " + info() + coloredDesc();
	}

	public final String toString() {
		return "Condition (type=" + getType() + ")";
	}
	
	protected abstract void save(StorageKey key);
	
	public final void serialize(StorageKey key) throws SerializationException {
		String type = getType();
		if(type.isEmpty()) {
			throw new SerializationException("Unknown type");
		}
		save(key);
		key.setString("type", type);
		if(!desc.isEmpty()) {
			key.setString("description", desc);
		}
	}
	
	public static final Condition deserialize(StorageKey key) {
		if(!key.hasSubKeys()) {
			Quester.log.severe("Condition deserialization error: no subkeys");
			return null;
		}
		Condition con = null;
		String type = null, des = null;
		
		if(key.getString("type") != null) {
			type = key.getString("type");
		}
		else {
			Quester.log.severe("Condition type missing.");
			return null;
		}
		if(key.getString("description") != null) {
			des = key.getString("description");
		}
		Class<? extends Condition> c = ElementManager.getInstance().getConditionClass(type);
		if(c != null) {
			try {
				Method load = c.getDeclaredMethod("load", StorageKey.class);
				load.setAccessible(true);
				con = (Condition) load.invoke(null, key);
				if(con == null) {
					return null;
				}
				if(des != null) {
					con.addDescription(des);
				}
			} catch (Exception e) {
				Quester.log.severe("Error when deserializing " + c.getSimpleName() + ". Method load() missing or invalid. " + e.getClass().getName());
				if(QConfiguration.debug) {
					e.printStackTrace();
				}
				return null;
			}
		}
		else {
			Quester.log.severe("Unknown condition type: '" + type  + "'");
		}
		
		return con;
	}
}
