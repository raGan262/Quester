package com.gmail.molnardad.quester.qevents;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.utils.Util;

@QElement("BLOCK")
public final class SetBlockQevent extends Qevent {

	private final Location location;
	public final int material;
	public final byte data;
	
	public SetBlockQevent(int mat, int dat, Location loc) {
		this.location = loc;
		this.material = mat;
		this.data = (byte) dat;
	}
	
	@Override
	public String info() {
		return material + ":" + data + "; " + "; LOC: " + Util.displayLocation(location);
	}

	@Override
	protected void run(Player player) {
		location.getBlock().setTypeIdAndData(material, data, true);
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("block", Util.serializeItem(material, data));
		section.set("location", Util.serializeLocString(location));
		
	}
	
	public static SetBlockQevent deser(ConfigurationSection section) {
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
		
		return new SetBlockQevent(mat, dat, loc);
	}
}
