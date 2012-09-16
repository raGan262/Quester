package com.gmail.molnardad.quester.conditions;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.Util;

public final class PermissionCondition extends Condition {
	
	public static final String TYPE = "PERMISSION";
	private final String perm;
	
	public PermissionCondition(String perm) {
		this.perm = perm;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isMet(Player player) {
		return Util.permCheck(player, perm, false);
	}
	
	@Override
	public String show() {
		if(!desc.isEmpty()) {
			return ChatColor.translateAlternateColorCodes('&', desc).replaceAll("%perm", perm);
		}
		return "Must have permission '" + perm + "'";
	}
	
	@Override
	public String toString() {
		return TYPE + ": " + perm + coloredDesc().replaceAll("%perm", perm);
	}
	
	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("permission", perm);
	}

	public static PermissionCondition deser(ConfigurationSection section) {
		String perm;
		
		if(section.isString("permission"))
			perm = section.getString("permission");
		else
			return null;
		
		return new PermissionCondition(perm);
	}
}
