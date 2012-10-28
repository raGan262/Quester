package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class MessageQevent extends Qevent {

	public static final String TYPE = "MSG";
	private final String message;
	private final String rawmessage;
	
	public MessageQevent(int occ, int del, String msg) {
		super(occ, del);
		this.rawmessage = msg;
		this.message = ChatColor.translateAlternateColorCodes('&', rawmessage).replaceAll("\\\\n", "\n");
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
		return TYPE + ": " + message + ChatColor.RESET + appendSuper();
	}

	@Override
	public void serialize(ConfigurationSection section) {
		super.serialize(section, TYPE);
		section.set("message", rawmessage);
	}
	
	public static MessageQevent deser(int occ, int del, ConfigurationSection section) {
		String msg;
		
		if(section.isString("message"))
			msg = ChatColor.translateAlternateColorCodes('&', section.getString("message")).replaceAll("\\\\n", "\n");
		else
			return null;
		
		return new MessageQevent(occ, del, msg);
	}

	@Override
	public void run(Player player) {
		player.sendMessage(message.replace("%p", player.getName()));
	}
}
