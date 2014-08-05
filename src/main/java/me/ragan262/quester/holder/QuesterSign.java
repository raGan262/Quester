package me.ragan262.quester.holder;

import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.SerUtils;
import org.bukkit.Location;

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
		catch(final Exception ignore) {}
		
		return sign;
	}
}
