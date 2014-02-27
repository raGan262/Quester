package me.ragan262.quester.qevents;

import me.ragan262.quester.Quester;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.storage.StorageKey;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
