package com.gmail.molnardad.quester.qevents;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("TELE")
public final class TeleportQevent extends Qevent {
	
	private final Location location;
	
	public TeleportQevent(final Location loc) {
		location = loc;
	}
	
	@Override
	public String info() {
		return Util.displayLocation(location);
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		Location loc = player.getLocation().clone();
		loc.setY(loc.getY() + 1);
		player.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 0);
		player.teleport(location, TeleportCause.PLUGIN);
		loc = location.clone();
		loc.setY(loc.getY() + 1);
		player.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 1);
	}
	
	@QCommand(min = 1, max = 1, usage = "{<location>}")
	public static Qevent fromCommand(final QCommandContext context) throws QCommandException {
		final Location loc =
				Util.getLoc(context.getPlayer(), context.getString(0), context.getSenderLang());
		if(loc == null) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_LOC_INVALID);
		}
		return new TeleportQevent(loc);
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("location", Util.serializeLocString(location));
		
	}
	
	protected static Qevent load(final StorageKey key) {
		final Location loc = Util.deserializeLocString(key.getString("location", ""));
		if(loc == null) {
			return null;
		}
		
		return new TeleportQevent(loc);
	}
}
