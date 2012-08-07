package com.gmail.molnardad.quester.qevents;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

@SerializableAs("QuesterMessageQevent")
public final class MessageQevent extends Qevent {

	private final String TYPE = "MESSAGE";
	private final String message;
	private final String rawmessage;
	private final int occasion;
	
	public MessageQevent(int occ, String msg) {
		this.rawmessage = msg;
		this.message = ChatColor.translateAlternateColorCodes('&', rawmessage).replaceAll("\\\\n", "\n");
		this.occasion = occ;
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
	public boolean execute(Player player) {
		player.sendMessage(message);
		return true;
	}
	
	@Override
	public String toString() {
		return TYPE + ": ON-" + parseOccasion(occasion) + "; MSG: " + message;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("message", rawmessage);
		map.put("occasion", occasion);
		
		return map;
	}
	
	public static MessageQevent deserialize(Map<String, Object> map) {
		String msg;
		int occ;
		
		msg = (String) map.get("message");
		occ = (Integer) map.get("occasion");
		
		return new MessageQevent(occ, msg);
	}
}
