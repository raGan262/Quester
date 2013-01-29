package com.gmail.molnardad.quester.elements;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.managers.DataManager;
import com.gmail.molnardad.quester.objectives.ActionObjective;
import com.gmail.molnardad.quester.objectives.BossObjective;
import com.gmail.molnardad.quester.objectives.BreakObjective;
import com.gmail.molnardad.quester.objectives.CollectObjective;
import com.gmail.molnardad.quester.objectives.CraftObjective;
import com.gmail.molnardad.quester.objectives.DeathObjective;
import com.gmail.molnardad.quester.objectives.DyeObjective;
import com.gmail.molnardad.quester.objectives.EnchantObjective;
import com.gmail.molnardad.quester.objectives.ExpObjective;
import com.gmail.molnardad.quester.objectives.FishObjective;
import com.gmail.molnardad.quester.objectives.ItemObjective;
import com.gmail.molnardad.quester.objectives.LocObjective;
import com.gmail.molnardad.quester.objectives.MilkObjective;
import com.gmail.molnardad.quester.objectives.MobKillObjective;
import com.gmail.molnardad.quester.objectives.MoneyObjective;
import com.gmail.molnardad.quester.objectives.NpcKillObjective;
import com.gmail.molnardad.quester.objectives.NpcObjective;
import com.gmail.molnardad.quester.objectives.PlaceObjective;
import com.gmail.molnardad.quester.objectives.PlayerKillObjective;
import com.gmail.molnardad.quester.objectives.ShearObjective;
import com.gmail.molnardad.quester.objectives.SmeltObjective;
import com.gmail.molnardad.quester.objectives.TameObjective;
import com.gmail.molnardad.quester.objectives.WorldObjective;
import com.gmail.molnardad.quester.utils.Util;

public abstract class Objective extends Element {

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
	private String desc = "";
	private Set<Integer> prerequisites = new HashSet<Integer>();
	
	public Set<Integer> getPrerequisites() {
		return prerequisites;
	}
	
	public void addPrerequisity(int newPre) {
		prerequisites.add(newPre);
	}
	
	public void removePrerequisity(int pre) {
		prerequisites.remove(pre);
	}
	
	private String coloredDesc() {
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
	
	public final boolean isComplete(int progress) {
		return progress > getTargetAmount();
	}
	
	public abstract int getTargetAmount();
	
	protected String parseDescription(String description) {
		return description;
	}
	protected abstract String show(int progress);
	protected abstract String info();
	
	public boolean tryToComplete(Player player) {
		return false;
	}
	
	public String inShow() {
		return inShow(0);
	}
	
	public String inShow(int progress) {
		if(!desc.isEmpty()) {
			String partiallyParsed = desc
					.replaceAll("%r", String.valueOf(getTargetAmount() - progress))
					.replaceAll("%t", String.valueOf(getTargetAmount()));
			return ChatColor.translateAlternateColorCodes('&', parseDescription(partiallyParsed));
		}
		return show(progress);
	}
	
	public String inInfo() {
		return getType() + ": " + info() + coloredDesc();
	}

	public final String toString() {
		return "Objective (type=" + getType() + ")";
	}

	// TODO serialization
	
	protected void serialize(ConfigurationSection section, String type) {
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
						if(DataManager.getInstance().debug)
							e.printStackTrace();
						return null;
					}
				}
			} catch (Exception e) {
				Quester.log.severe("Error when deserializing " + c.getSimpleName() + ". Field 'TYPE' missing or access denied. " + e.getClass().getName());
				if(DataManager.getInstance().debug)
					e.printStackTrace();
				return null;
			}
		}
		if(!success)
			Quester.log.severe("Unknown objective type: '" + type  + "'");
		
		return obj;
	}
}
