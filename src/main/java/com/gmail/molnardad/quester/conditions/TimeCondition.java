package com.gmail.molnardad.quester.conditions;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.elements.Condition;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("TIME")
public final class TimeCondition extends Condition {

	private final int from;
	private final int to;
	private final String world;
	
	public TimeCondition(int from, int to, String world) {
		this.from = from;
		this.to = to;
		this.world = world;
	}

	@Override
	protected String parseDescription(String description) {
		return description.replaceAll("%from", String.valueOf(from)).replaceAll("%to", String.valueOf(to));
	}
	
	@Override
	public boolean isMet(Player player, Quester plugin) {
		World w;
		if(world.isEmpty()) {
			w = player.getWorld();
		}
		else {
			w  = Bukkit.getWorld(world);
			if(w == null) {
				return false;
			}
		}
		
		if(from < to) {
			return (w.getTime() > from && w.getTime() < to);
		}
		else {
			return (w.getTime() > from || w.getTime() < to);
		}
	}
	
	@Override
	public String show() {
		String w = (world.isEmpty()) ? "" : " in world " + world;
		return "Time must be between " + from + " and " + to + " ticks" + w + ".";
	}
	
	@Override
	public String info() {
		return from + "-" + to + "; WORLD: " + (world.isEmpty() ? "PLAYER" : world);
	}
	
	@QCommand(
			min = 1,
			max = 2,
			usage = "<from-to> {[world]}")
	public static Condition fromCommand(QCommandContext context) throws QCommandException {
		int from;
		int to;
		String world = "";
		String[] ss = context.getString(0).split("-");
		if(ss.length != 2) {
			throw new QCommandException(context.getSenderLang().ERROR_CMD_ARG_CANT_PARSE.replaceAll("%arg", context.getString(0)));
		}
		from = Integer.parseInt(ss[0]);
		to = Integer.parseInt(ss[1]);
		if(from >= 0 && from < 25) {
			from = (from + 16) % 24;
			from *= 1000;
		}
		if(to >= 0 && to < 25) {
			to = (to + 16) % 24;
			to *= 1000;
		}
		if(context.length() > 1) {
			if(context.getString(1).equalsIgnoreCase(QConfiguration.worldLabelThis)) {
				if(context.getPlayer() == null) {
					throw new QCommandException(context.getSenderLang().ERROR_CMD_WORLD_THIS.replaceAll("%this", QConfiguration.worldLabelThis));
				}
				world = context.getPlayer().getWorld().getName();
			}
			else {
				World x = Bukkit.getServer().getWorld(context.getString(1));
				if(x == null) {
					throw new QCommandException(context.getSenderLang().ERROR_CMD_WORLD_INVALID);
				}
				world = x.getName();
			}
		}
		return new TimeCondition(from, to, world);
	}

	@Override
	protected void save(StorageKey key) {
		if(from > 0) {
			key.setInt("from", from);
		}
		if(to > 0) {
			key.setInt("to", to);
		}
		if(!world.isEmpty()) {
			key.setString("world", world);
		}
	}

	protected static Condition load(StorageKey key) {
		int from = key.getInt("from", 0);
		int to = key.getInt("to", 0);
		String world = "";
		World x = Bukkit.getServer().getWorld(key.getString("world", ""));
		if(x != null) {
			world = x.getName();
		}
		return new TimeCondition(from, to, world);
	}
}
