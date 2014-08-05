package me.ragan262.quester.qevents;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.quester.Quester;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.elements.QElement;
import me.ragan262.quester.elements.Qevent;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
	
	@Command(min = 1, usage = "<command>")
	public static Qevent fromCommand(final QuesterCommandContext context) {
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
