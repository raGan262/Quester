package me.ragan262.quester.commandbase;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.ragan262.quester.lang.QuesterLang;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

public class QCommandContext {
	
	private final CommandManager comMan;
	private final String[] args;
	private final String[] parentArgs;
	private final CommandSender sender;
	private final Set<Character> flags;
	private final QuesterLang lang;
	
	//	map valueFlags will be added once it is needed :)
	//	private final Map<String, String> valueFlags;
	
	private QCommandContext(final String[] args, final String[] parentArgs, final CommandSender sender, final CommandManager cMan, final Set<Character> flags, final QuesterLang lang) {
		this.args = args;
		this.parentArgs = parentArgs;
		comMan = cMan;
		this.sender = sender;
		this.flags = flags;
		this.lang = lang;
	}
	
	public QCommandContext(final String[] args, final String[] parentArgs, final CommandSender sender, final CommandManager cMan, final QuesterLang lang) {
		this.sender = sender;
		this.parentArgs = parentArgs;
		comMan = cMan;
		this.lang = lang;
		flags = new HashSet<Character>();
		
		int i = 0;
		for(; i < args.length; i++) {
			args[i] = args[i].trim();
			if(args[i].length() == 0) {
				continue;
			}
			else if(args[i].charAt(0) == '\'' || args[i].charAt(0) == '"') {
				final char quote = args[i].charAt(0);
				if(args[i].charAt(args[i].length() - 1) == quote && args[i].length() > 1) {
					args[i] = args[i].substring(1, args[i].length() - 1);
					continue;
				}
				String quoted = args[i].substring(1);
				for(int inner = i + 1; inner < args.length; inner++) {
					if(args[inner].isEmpty()) {
						continue;
					}
					final String test = args[inner].trim();
					quoted += " " + test;
					if(test.charAt(test.length() - 1) == quote) {
						args[i] = quoted.substring(0, quoted.length() - 1);
						for(int j = i + 1; j <= inner; ++j) {
							args[j] = "";
						}
						break;
					}
				}
			}
		}
		for(i = 0; i < args.length; ++i) {
			if(args[i].length() == 0) {
				continue;
			}
			if(args[i].charAt(0) == '-' && args[i].matches("^-[a-zA-Z]+$")) {
				for(int k = 1; k < args[i].length(); k++) {
					flags.add(args[i].charAt(k));
				}
				args[i] = "";
			}
		}
		final List<String> copied = Lists.newArrayList();
		for(String arg : args) {
			arg = arg.trim();
			if(arg == null || arg.isEmpty()) {
				continue;
			}
			copied.add(arg.trim());
		}
		this.args = copied.toArray(new String[copied.size()]);
	}
	
	public QCommandContext getSubContext(final int level) {
		if(args.length > level - 1) {
			final int parLength = parentArgs.length;
			final int argLength = args.length - level;
			final String[] parentArgs = new String[parLength + level];
			final String[] args = new String[argLength];
			System.arraycopy(this.parentArgs, 0, parentArgs, 0, parLength);
			for(int i = 0; i < level; i++) {
				parentArgs[parLength + i] = this.args[i];
			}
			System.arraycopy(this.args, level, args, 0, argLength);
			
			return new QCommandContext(args, parentArgs, sender, comMan, flags, lang);
		}
		return null;
	}
	
	public int length() {
		return args.length;
	}
	
	public CommandSender getSender() {
		return sender;
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
	
	public QuesterLang getSenderLang() {
		return lang;
	}
	
	public Player getPlayer() {
		if(sender instanceof Player) {
			return (Player) sender;
		}
		return null;
	}
	
	public String getString(final int i) {
		return args[i];
	}
	
	public String getString(final int i, final String def) {
		return i < 0 || i >= args.length ? def : args[i];
	}
	
	public int getInt(final int i) throws NumberFormatException {
		return Integer.parseInt(args[i]);
	}
	
	public int getInt(final int i, final int def) {
		try {
			return Integer.parseInt(args[i]);
		}
		catch (final Exception e) {
			return def;
		}
	}
	
	public double getDouble(final int i) throws NumberFormatException {
		return Double.parseDouble(args[i]);
	}
	
	public double getDouble(final int i, final double def) {
		try {
			return Double.parseDouble(args[i]);
		}
		catch (final Exception e) {
			return def;
		}
	}
	
	public boolean hasFlag(final char character) {
		return flags.contains(character);
	}
	
	public String[] getArgs() {
		return args;
	}
	
	public String[] getParentArgs() {
		return parentArgs;
	}
	
	public String getParentArg(final int i) {
		return parentArgs[i];
	}
	
	public String[] getAllArgs() {
		final String[] result = new String[args.length + parentArgs.length];
		System.arraycopy(parentArgs, 0, result, 0, parentArgs.length);
		System.arraycopy(args, 0, result, parentArgs.length, args.length);
		return result;
	}
	
	public String getUsage() {
		return comMan.getUsage(parentArgs);
	}
}
