package com.gmail.molnardad.quester.objectives;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.utils.Util;

public abstract class Objective {

	@SuppressWarnings("unchecked")
	private static Class<? extends Objective>[] classes = new Class[]{
		BreakObjective.class,
		CollectObjective.class,
		CraftObjective.class,
		DeathObjective.class,
		EnchantObjective.class,
		ExpObjective.class,
		FishObjective.class,
		ItemObjective.class,
		LocObjective.class,
		MilkObjective.class,
		MobKillObjective.class,
		MoneyObjective.class,
		PlaceObjective.class,
		PlayerKillObjective.class,
		ShearObjective.class,
		SmeltObjective.class,
		TameObjective.class,
		WorldObjective.class,
		ActionObjective.class,
		NpcObjective.class,
		DyeObjective.class,
		BossObjective.class,
		NpcKillObjective.class
	};
	String desc = "";
	Set<Integer> prerequisites = new HashSet<Integer>();
	
	public abstract String getType();
	
	
	public Set<Integer> getPrerequisites() {
		return prerequisites;
	}
	
	public void addPrerequisity(int newPre) {
		prerequisites.add(newPre);
	}
	
	public void removePrerequisity(int pre) {
		prerequisites.remove(pre);
	}
	
	public String coloredDesc() {
		String des = "";
		if(!prerequisites.isEmpty()) {
			des += " PRE: " + Util.serializePrerequisites(prerequisites, ",");
		}
		if(!desc.isEmpty()) {
			des += "\n  - " + ChatColor.translateAlternateColorCodes('&', desc) + ChatColor.RESET;
		}
		return des;
	}
	
	public void addDescription(String msg) {
		this.desc += (" " + msg).trim();
	}
	
	public void removeDescription() {
		this.desc = "";
	}
	
	public int getTargetAmount() {
		return 1;
	}
	
	public boolean isComplete(Player player, int progress) {
		return progress > 0;
	}
	
	public boolean tryToComplete(Player player) {
		return false;
	}
	
	
	public abstract String progress(int progress);
	public abstract String toString();
	
	public abstract void serialize(ConfigurationSection section);
	
	void serialize(ConfigurationSection section, String type) {
		section.set("type", type);
		if(!desc.isEmpty())
			section.set("description", desc);
		if(!prerequisites.isEmpty())
			section.set("prerequisites", Util.serializePrerequisites(prerequisites));
	}
	
	public static Objective deserialize(ConfigurationSection section) {
		if(section == null) {
			Quester.log.severe("Objective deserialization error: section null.");
			return null;
		}
		Objective obj = null;
		String type = null, des = null;
		Set<Integer> prereq = new HashSet<Integer>();
		
		if(section.isString("type"))
			type = section.getString("type");
		else {
			Quester.log.severe("Objective type missing.");
			return null;
		}
		if(section.isString("description")) {
			des = section.getString("description");
		}
		if(section.isString("prerequisites")) {
			try {
				prereq = Util.deserializePrerequisites(section.getString("prerequisites"));
			} catch (Exception ignore) {}
		}
		
		boolean success = false;
		for(Class<? extends Objective> c : classes) {
			try {
				if(((String) c.getField("TYPE").get(null)).equalsIgnoreCase(type)) {
					try {
						success = true;
						Method deser = c.getMethod("deser", ConfigurationSection.class);
						obj = (Objective) deser.invoke(null, section);
						if(obj == null)
							return null;
						if(des != null)
							obj.addDescription(des);
						if(!prereq.isEmpty()) {
							for(int i : prereq) {
								obj.addPrerequisity(i);
							}
						}
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
			Quester.log.severe("Unknown objective type: '" + type  + "'");
		
		return obj;
	}
}
