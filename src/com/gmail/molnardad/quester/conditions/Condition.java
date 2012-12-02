package com.gmail.molnardad.quester.conditions;

import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.Quester;

public abstract class Condition {

	@SuppressWarnings("unchecked")
	private static Class<? extends Condition>[] classes = new Class[]{
		ItemCondition.class,
		MoneyCondition.class,
		PermissionCondition.class,
		PointCondition.class,
		QuestCondition.class,
		QuestNotCondition.class
	};
	String desc = "";
	
	public abstract String getType();
	
	public String coloredDesc() {
		String des = "";
		if(!desc.isEmpty()) {
			des = "\n  - " + ChatColor.translateAlternateColorCodes('&', desc) + ChatColor.RESET;
		}
		return des;
	}
	
	public void addDescription(String msg) {
		this.desc += (" " + msg).trim();
	}
	
	public void removeDescription() {
		this.desc = "";
	}
	
	public abstract boolean isMet(Player player);
	public abstract String show();
	public abstract String toString();

	public abstract void serialize(ConfigurationSection section);
	
	void serialize(ConfigurationSection section, String type) {
		section.set("type", type);
		if(!desc.isEmpty())
			section.set("description", desc);
	}
	
	public static Condition deserialize(ConfigurationSection section) {
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
						if(QuestData.debug)
							e.printStackTrace();
						return null;
					}
				}
			} catch (Exception e) {
				Quester.log.severe("Error when deserializing " + c.getSimpleName() + ". Field 'TYPE' missing or access denied. " + e.getClass().getName());
				if(QuestData.debug)
					e.printStackTrace();
				return null;
			}
		}
		if(!success)
			Quester.log.severe("Unknown condition type: '" + type  + "'");
		
		return con;
	}
}
