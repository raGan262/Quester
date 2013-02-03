package com.gmail.molnardad.quester.elements;

import java.lang.reflect.Method;

import org.apache.commons.lang.SerializationException;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.conditions.ItemCondition;
import com.gmail.molnardad.quester.conditions.MoneyCondition;
import com.gmail.molnardad.quester.conditions.PermissionCondition;
import com.gmail.molnardad.quester.conditions.PointCondition;
import com.gmail.molnardad.quester.conditions.QuestCondition;
import com.gmail.molnardad.quester.conditions.QuestNotCondition;
import com.gmail.molnardad.quester.managers.DataManager;

public abstract class Condition extends Element {

	@SuppressWarnings("unchecked")
	private static Class<? extends Condition>[] classes = new Class[]{
		ItemCondition.class,
		MoneyCondition.class,
		PermissionCondition.class,
		PointCondition.class,
		QuestCondition.class,
		QuestNotCondition.class
	};
	
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
	
	// TODO serialization
	
	protected void save(ConfigurationSection section) throws SerializationException {
		String type = getType();
		if(type.isEmpty()) {
			throw new SerializationException("Unknown type");
		}
		section.set("type", type);
		if(!desc.isEmpty())
			section.set("description", desc);
	}
	
	public static Condition load(ConfigurationSection section) {
		if(section == null) {
			Quester.log.severe("Condition deserialization error: section null.");
			return null;
		}
		Condition con = null;
		String type = null, des = null;
		
		if(section.isString("type"))
			type = section.getString("type");
		else {
			Quester.log.severe("Condition type missing.");
			return null;
		}
		if(section.isString("description"))
			des = section.getString("description");
		
		boolean success = false;
		for(Class<? extends Condition> c : classes) {
			try {
				if(((String) c.getField("TYPE").get(null)).equalsIgnoreCase(type)) {
					try {
						Method deser = c.getMethod("deser", ConfigurationSection.class);
						con = (Condition) deser.invoke(null, section);
						if(con == null)
							return null;
						if(des != null)
							con.addDescription(des);
						success = true;
						break;
					} catch (Exception e) {
						Quester.log.severe("Error when deserializing " + c.getSimpleName() + ". Method deser() missing or broken. " + e.getClass().getName());
						if(DataManager.debug)
							e.printStackTrace();
						return null;
					}
				}
			} catch (Exception e) {
				Quester.log.severe("Error when deserializing " + c.getSimpleName() + ". Field 'TYPE' missing or access denied. " + e.getClass().getName());
				if(DataManager.debug)
					e.printStackTrace();
				return null;
			}
		}
		if(!success)
			Quester.log.severe("Unknown condition type: '" + type  + "'");
		
		return con;
	}
}
