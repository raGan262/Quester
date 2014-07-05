package me.ragan262.quester.commandmanager;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import me.ragan262.commandmanager.exceptions.CommandExceptionHandler;
import me.ragan262.quester.exceptions.QuesterException;

public class QuesterCommandExceptionHandler implements CommandExceptionHandler {
	
	private final Logger logger;
	
	public QuesterCommandExceptionHandler(final Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public void handleException(final Exception e, final CommandSender sender) {
		if(e instanceof QuesterException) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}
		else {
			sender.sendMessage(ChatColor.RED
					+ "Error occured during execution of this command. See console for full report.");
			logger.log(Level.SEVERE, "Command execution error: (Sender: " + sender.getName() + ")",
					e);
		}
	}
	
}
