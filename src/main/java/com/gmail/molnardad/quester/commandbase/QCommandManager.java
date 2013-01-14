package com.gmail.molnardad.quester.commandbase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;

import com.gmail.molnardad.quester.QuestData;
import com.gmail.molnardad.quester.Quester;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.commandbase.exceptions.QUsageException;

public class QCommandManager {

	Logger logger = null;
	
	private Map<Method, Map<String, Method>> labels = new HashMap<Method, Map<String, Method>>();
	private Map<Method, Object> instances = new HashMap<Method, Object>();
	private Map<Method, QCommand> annotations = new HashMap<Method, QCommand>();
	
	public QCommandManager() {
		logger = Quester.log;
	}
	
	public void register(Class<?> clss) {
		registerMethods(null, clss);
	}
	
	private void registerMethods(Method parent, Class<?> clss) {
		Object instance = construct(clss);
		for(Method method : clss.getMethods()) {
			
			if(!method.isAnnotationPresent(QCommand.class)) {
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
			
			Map<String, Method> lblmap = labels.get(parent);
			for(String label : qCmd.labels()) {
				lblmap.put(label.toLowerCase(), method);
			}
			
			if(method.isAnnotationPresent(QNestedCommand.class)) {
				for(Class<?> iCls : method.getAnnotation(QNestedCommand.class).value()) {
					registerMethods(method, iCls);
				}
			}
			
		}
	}
	
	public void execute(String[] args, CommandSender sender) throws QCommandException {
		executeMethod(args, sender, null, 0);
	}
	
	private void executeMethod(String[] args, CommandSender sender, Method parent, int level) throws QCommandException{
		
		if(args.length <= level) {
			throw new QUsageException("Not enough argmunents.", getUsage(args, level, parent));
		}
		String label = args[level].toLowerCase();
		
		Method method = labels.get(parent).get(label);
		if(method == null) {
			throw new QUsageException("Unknown argument: " + label, getUsage(args, level, parent));
		}
		
		int numArgs = args.length - level - 1;
		
		if(labels.get(method) != null) { // going deeper
			if(numArgs < 1) {
				throw new QUsageException("Not enough argmunents.", getUsage(args, level, method));
			}

			executeMethod(args, sender, method, level+1);
		}
		else {
			
			QCommand cmd = annotations.get(method);
			String[] parentArgs = new String[level+1];
			String[] realArgs = new String[args.length - level - 1];
			System.arraycopy(args, 0, parentArgs, 0, level+1);
			System.arraycopy(args, level+1, realArgs, 0, args.length - level - 1);
			
			QCommandContext context = new QCommandContext(realArgs, parentArgs, sender);
			
			if(realArgs.length < cmd.min()) {
				throw new QUsageException("Not enough argmunents.", getUsage(args, level, method));
			}
			
			if(realArgs.length > cmd.max()) {
				throw new QUsageException("Too many argmunents.", getUsage(args, level, method));
			}
			
			invoke(method, context, sender);
		}
	}
	
	private void invoke(Method method, Object... methodArgs) {
		Exception ex = null;
		try {
			method.invoke(instances.get(method), methodArgs);
		} catch (IllegalAccessException e) {
			ex = e;
		} catch (IllegalArgumentException e) {
			ex = e;
		} catch (InvocationTargetException e) {
			ex = e;
		}
		if(ex != null) {
			logger.warning("Failed to execute command.");
			if(QuestData.debug) {
				ex.printStackTrace();
			}
		}
	}
	
	public String getUsage(String[] args, int level, Method method) {
		
		StringBuilder usage = new StringBuilder();
		
		usage.append(QuestData.displayedCmd);
		
		if(method != null) {
			for(int i = 0; i <= level; i++) {
				usage.append(' ').append(args[i]);
			}
			usage.append(' ').append(annotations.get(method).usage());
		}
		else {
			usage.append(" help");
		}
		
		return usage.toString();
	}
	
	private Object construct(Class<?> clss) {
		Exception ex = null;
		try {
			Constructor<?> constr = clss.getConstructor(Quester.class);
			constr.setAccessible(true);
			return constr.newInstance(Quester.plugin);
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
			if(QuestData.debug) {
				logger.info("Instantiating class '" + clss.getCanonicalName() + " failed.");
				ex.printStackTrace();
			}
		}
		return null;
	}
	
}
