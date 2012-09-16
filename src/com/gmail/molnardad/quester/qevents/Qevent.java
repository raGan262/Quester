package com.gmail.molnardad.quester.qevents;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.Quester;

public abstract class Qevent {

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
	long delay = 0;
	int occasion = -10;
	
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
	
	public abstract void serialize(ConfigurationSection section);
	
	void serialize(ConfigurationSection section, String type) {
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
						Quester.log.severe("Error when deserializing " + c.getName() + ". Method deser() missing or broken. " + e.getClass().getName());
						if(QuestData.debug)
							e.printStackTrace();
						return null;
					}
				}
			} catch (Exception e) {
				Quester.log.severe("Error when deserializing " + c.getName() + ". Field 'TYPE' missing or access denied. " + e.getClass().getName());
				if(QuestData.debug)
					e.printStackTrace();
				return null;
			}
		}
		if(!success)
			Quester.log.severe("Unknown event type: '" + type  + "'");
		
		return qev;
	}
}
