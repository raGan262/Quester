package com.gmail.molnardad.quester.commandbase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.LanguageManager;
import com.gmail.molnardad.quester.QConfiguration;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.commandbase.exceptions.QPermissionException;
import com.gmail.molnardad.quester.commandbase.exceptions.QUsageException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public class CommandManager {

	Logger logger = null;
	LanguageManager langMan = null;
	
	String displayedCommand = "";
	String helpCommand = "help";
	Object[] arguments = null;
	Class<?>[] classes = null;
	
	private Map<Method, Map<String, Method>> labels = new HashMap<Method, Map<String, Method>>();
	private Map<Method, Map<String, Method>> aliases = new HashMap<Method, Map<String, Method>>();
	private Map<Method, Object> instances = new HashMap<Method, Object>();
	private Map<Method, QCommand> annotations = new HashMap<Method, QCommand>();
	
	public CommandManager(LanguageManager langMan, Logger logger, String displayedCommand, Object... arguments) {
		this.logger = logger;
		this.langMan = langMan;
		this.displayedCommand = displayedCommand;
		this.arguments = arguments;
		classes = new Class<?>[arguments.length];
		for(int i=0; i<arguments.length; i++) {
			classes[i] = arguments[i].getClass();
		}
	}
	
	public void setHelpCommand(String helpCommand) {
		if(helpCommand != null) {
			this.helpCommand = helpCommand;
		}
	}
	
	public void register(Class<?> clss) {
		registerMethods(null, clss);
	}
	
	private void registerMethods(Method parent, Class<?> clss) {
		Object instance = construct(clss);
		for(Method method : clss.getMethods()) {
			
			if(!method.isAnnotationPresent(QCommand.class) || !method.isAnnotationPresent(QCommandLabels.class)) {
				continue;
			}
			
			if(instance == null && !Modifier.isStatic(method.getModifiers())) {
				logger.warning("Failed to register command:" + "" + method.getName() + "() in " + clss.getCanonicalName());
				continue;
			}
			else {
				instances.put(method, instance);
			}
			
			QCommand qCmd = method.getAnnotation(QCommand.class);
			annotations.put(method, qCmd);
			
			if(labels.get(parent) == null) {
				labels.put(parent, new HashMap<String, Method>());
			}
			if(aliases.get(parent) == null) {
				aliases.put(parent, new HashMap<String, Method>());
			}
			
			Map<String, Method> lblMap = labels.get(parent);
			Map<String, Method> aliMap = aliases.get(parent);
			
			QCommandLabels qCmdLbls = method.getAnnotation(QCommandLabels.class);
			String[] aliases = qCmdLbls.value();
			lblMap.put(aliases[0].toLowerCase(), method);
			for(int i = 1; i < aliases.length; i++) {
				aliMap.put(aliases[i].toLowerCase(), method);
			}
			
			if(method.isAnnotationPresent(QNestedCommand.class)) {
				for(Class<?> iCls : method.getAnnotation(QNestedCommand.class).value()) {
					registerMethods(method, iCls);
				}
			}
			
		}
	}
	
	public void execute(String[] args, CommandSender sender) throws QCommandException, QuesterException {
		executeMethod(args, sender, null, 0);
	}
	
	private void executeMethod(String[] args, CommandSender sender, Method parent, int level) throws QCommandException, QuesterException{
		
		if(args.length <= level) {
			throw new QUsageException("Not enough argmunents.", getUsage(args, level, parent));
		}
		String label = args[level].toLowerCase();
		
		boolean execute = false;
		if(parent != null) {
			execute = annotations.get(parent).forceExecute();
		}
		
		Method method = labels.get(parent).get(label);
		if(method == null) {
			method = aliases.get(parent).get(label);
		}
		if(method == null) {
			if(execute){
				method = parent;
				level--;
			}
			else {
				throw new QUsageException("Unknown argument: " + label, getUsage(args, level-1, parent));
			}
		}

		// check every permission for nested command
		QCommand cmd = annotations.get(method);
		if(sender == null || !Util.permCheck(sender, cmd.permission(), false, null)) {
			throw new QPermissionException();
		}
		
		if(method != parent && labels.get(method) != null) { // going deeper
			int numArgs = args.length - level - 1;
			if(numArgs < 1) {
				throw new QUsageException("Not enough argmunents.", getUsage(args, level, method));
			}

			executeMethod(args, sender, method, level+1);
		}
		else {
			
			
			String[] parentArgs = new String[level+1];
			String[] realArgs = new String[args.length - level - 1];
			System.arraycopy(args, 0, parentArgs, 0, level+1);
			System.arraycopy(args, level+1, realArgs, 0, args.length - level - 1);
			
			QCommandContext context = new QCommandContext(realArgs, parentArgs, sender, this, langMan.getPlayerLang(sender.getName()));
			
			if(context.length() < cmd.min()) {
				throw new QUsageException("Not enough argmunents.", getUsage(args, level, method));
			}
			
			if(!(cmd.max() < 0) && context.length() > cmd.max()) {
				throw new QUsageException("Too many argmunents.", getUsage(args, level, method));
			}
			
			invoke(method, context, sender);
		}
	}
	
	private void invoke(Method method, Object... methodArgs) throws QCommandException, QuesterException, NumberFormatException {
		Exception ex = null;
		try {
			method.invoke(instances.get(method), methodArgs);
		} catch (IllegalAccessException e) {
			ex = e;
		} catch (IllegalArgumentException e) {
			ex = e;
		} catch (InvocationTargetException e) {
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
			logger.warning("Failed to execute command.");
			if(QConfiguration.debug) {
				ex.printStackTrace();
			}
		}
	}
	
	public Map<String, List<String>> getHelp(String[] args, CommandSender sender) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		Method m = null;
		for(String s : args) { 
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
		QCommand anno = null;
		Map<String, Method> lbls = labels.get(m);
		if(lbls != null) {
			for(String s : lbls.keySet()) {
				Method innerMethod = lbls.get(s);
				anno = annotations.get(innerMethod);
				if(anno != null && Util.permCheck(sender, anno.permission(), false, null)) {
					if(result.get(anno.section()) == null) {
						result.put(anno.section(), new ArrayList<String>());
					}
					StringBuilder cmd = new StringBuilder();
					String usage = anno.usage();
					cmd.append(displayedCommand);
					if(args.length > 0) {
						cmd.append(' ').append(Util.implode(args));
					}
					cmd.append(' ').append(s);
					if(!usage.isEmpty()) {
						cmd.append(' ').append(ChatColor.GOLD).append(usage);
					}
					cmd.append(ChatColor.GRAY).append(" - ").append(anno.desc());
					result.get(anno.section()).add(cmd.toString());
				}
			}
		}
		else {
			if(m != null) {
				anno = annotations.get(m);
				if(anno != null && Util.permCheck(sender, anno.permission(), false, null)) {
					if(result.get(anno.section()) == null) {
						result.put(anno.section(), new ArrayList<String>());
					}
					StringBuilder cmd = new StringBuilder();
					String usage = anno.usage();
					cmd.append(displayedCommand);
					if(args.length > 0) {
						cmd.append(' ').append(Util.implode(args));
					}
					if(!usage.isEmpty()) {
						cmd.append(' ').append(ChatColor.GOLD).append(usage);
					}
					cmd.append(ChatColor.GRAY).append(" - ").append(anno.desc());
					result.get(anno.section()).add(cmd.toString());
				}
			}
		}
		return result;
	}
	
	public String getUsage(String[] args, int level, Method method) {
		
		StringBuilder usage = new StringBuilder();
		
		usage.append(displayedCommand);
		
		if(method != null) {
			for(int i = 0; i <= level; i++) {
				usage.append(' ').append(args[i]);
			}
			Map<String, Method> lbls = labels.get(method);
			if(lbls == null) {
				usage.append(' ').append(annotations.get(method).usage());
			}
			else {
				boolean first = true;
				usage.append(" <");
				for(String key : lbls.keySet()) {
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
	
	public String getUsage(String[] args) {
		StringBuilder usage = new StringBuilder();
		usage.append(displayedCommand);
		
		Method method = null;
		Method oldMethod = null;
		
		for(String arg : args) {
			String lcArg = arg.toLowerCase();
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
			Map<String, Method> lbls = labels.get(oldMethod);
			boolean first = true;
			usage.append(" <");
			for(String key : lbls.keySet()) {
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
	
	private Object construct(Class<?> clss) {
		Exception ex = null;
		try {
			Constructor<?> constr = clss.getConstructor(classes);
			constr.setAccessible(true);
			return constr.newInstance(arguments);
		} catch (NoSuchMethodException e) {
			ex = e;
		} catch (SecurityException e) {
			ex = e;
		} catch (InstantiationException e) {
			ex = e;
		} catch (IllegalAccessException e) {
			ex = e;
		} catch (IllegalArgumentException e) {
			ex = e;
		} catch (InvocationTargetException e) {
			ex = e;
		}
		if(ex != null) {
			if(QConfiguration.debug) {
				logger.info("Instantiating class '" + clss.getCanonicalName() + " failed.");
				ex.printStackTrace();
			}
		}
		return null;
	}
	
}
