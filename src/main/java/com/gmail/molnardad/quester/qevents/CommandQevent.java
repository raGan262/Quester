package com.gmail.molnardad.quester.qevents;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.storage.StorageKey;
import com.gmail.molnardad.quester.utils.Util;

@QElement("CMD")
public final class CommandQevent extends Qevent {
	
	private final String command;
	
	public CommandQevent(final String cmd) {
		command = cmd;
	}
	
	@Override
	public String info() {
		return "/" + command;
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%p", player.getName()));
	}
	
	@QCommand(min = 1, usage = "<command>")
	public static Qevent fromCommand(final QCommandContext context) {
		return new CommandQevent(Util.implode(context.getArgs()));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("command", command);
	}
	
	protected static Qevent load(final StorageKey key) {
		String cmd;
		
		cmd = key.getString("command");
		if(cmd == null) {
			return null;
		}
		
		return new CommandQevent(cmd);
	}
}
