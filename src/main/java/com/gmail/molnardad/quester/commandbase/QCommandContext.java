package com.gmail.molnardad.quester.commandbase;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.molnardad.quester.managers.LanguageManager;
import com.gmail.molnardad.quester.managers.CommandManager;
import com.gmail.molnardad.quester.strings.QuesterStrings;
import com.google.common.collect.Lists;

public class QCommandContext {

	private final CommandManager comMan;
	private final String[] args;
	private final String[] parentArgs;
	private final CommandSender sender;
	private final Set<Character> flags;
	private final QuesterStrings lang;
	// valueFlags will be added once it is needed :)
	
	private QCommandContext(String[] args, String[] parentArgs, CommandSender sender, CommandManager cMan, Set<Character> flags, QuesterStrings lang) {
		this.args = args;
		this.parentArgs = args;
		this.comMan = cMan;
		this.sender = sender;
		this.flags = flags;
		this.lang = lang;
	}
	
	public QCommandContext(String[] args, String[] parentArgs, CommandSender sender, CommandManager cMan) {
		this.sender = sender;
		this.parentArgs = parentArgs;
		this.comMan = cMan;
		this.lang = LanguageManager.getInstance().getPlayerLang(sender.getName());
		this.flags = new HashSet<Character>();
		
		int i = 1;
		for(;i < args.length; i++) {
			args[i] = args[i].trim();
			if (args[i].length() == 0) {
				continue;
			} 
			else if (args[i].charAt(0) == '\'' || args[i].charAt(0) == '"') {
				char quote = args[i].charAt(0);
				if(args[i].charAt(args[i].length() - 1) == quote) {
					args[i] = args[i].substring(1, args[i].length() - 1);
					continue;
				}
				String quoted = args[i].substring(1);
				for (int inner = i + 1; inner < args.length; inner++) {
					if (args[inner].isEmpty()) {
						continue;
					}
					String test = args[inner].trim();
					quoted += " " + test;
					if (test.charAt(test.length() - 1) == quote) {
						args[i] = quoted.substring(0, quoted.length() - 1);
						for (int j = i + 1; j <= inner; ++j) {
							args[j] = "";
						}
						break;
					}
				}
			}
		}
		for (i = 1; i < args.length; ++i) {
			if (args[i].length() == 0) {
				continue;
			}
			if (args[i].charAt(0) == '-' && args[i].matches("^-[a-zA-Z]+$")) {
				for (int k = 1; k < args[i].length(); k++) {
					flags.add(args[i].charAt(k));
				}
				args[i] = "";
			}
		}
		List<String> copied = Lists.newArrayList();
		for (String arg : args) {
			arg = arg.trim();
			if (arg == null || arg.isEmpty()) {
				continue;
			}
			copied.add(arg.trim());
		}
		this.args = copied.toArray(new String[copied.size()]);
	}
	
	public QCommandContext getSubContext() {
		if(args.length > 0) {
			int parLength = this.parentArgs.length;
			int argLength = this.args.length - 1;
			String[] parentArgs = new String[parLength + 1];
			String[] args = new String[argLength];
			System.arraycopy(this.parentArgs, 0, parentArgs, 0, parLength);
			parentArgs[parLength] = this.args[0];
			System.arraycopy(this.args, 1, args, 0, argLength);
			
			return new QCommandContext(args, parentArgs, sender, comMan, flags, lang);
		}
		return null;
	}
	
	public int length() {
		return args.length;
	}
	
	public Location getSenderLocation() {
		if(sender instanceof BlockCommandSender) {
			((BlockCommandSender) sender).getBlock().getLocation();
		}
		else if(sender instanceof Player) {
			((Player) sender).getLocation();
		}
		return null;
	}
	
	public QuesterStrings getSenderLang() {
		return lang;
	}
	
	public Player getPlayer() {
		if(sender instanceof Player) {
			return (Player) sender;
		}
		return null;
	}
	
	public String getString(int i) {
		return args[i];
	}
	
	public String getString(int i, String def) {
		return (i<0 || i>=args.length ? args[i] : def);
	}
	
	public int getInt(int i) throws NumberFormatException {
		return Integer.parseInt(args[i]);
	}
	
	public int getInt(int i, int def) {
		try {
			return Integer.parseInt(args[i]);
		}
		catch (Exception e) {
			return def;
		}
	}
	
	public double getDouble(int i) throws NumberFormatException {
		return Double.parseDouble(args[i]);
	}
	
	public double getDouble(int i, double def) {
		try {
			return Double.parseDouble(args[i]);
		}
		catch (Exception e) {
			return def;
		}
	}
	
	public boolean hasFlag(char character) {
		return flags.contains(character);
	}
	
	public String[] getArgs() {
		return args;
	}
	
	public String[] getParentArgs() {
		return parentArgs;
	}
	
	public String[] getAllArgs() {
		String[] result = new String[args.length + parentArgs.length];
		System.arraycopy(parentArgs, 0, result, 0, parentArgs.length);
		System.arraycopy(args, 0, result, parentArgs.length, args.length);
		return result;
	}
	
	public String getUsage() {
		return comMan.getUsage(parentArgs);
	}
}
