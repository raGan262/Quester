package com.gmail.molnardad.quester.qevents;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.utils.Util;

@QElement("TELE")
public final class TeleportQevent extends Qevent {

	private final Location location;
	
	public TeleportQevent(Location loc) {
		this.location = loc;
	}
	
	@Override
	public String info() {
		return Util.displayLocation(location);
	}

	@Override
	protected void run(Player player, Quester plugin) {
		Location loc = player.getLocation().clone();
		loc.setY(loc.getY()+1);
		player.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
		player.teleport(location, TeleportCause.PLUGIN);
		loc = location.clone();
		loc.setY(loc.getY()+1);
		player.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 1);
	}

	@QCommand(
			min = 1,
			max = 1,
			usage = "{<location>}")
	public static Qevent fromCommand(QCommandContext context) {
		return new TeleportQevent(Util.getLoc(context.getPlayer(), context.getString(0), context.getSenderLang()));
	}

	// TODO serialization
	
	public void serialize(ConfigurationSection section) {
		section.set("location", Util.serializeLocString(location));
		
	}
	
	public static TeleportQevent deser(ConfigurationSection section) {
		Location loc = null;
		try {
			if(section.isString("location"))
				loc = Util.deserializeLocString(section.getString("location"));
			if(loc == null)
				return null;
		} catch (Exception e) {
			return null;
		}
		
		return new TeleportQevent(loc);
	}
}
