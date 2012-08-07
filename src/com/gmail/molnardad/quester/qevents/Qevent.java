package com.gmail.molnardad.quester.qevents;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public abstract class Qevent implements ConfigurationSerializable {

	public abstract String getType();
	public abstract int getOccasion();
	public abstract boolean execute(Player player);
	public abstract String toString();
	
	String parseOccasion(int i) {
		String result;
		switch(i) {
			case -1 : result = "START";
				break;
			case -2 : result = "CANCEL";
				break;
			case -3 : result = "DONE";
				break;
			default : result = "OBJ";
		}
		return result;
	}
	
}
