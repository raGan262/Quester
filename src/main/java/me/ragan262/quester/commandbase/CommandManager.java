package me.ragan262.quester.commandbase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.commandbase.exceptions.QPermissionException;
import me.ragan262.quester.commandbase.exceptions.QUsageException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.lang.LanguageManager;
import me.ragan262.quester.utils.Util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandManager {
	
	Logger logger = null;
	LanguageManager langMan = null;
	
	String displayedCommand = "";
	String helpCommand = "help";
	Object[] arguments = null;
	Class<?>[] classes = null;
	
	private final Map<Method, Map<String, Method>> labels =
			new HashMap<Method, Map<String, Method>>();
	private final Map<Method, Map<String, Method>> aliases =
			new HashMap<Method, Map<String, Method>>();
	private final Map<Method, Object> instances = new HashMap<Method, Object>();
	private final Map<Method, QCommand> annotations = new HashMap<Method, QCommand>();
	
	public CommandManager(final LanguageManager langMan, final Logger logger, final String displayedCommand, final Object... arguments) {
		this.logger = logger;
		this.langMan = langMan;
		this.displayedCommand = displayedCommand;
		this.arguments = arguments;
		classes = new Class<?>[arguments.length];
		for(int i = 0; i < arguments.length; i++) {
			classes[i] = arguments[i].getClass();
		}
	}
	
	public void setHelpCommand(final String helpCommand) {
		if(helpCommand != null) {
			this.helpCommand = helpCommand;
		}
	}
	
	public void register(final Class<?> clss) {
		registerMethods(null, clss);
	}
	
	private void registerMethods(final Method parent, final Class<?> clss) {
		final Object instance = construct(clss);
		for(final Method method : clss.getMethods()) {
			
			if(!method.isAnnotationPresent(QCommand.class)
					|| !method.isAnnotationPresent(QCommandLabels.class)) {
				continue;
			}
			
			if(instance == null && !Modifier.isStatic(method.getModifiers())) {
				logger.warning("Failed to register command:" + "" + method.getName() + "() in "
						+ clss.getCanonicalName());
				continue;
			}
			else {
				instances.put(method, instance);
			}
			
			final QCommand qCmd = method.getAnnotation(QCommand.class);
			annotations.put(method, qCmd);
			
			if(labels.get(parent) == null) {
				labels.put(parent, new HashMap<String, Method>());
			}
			if(aliases.get(parent) == null) {
				aliases.put(parent, new HashMap<String, Method>());
			}
			
			final Map<String, Method> lblMap = labels.get(parent);
			final Map<String, Method> aliMap = aliases.get(parent);
			
			final QCommandLabels qCmdLbls = method.getAnnotation(QCommandLabels.class);
			final String[] aliases = qCmdLbls.value();
			lblMap.put(aliases[0].toLowerCase(), method);
			for(int i = 1; i < aliases.length; i++) {
				aliMap.put(aliases[i].toLowerCase(), method);
			}
			
			if(method.isAnnotationPresent(QNestedCommand.class)) {
				for(final Class<?> iCls : method.getAnnotation(QNestedCommand.class).value()) {
					registerMethods(method, iCls);
				}
			}
			
		}
	}
	
	public void execute(final String[] args, final CommandSender sender) throws QCommandException, QuesterException {
		executeMethod(args, sender, null, 0);
	}
	
	private void executeMethod(final String[] args, final CommandSender sender, final Method parent, int level) throws QCommandException, QuesterException {
		
		if(args.length <= level) {
			throw new QUsageException("Not enough argmunents.", getUsage(args, level, parent));
		}
		final String label = args[level].toLowerCase();
		
		boolean execute = false;
		if(parent != null) {
			execute = annotations.get(parent).forceExecute();
		}
		
		Method method = labels.get(parent).get(label);
		if(method == null) {
			method = aliases.get(parent).get(label);
		}
		if(method == null) {
			if(execute) {
				method = parent;
				level--;
			}
			else {
				throw new QUsageException("Unknown argument: " + label, getUsage(args, level - 1,
						parent));
			}
		}
		
		// check every permission for nested command
		final QCommand cmd = annotations.get(method);
		if(sender == null || !Util.permCheck(sender, cmd.permission(), false, null)) {
			throw new QPermissionException();
		}
		
		if(method != parent && labels.get(method) != null) { // going deeper
			final int numArgs = args.length - level - 1;
			if(numArgs < 1) {
				throw new QUsageException("Not enough argmunents.", getUsage(args, level, method));
			}
			
			executeMethod(args, sender, method, level + 1);
		}
		else {
			
			final String[] parentArgs = new String[level + 1];
			final String[] realArgs = new String[args.length - level - 1];
			System.arraycopy(args, 0, parentArgs, 0, level + 1);
			System.arraycopy(args, level + 1, realArgs, 0, args.length - level - 1);
			
			final QCommandContext context =
					new QCommandContext(realArgs, parentArgs, sender, this,
							langMan.getPlayerLang(sender.getName()));
			
			if(context.length() < cmd.min()) {
				throw new QUsageException("Not enough argmunents.", getUsage(args, level, method));
			}
			
			if(!(cmd.max() < 0) && context.length() > cmd.max()) {
				throw new QUsageException("Too many argmunents.", getUsage(args, level, method));
			}
			
			invoke(method, context, sender);
		}
	}
	
	private void invoke(final Method method, final Object... methodArgs) throws QCommandException, QuesterException, NumberFormatException {
		Exception ex = null;
		try {
			method.invoke(instances.get(method), methodArgs);
		}
		catch (final IllegalAccessException e) {
			ex = e;
		}
		catch (final IllegalArgumentException e) {
			ex = e;
		}
		catch (final InvocationTargetException e) {
			if(e.getCause() instanceof QCommandException) {
				throw (QCommandException) e.getCause();
			}
			else if(e.getCause() instanceof QuesterException) {
				throw (QuesterException) e.getCause();
			}
			else if(e.getCause() instanceof IllegalArgumentException) {
				throw (IllegalArgumentException) e.getCause();
			}
			else {
				ex = e;
			}
		}
		if(ex != null) {
			logger.log(Level.SEVERE, "Failed to execute command.", ex);
		}
	}
	
	public Map<String, List<String>> getHelp(final String[] args, final CommandSender sender, final boolean deep) {
		final Map<String, List<String>> result = new HashMap<String, List<String>>();
		Method m = null;
		for(final String s : args) {
			if(labels.get(m).containsKey(s)) {
				m = labels.get(m).get(s);
			}
			else if(aliases.get(m).containsKey(s)) {
				m = aliases.get(m).get(s);
			}
			else {
				throw new IllegalArgumentException(s);
			}
			if(!Util.permCheck(sender, annotations.get(m).permission(), false, null)) {
				return result;
			}
		}
		addHelpToMap(sender, m, args, result, deep);
		return result;
	}
	
	private void addHelpToMap(final CommandSender sender, final Method method, final String[] arguments, final Map<String, List<String>> resultMap, final boolean deep) {
		QCommand command = null;
		final Map<String, Method> lbls = labels.get(method);
		if(lbls != null) {
			for(final String label : lbls.keySet()) {
				final Method innerMethod = lbls.get(label);
				command = annotations.get(innerMethod);
				if(command != null && Util.permCheck(sender, command.permission(), false, null)) {
					if(deep) {
						final String[] newArguments = new String[arguments.length + 1];
						for(int i = 0; i < arguments.length; i++) {
							newArguments[i] = arguments[i];
						}
						newArguments[arguments.length] = label;
						addHelpToMap(sender, innerMethod, newArguments, resultMap, deep);
					}
					else {
						if(resultMap.get(command.section()) == null) {
							resultMap.put(command.section(), new ArrayList<String>());
						}
						final StringBuilder cmdString = new StringBuilder();
						final String usage = command.usage();
						cmdString.append(displayedCommand);
						if(arguments.length > 0) {
							cmdString.append(' ').append(Util.implode(arguments));
						}
						cmdString.append(' ').append(label);
						if(!usage.isEmpty()) {
							cmdString.append(' ').append(ChatColor.GOLD).append(usage);
						}
						cmdString.append(ChatColor.GRAY).append(" - ").append(command.desc());
						resultMap.get(command.section()).add(cmdString.toString());
					}
				}
			}
		}
		else {
			if(method != null) {
				command = annotations.get(method);
				if(command != null && Util.permCheck(sender, command.permission(), false, null)) {
					if(resultMap.get(command.section()) == null) {
						resultMap.put(command.section(), new ArrayList<String>());
					}
					final StringBuilder cmdString = new StringBuilder();
					final String usage = command.usage();
					cmdString.append(displayedCommand);
					if(arguments.length > 0) {
						cmdString.append(' ').append(Util.implode(arguments));
					}
					if(!usage.isEmpty()) {
						cmdString.append(' ').append(ChatColor.GOLD).append(usage);
					}
					cmdString.append(ChatColor.GRAY).append(" - ").append(command.desc());
					resultMap.get(command.section()).add(cmdString.toString());
				}
			}
		}
	}
	
	public String getUsage(final String[] args, final int level, final Method method) {
		
		final StringBuilder usage = new StringBuilder();
		
		usage.append(displayedCommand);
		
		if(method != null) {
			for(int i = 0; i <= level; i++) {
				usage.append(' ').append(args[i]);
			}
			final Map<String, Method> lbls = labels.get(method);
			if(lbls == null) {
				usage.append(' ').append(annotations.get(method).usage());
			}
			else {
				boolean first = true;
				usage.append(" <");
				for(final String key : lbls.keySet()) {
					if(first) {
						first = false;
					}
					else {
						usage.append('|');
					}
					usage.append(key);
				}
				usage.append(">");
			}
		}
		else {
			usage.append(' ').append(helpCommand);
		}
		
		return usage.toString();
	}
	
	public String getUsage(final String[] args) {
		final StringBuilder usage = new StringBuilder();
		usage.append(displayedCommand);
		
		Method method = null;
		Method oldMethod = null;
		
		for(final String arg : args) {
			final String lcArg = arg.toLowerCase();
			method = labels.get(oldMethod).get(lcArg);
			if(method == null) {
				method = aliases.get(oldMethod).get(lcArg);
			}
			if(method != null) {
				usage.append(' ').append(lcArg);
				oldMethod = method;
			}
			else {
				break;
			}
		}
		if(labels.get(oldMethod) == null) {
			usage.append(' ').append(annotations.get(oldMethod).usage());
		}
		else {
			final Map<String, Method> lbls = labels.get(oldMethod);
			boolean first = true;
			usage.append(" <");
			for(final String key : lbls.keySet()) {
				if(first) {
					first = false;
				}
				else {
					usage.append('|');
				}
				usage.append(key);
			}
			usage.append(">");
		}
		return usage.toString();
	}
	
	private Object construct(final Class<?> clss) {
		Exception ex = null;
		try {
			final Constructor<?> constr = clss.getConstructor(classes);
			constr.setAccessible(true);
			return constr.newInstance(arguments);
		}
		catch (final NoSuchMethodException e) {
			ex = e;
		}
		catch (final SecurityException e) {
			ex = e;
		}
		catch (final InstantiationException e) {
			ex = e;
		}
		catch (final IllegalAccessException e) {
			ex = e;
		}
		catch (final IllegalArgumentException e) {
			ex = e;
		}
		catch (final InvocationTargetException e) {
			ex = e;
		}
		if(ex != null) {
			logger.log(Level.SEVERE,
					"Instantiating class '" + clss.getCanonicalName() + " failed.", ex);
		}
		return null;
	}
	
}
