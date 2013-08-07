package com.gmail.molnardad.quester.elements;

import java.lang.reflect.Method;

import org.apache.commons.lang.SerializationException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Ql;

public abstract class Qevent extends Element {
	
	private int delay = 0;
	private int occasion = -10;
	
	public final void setOccasion(final int occasion) {
		this.occasion = occasion;
	}
	
	public final void setOccasion(final int occasion, final int delay) {
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
	
	@Override
	public final String toString() {
		return "Event (type=" + getType() + ")";
	}
	
	public final static String parseOccasion(final int occ) {
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
					catch (final Exception e) {
						Ql.warning(getType() + " event external exception. [" + occasion + ":"
								+ delay + "]");
						Ql.debug("Exception", e);
					}
				}
			}.runTaskLater(plugin, delay * 20);
		}
		else {
			try {
				Qevent.this.run(player, plugin);
			}
			catch (final Exception e) {
				Ql.warning(getType() + " event external exception. [" + occasion + ":" + delay
						+ "]");
				Ql.debug("Exception", e);
			}
		}
	}
	
	protected abstract void save(StorageKey key);
	
	public final void serialize(final StorageKey key) {
		final String type = getType();
		if(type.isEmpty()) {
			throw new SerializationException("Unknown type");
		}
		save(key);
		key.setString("type", type);
		key.setInt("occasion", occasion);
		if(delay != 0) {
			key.setInt("delay", delay);
		}
	}
	
	public static final Qevent deserialize(final StorageKey key) {
		if(!key.hasSubKeys()) {
			Ql.severe("Qevent deserialization error: no sybkeys.");
			return null;
		}
		
		final String type = key.getString("type");
		if(type == null) {
			Ql.severe("Event type missing.");
			return null;
		}
		
		Qevent qev = null;
		
		final Class<? extends Qevent> c = ElementManager.getInstance().getEventClass(type);
		if(c != null) {
			try {
				final Method load = c.getDeclaredMethod("load", StorageKey.class);
				load.setAccessible(true);
				qev = (Qevent) load.invoke(null, key);
				if(qev == null) {
					return null;
				}
				
				qev.occasion = key.getInt("occasion", -10);
				qev.delay = key.getInt("delay", 0);
			}
			catch (final Exception e) {
				Ql.severe("Error when deserializing " + c.getSimpleName()
						+ ". Method load() missing or invalid. " + e.getClass().getName());
				Ql.debug("Exception follows", e);
				return null;
			}
		}
		else {
			Ql.severe("Unknown event type: '" + type + "'");
		}
		return qev;
	}
}
