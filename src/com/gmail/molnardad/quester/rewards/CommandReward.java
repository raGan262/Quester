package com.gmail.molnardad.quester.rewards;

import java.util.HashMap;
import java.util.Map;


import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterCommandReward")
public final class CommandReward implements Reward {

	private final String TYPE = "COMMAND";
	private final String command;
	
	public CommandReward(String cmd) {
		this.command = cmd;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean giveReward(Player player) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%p", player.getName()));
		return true;
	}

	@Override
	public boolean checkReward(Player player) {
		return true;
	}
	
	@Override
	public String toString() {
		return TYPE+": /" + command;
	}
	
	public String checkErrorMessage(){
		return "Command reward check error message.";
	}
	
	public String giveErrorMessage() {
		return "Command reward give error message.";
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("command", command);
		
		return map;
	}

	public static CommandReward deserialize(Map<String, Object> map) {
		String cmd;
		
		try {
			cmd = (String) map.get("command");
			return new CommandReward(cmd);
		} catch (Exception e) {
			return null;
		}
	}

}
