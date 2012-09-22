package com.gmail.molnardad.quester.qevents;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.utils.Util;

public final class SetBlockQevent extends Qevent {

	public static final String TYPE = "BLOCK";
	private final Location location;
	public final int material;
	public final byte data;
	
	public SetBlockQevent(int occ, int del, int mat, int dat, Location loc) {
		super(occ, del);
		this.location = loc;
		this.material = mat;
		this.data = (byte) dat;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public int getOccasion() {
		return occasion;
	}
	
	@Override
	public String toString() {
		String locStr = String.format("%.1f:%.1f:%.1f("+location.getWorld().getName()+")", location.getX(), location.getY(), location.getZ());
		return TYPE + ": DEL: " + delay + "; BLOCK: " + material + ":" + data + "; " + "; LOC: " + locStr;
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("block", Util.serializeItem(material, data));
		section.set("location", Util.serializeLocString(location));
		
	}
	
	public static SetBlockQevent deser(int occ, int del, ConfigurationSection section) {
		int mat = 0, dat = 0;
		Location loc = null;
		try {
			int[] itm = Util.parseItem(section.getString("block"));
			mat = itm[0];
			dat = itm[1];
			if(dat < 0)
				dat = 0;
			if(section.isString("location"))
				loc = Util.deserializeLocString(section.getString("location"));
			if(loc == null)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		return new SetBlockQevent(occ, del, mat, dat, loc);
	}

	@Override
	public void run(Player player) {
		location.getBlock().setTypeIdAndData(material, data, true);
	}
}
