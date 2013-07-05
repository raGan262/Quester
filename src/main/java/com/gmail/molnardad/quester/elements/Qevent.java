package com.gmail.molnardad.quester.elements;

import java.lang.reflect.Method;

import org.apache.commons.lang.SerializationException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.storage.StorageKey;

public abstract class Qevent extends Element {

	private int delay = 0;
	private int occasion = -10;

	public final void setOccasion(int occasion) {
		this.occasion = occasion;
	}
	
	public final void setOccasion(int occasion, int delay) {
		this.occasion = occasion;
		this.delay = delay;
	}
	
	public final int getOccasion() {
		return occasion;
	}
	
	protected abstract String info();
	protected abstract void run(Player player, Quester plugin);

	private String delayString() {
		if(delay > 0) {
			return ChatColor.RESET + "\n - DELAY: " + delay;
		}
		else {
			return "";
		}
	}
	
	public String inInfo() {
		return getType() + ": " + info() + delayString();
	}
	
	public final String toString() {
		return "Event (type=" + getType() + ")";
	}
	
	public final static String parseOccasion(int occ) {
		if(occ == -1) {
			return "On start";
		}
		else if(occ == -2) {
			return "On cancel";
		}
		else if(occ == -3) {
			return "On complete";
		}
		else if(occ >= 0) {
			return "On objective";
		}
		else {
			return "Unknown occasion";
		}
	}
	
	public final void execute(final Player player, final Quester plugin) {
		
		if(delay > 0) {
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						Qevent.this.run(player, plugin);
					}
					catch (Exception e) {
						Quester.log.warning(getType() + " event external exception. [" + occasion + ":" + delay + "]");
						e.printStackTrace();
					}
				}
			}.runTaskLater(plugin, delay*20);
		} else {
			try {
				Qevent.this.run(player, plugin);
			}
			catch (Exception e) {
				Quester.log.warning(getType() + " event external exception. [" + occasion + ":" + delay + "]");
				e.printStackTrace();
			}
		}
	}

	protected abstract void save(StorageKey key);
	
	public final void serialize(StorageKey key) {
		String type = getType();
		if(type.isEmpty()) {
			throw new SerializationException("Unknown type");
		}
		save(key);
		key.setString("type", type);
		key.setInt("occasion", occasion);
		if(delay != 0) {
			key.setLong("delay", delay);
		}
		else {
			key.removeKey("delay");
		}
	}
	
	public static final Qevent deserialize(StorageKey key) {
		if(!key.hasSubKeys()) {
			Quester.log.severe("Qevent deserialization error: no sybkeys.");
			return null;
		}
		Qevent qev = null;
		int occ = -10, del = 0;
		String type = null;
		occ = key.getInt("occasion", -10);
		del = key.getInt("delay", 0);
		type = key.getString("type");
		if(type == null) {
			Quester.log.severe("Event type missing.");
			return null;
		}
		Class<? extends Qevent> c = ElementManager.getInstance().getEventClass(type);
		if(c != null) {
			try {
				Method load = c.getDeclaredMethod("load", StorageKey.class);
				load.setAccessible(true);
				qev = (Qevent) load.invoke(null, key);
				if(qev == null) {
					return null;
				}
				qev.occasion = occ;
				qev.delay = del;
			} catch (Exception e) {
				Quester.log.severe("Error when deserializing " + c.getSimpleName() + ". Method load() missing or invalid. " + e.getClass().getName());
				if(QConfiguration.debug) {
					e.printStackTrace();
				}
				return null;
			}
		}
		else {
			Quester.log.severe("Unknown event type: '" + type  + "'");
		}
		return qev;
	}
}
