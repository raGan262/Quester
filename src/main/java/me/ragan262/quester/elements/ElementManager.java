package me.ragan262.quester.elements;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import me.ragan262.commandmanager.annotations.Command;
import me.ragan262.commandmanager.exceptions.CommandException;
import me.ragan262.commandmanager.exceptions.UsageException;
import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.commandmanager.QuesterCommandContext;
import me.ragan262.quester.exceptions.ElementException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Ql;
import me.ragan262.quester.utils.Util;

import org.apache.commons.lang.Validate;

public class ElementManager {
	
	final class ElementInfo {
		private Class<? extends Element> clss;
		private String usage;
		private Method commandMethod;
		private Method loadMethod;
		private Command command;
	}
	
	private static ElementManager instance = null;
	
	private final Map<Class<? extends Element>, Map<String, ElementInfo>> elements =
			new IdentityHashMap<Class<? extends Element>, Map<String, ElementInfo>>();
	
	public ElementManager() {
		elements.put(Element.CONDITION, new HashMap<String, ElementInfo>());
		elements.put(Element.OBJECTIVE, new HashMap<String, ElementInfo>());
		elements.put(Element.QEVENT, new HashMap<String, ElementInfo>());
		elements.put(Element.TRIGGER, new HashMap<String, ElementInfo>());
	}
	
	public static ElementManager getInstance() {
		return instance;
	}
	
	public static void setInstance(final ElementManager eMan) {
		instance = eMan;
	}
	
	public Class<? extends Element> getElementClass(final Class<? extends Element> elementClass, final String type) {
		Validate.notNull(elementClass, "Element type class cannot be null.");
		final ElementInfo ei = elements.get(elementClass).get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.clss;
	}
	
	public boolean elementExists(final Class<? extends Element> elementClass, final String type) {
		Validate.notNull(elementClass, "Element type class cannot be null.");
		return elements.get(elementClass).containsKey(type.toUpperCase());
	}
	
	public String getElementUsage(final Class<? extends Element> elementClass, final String type) {
		Validate.notNull(elementClass, "Element type class cannot be null.");
		final ElementInfo ei = elements.get(elementClass).get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.usage;
	}
	
	public Command getElementCommand(final Class<? extends Element> elementClass, final String type) {
		Validate.notNull(elementClass, "Element type class cannot be null.");
		final ElementInfo ei = elements.get(elementClass).get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.command;
	}
	
	public String getElementList(final Class<? extends Element> elementClass) {
		Validate.notNull(elementClass, "Element type class cannot be null.");
		final Map<String, ElementInfo> map = elements.get(elementClass);
		if(map == null) {
			return "";
		}
		return Util.implode(map.keySet().toArray(new String[0]), ',');
	}
	
	private String getParentArgs(final String[] args) {
		final StringBuilder result = new StringBuilder();
		result.append(QConfiguration.displayedCmd).append(' ');
		for(final String arg : args) {
			result.append(arg).append(' ');
		}
		return result.toString();
	}
	
	private Element getFromCommand(final ElementInfo ei, final QuesterCommandContext context) throws CommandException, QuesterException {
		Object obj = null;
		try {
			String parent;
			if(context.length() < ei.command.min()) {
				parent = getParentArgs(context.getParentArgs());
				throw new UsageException(context.getSenderLang().get("ERROR_CMD_ARGS_NOT_ENOUGH"),
						parent + ei.usage);
			}
			if(!(ei.command.max() < 0) && context.length() > ei.command.max()) {
				parent = getParentArgs(context.getParentArgs());
				throw new UsageException(context.getSenderLang().get("ERROR_CMD_ARGS_TOO_MANY"),
						ei.usage);
			}
			
			obj = ei.commandMethod.invoke(null, context);
		}
		catch (final IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (final InvocationTargetException e) {
			if(e.getCause() instanceof CommandException) {
				throw (CommandException) e.getCause();
			}
			else if(e.getCause() instanceof QuesterException) {
				throw (QuesterException) e.getCause();
			}
			else if(e.getCause() instanceof IllegalArgumentException) {
				throw new CommandException(e.getCause().getMessage());
			}
			else {
				e.printStackTrace();
			}
		}
		return (Element) obj;
	}
	
	public Element getElementFromCommand(final Class<? extends Element> elementclass, final String type, final QuesterCommandContext context) throws CommandException, QuesterException {
		Validate.notNull(elementclass, "Element type class cannot be null.");
		final ElementInfo ei = elements.get(elementclass).get(type.toUpperCase());
		if(ei != null && context != null) {
			return getFromCommand(ei, context);
		}
		return null;
	}
	
	// return type of the method has been check during class registration
	@SuppressWarnings("unchecked")
	public <T extends Element> T invokeLoadMethod(final Class<T> elementclass, final String type, final StorageKey key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final ElementInfo ei = elements.get(elementclass).get(type.toUpperCase());
		return (T) ei.loadMethod.invoke(null, key);
	}
	
	public void register(final Class<? extends Element> clss) throws ElementException {
		register(clss, false);
	}
	
	public void register(final Class<? extends Element> clss, final boolean force) throws ElementException {
		if(!clss.isAnnotationPresent(QElement.class)) {
			throw new ElementException("Annotation not present.");
		}
		try {
			final Class<? extends Element> elementClass = getElementClass(clss);
			if(elementClass == null) {
				throw new ElementException("Unknown element type class.");
			}
			registerElement(elementClass, clss, force);
		}
		catch (final NoSuchMethodException e) {
			throw new ElementException("Missing fromCommand or load method.");
		}
		catch (final SecurityException e) {
			throw new ElementException("Element can't be accessed.");
		}
	}
	
	private Class<? extends Element> getElementClass(final Class<? extends Element> clss) {
		for(final Class<? extends Element> c : elements.keySet()) {
			if(c.isAssignableFrom(clss)) {
				return c;
			}
		}
		return null;
	}
	
	private void registerElement(final Class<? extends Element> elementClass, final Class<? extends Element> clss, final boolean force) throws NoSuchMethodException, SecurityException, ElementException {
		// check load method
		final Method load = clss.getDeclaredMethod("load", StorageKey.class);
		if(!Modifier.isStatic(load.getModifiers()) || !Modifier.isProtected(load.getModifiers())) {
			throw new ElementException(
					"Incorrect load method modifiers, expected \"protected static\".");
		}
		if(load.getReturnType() != elementClass) {
			throw new ElementException("Load method does not return "
					+ elementClass.getSimpleName() + ".");
		}
		// check fromcommand method
		final Method fromCommand =
				clss.getDeclaredMethod("fromCommand", QuesterCommandContext.class);
		if(!Modifier.isStatic(fromCommand.getModifiers())) {
			throw new ElementException("Incorrect fromCommand method modifiers, expected static.");
		}
		if(fromCommand.getReturnType() != elementClass) {
			throw new ElementException("fromCommand method does not return "
					+ elementClass.getSimpleName() + ".");
		}
		// check if an element of the same type exists
		final String type = clss.getAnnotation(QElement.class).value().toUpperCase();
		final Map<String, ElementInfo> map = elements.get(elementClass);
		if(map.containsKey(type)) {
			if(!force) {
				throw new ElementException(elementClass.getSimpleName()
						+ " of the same type already registered.");
			}
			Ql.info(elementClass.getSimpleName() + " " + type + " has been replaced by class "
					+ clss.getCanonicalName());
		}
		// register
		final ElementInfo ei = new ElementInfo();
		ei.clss = clss;
		ei.loadMethod = load;
		ei.loadMethod.setAccessible(true);
		ei.command = fromCommand.getAnnotation(Command.class);
		ei.commandMethod = fromCommand;
		ei.commandMethod.setAccessible(true);
		ei.usage = ei.command.usage();
		map.put(type, ei);
	}
}
