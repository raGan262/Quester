package com.gmail.molnardad.quester.elements;

import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.managers.DataManager;
import com.gmail.molnardad.quester.qevents.CancelQevent;
import com.gmail.molnardad.quester.qevents.CommandQevent;
import com.gmail.molnardad.quester.qevents.EffectQevent;
import com.gmail.molnardad.quester.qevents.ExperienceQevent;
import com.gmail.molnardad.quester.qevents.ExplosionQevent;
import com.gmail.molnardad.quester.qevents.ItemQevent;
import com.gmail.molnardad.quester.qevents.LightningQevent;
import com.gmail.molnardad.quester.qevents.MessageQevent;
import com.gmail.molnardad.quester.qevents.MoneyQevent;
import com.gmail.molnardad.quester.qevents.ObjectiveCompleteQevent;
import com.gmail.molnardad.quester.qevents.PointQevent;
import com.gmail.molnardad.quester.qevents.QuestQevent;
import com.gmail.molnardad.quester.qevents.SetBlockQevent;
import com.gmail.molnardad.quester.qevents.SpawnQevent;
import com.gmail.molnardad.quester.qevents.TeleportQevent;
import com.gmail.molnardad.quester.qevents.ToggleQevent;

public abstract class Qevent extends Element {

	@SuppressWarnings("unchecked")
	private static Class<? extends Qevent>[] classes = new Class[]{
															CancelQevent.class,
															CommandQevent.class,
															ExplosionQevent.class,
															LightningQevent.class,
															MessageQevent.class,
															ObjectiveCompleteQevent.class,
															QuestQevent.class,
															SetBlockQevent.class,
															SpawnQevent.class,
															TeleportQevent.class,
															ToggleQevent.class,
															EffectQevent.class,
															ExperienceQevent.class,
															MoneyQevent.class,
															PointQevent.class,
															ItemQevent.class
														};
	private long delay = 0;
	private int occasion = -10;

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
		return info() + delayString();
	}
	
	public final String toString() {
		return "Condition (type=" + getType() + ")";
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
	
	// TODO serialization
	
	protected void serialize(ConfigurationSection section, String type) {
		section.set("type", type);
		section.set("occasion", occasion);
		if(delay != 0)
			section.set("delay", delay);
		else
			section.set("delay", null);
	}
	
	public static Qevent deserialize(ConfigurationSection section) {
		if(section == null) {
			Quester.log.severe("Qevent deserialization error: section null.");
			return null;
		}
		Qevent qev = null;
		int occ = -10, del = 0;
		String type;
		
		if(section.isInt("occasion"))
			occ = section.getInt("occasion");
		if(section.isInt("delay"))
			del = section.getInt("delay");
		if(section.isString("type"))
			type = section.getString("type");
		else {
			Quester.log.severe("Event type missing.");
			return null;
		}
		boolean success = false;
		for(Class<? extends Qevent> c : classes) {
			try {
				if(((String) c.getField("TYPE").get(null)).equalsIgnoreCase(type)) {
					try {
						Method deser = c.getMethod("deser", int.class, int.class, ConfigurationSection.class);
						qev = (Qevent) deser.invoke(null, occ, del, section);
						if(qev == null)
							return null;
						success = true;
						break;
					} catch (Exception e) {
						Quester.log.severe("Error when deserializing " + c.getSimpleName() + ". Method deser() missing or broken. " + e.getClass().getName());
						if(DataManager.debug)
							e.printStackTrace();
						return null;
					}
				}
			} catch (Exception e) {
				Quester.log.severe("Error when deserializing " + c.getSimpleName() + ". Field 'TYPE' missing or access denied. " + e.getClass().getName());
				if(DataManager.debug)
					e.printStackTrace();
				return null;
			}
		}
		if(!success)
			Quester.log.severe("Unknown event type: '" + type  + "'");
		
		return qev;
	}
}
