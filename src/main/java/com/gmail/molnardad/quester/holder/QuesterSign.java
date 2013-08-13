package com.gmail.molnardad.quester.holder;

import org.bukkit.Location;

import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.SerUtils;

public class QuesterSign {
	
	private int holder = -1;
	private final Location location;
	
	public QuesterSign(final Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public int getHolderID() {
		return holder;
	}
	
	public void setHolderID(final int ID) {
		holder = ID;
	}
	
	public void serialize(final StorageKey key) {
		key.setString("location", SerUtils.serializeLocString(location));
		key.setInt("holder", holder);
	}
	
	public static QuesterSign deserialize(final StorageKey key) {
		Location loc;
		QuesterSign sign = null;
		
		try {
			loc = SerUtils.deserializeLocString(key.getString("location", ""));
			if(loc == null) {
				return null;
			}
			sign = new QuesterSign(loc);
			sign.setHolderID(key.getInt("holder", -1));
		}
		catch (final Exception ignore) {}
		
		return sign;
	}
}
