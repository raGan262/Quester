package com.gmail.molnardad.quester.qevents;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class CommandQevent extends Qevent {

	public static final String TYPE = "COMMAND";
	private final String command;
	
	public CommandQevent(int occ, int del, String cmd) {
		super(occ, del);
		this.command = cmd;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public int getOccasion() {
		return occasion;
	}
	
	@Override
	public String toString() {
		return TYPE + ": CMD: /" + command;
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("command", command);
	}
	
	public static CommandQevent deser(int occ, int del, ConfigurationSection section) {
		String cmd;
		
		if(section.isString("command"))
			cmd = section.getString("command");
		else
			return null;
		
		return new CommandQevent(occ, del, cmd);
	}

	@Override
	public void run(Player player) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%p", player.getName()));
	}
}
