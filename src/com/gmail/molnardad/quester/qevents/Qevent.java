package com.gmail.molnardad.quester.qevents;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;

public abstract class Qevent implements ConfigurationSerializable {

	protected final long delay;
	protected final int occasion;
	
	public Qevent(int occ, int del) {
		occasion = occ;
		delay = del;
	}
	
	public abstract String getType();
	public abstract int getOccasion();
	public abstract String toString();
	public abstract void run(Player player);

	public int execute(final Player player) {
		if(delay > 0)
			return Bukkit.getScheduler().scheduleSyncDelayedTask(Quester.plugin, new Runnable() {
				  public void run() {
					    Qevent.this.run(player);
					  }
			}, delay*20);
		else
			Qevent.this.run(player);
		return 0;
	}
	
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
