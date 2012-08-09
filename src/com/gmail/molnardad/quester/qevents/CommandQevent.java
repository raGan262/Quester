package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterCommandQevent")
public final class CommandQevent extends Qevent {

	private final String TYPE = "CMD";
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
		return TYPE + ": ON-" + parseOccasion(occasion) + "; - /" + command;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("command", command);
		map.put("occasion", occasion);
		map.put("delay", delay);
		
		return map;
	}
	
	public static CommandQevent deserialize(Map<String, Object> map) {
		String cmd;
		int occ, del;
		
		cmd = (String) map.get("command");
		occ = (Integer) map.get("occasion");
		del = (Integer) map.get("delay");
		
		return new CommandQevent(occ, del, cmd);
	}

	@Override
	public void run(Player player) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%p", player.getName()));
	}
}
