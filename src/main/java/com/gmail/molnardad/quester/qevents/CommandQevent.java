package com.gmail.molnardad.quester.qevents;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.utils.Util;

@QElement("CMD")
public final class CommandQevent extends Qevent {

	private final String command;
	
	public CommandQevent(String cmd) {
		this.command = cmd;
	}
	
	@Override
	public String info() {
		return "/" + command;
	}

	@Override
	protected void run(Player player) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%p", player.getName()));
	}
	
	@QCommand(
			min = 1,
			usage = "<command>")
	public static Qevent fromCommand(QCommandContext context) {
		return new CommandQevent(Util.implode(context.getArgs()));
	}

	// TODO serialization
	public void serialize(ConfigurationSection section) {
		section.set("command", command);
	}
	
	public static CommandQevent deser(ConfigurationSection section) {
		String cmd;
		
		if(section.isString("command"))
			cmd = section.getString("command");
		else
			return null;
		
		return new CommandQevent(cmd);
	}
}
