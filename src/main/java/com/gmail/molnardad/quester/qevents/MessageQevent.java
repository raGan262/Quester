package com.gmail.molnardad.quester.qevents;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.QElement;
import com.gmail.molnardad.quester.elements.Qevent;
import com.gmail.molnardad.quester.lang.LanguageManager;
import com.gmail.molnardad.quester.storage.StorageKey;

@QElement("MSG")
public final class MessageQevent extends Qevent {
	
	private final String message;
	private final String rawmessage;
	private final boolean isCustomMessage;
	
	public MessageQevent(final String msg) {
		final String custom = LanguageManager.getCustomMessageKey(msg);
		rawmessage = msg;
		if(custom != null) {
			isCustomMessage = true;
			message = custom;
		}
		else {
			isCustomMessage = false;
			message = ChatColor.translateAlternateColorCodes('&', msg).replaceAll("\\\\n", "\n");
		}
	}
	
	@Override
	public String info() {
		return message;
	}
	
	@Override
	protected void run(final Player player, final Quester plugin) {
		if(isCustomMessage) {
			final String msg =
					plugin.getLanguageManager().getPlayerLang(player.getName()).getCustom(message);
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg)
					.replaceAll("\\\\n", "\n").replace("%p", player.getName()));
		}
		else {
			player.sendMessage(message.replace("%p", player.getName()));
		}
	}
	
	@QCommand(min = 1, max = 1, usage = "<message>")
	public static Qevent fromCommand(final QCommandContext context) {
		return new MessageQevent(context.getString(0));
	}
	
	@Override
	protected void save(final StorageKey key) {
		key.setString("message", rawmessage);
	}
	
	protected static Qevent load(final StorageKey key) {
		final String msg = key.getString("message");
		if(msg == null) {
			return null;
		}
		
		return new MessageQevent(msg);
	}
}
