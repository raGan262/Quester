package me.ragan262.quester.elements;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;

import me.ragan262.quester.QConfiguration;
import me.ragan262.quester.commandbase.QCommand;
import me.ragan262.quester.commandbase.QCommandContext;
import me.ragan262.quester.commandbase.exceptions.QCommandException;
import me.ragan262.quester.commandbase.exceptions.QUsageException;
import me.ragan262.quester.exceptions.ElementException;
import me.ragan262.quester.exceptions.QuesterException;
import me.ragan262.quester.storage.StorageKey;
import me.ragan262.quester.utils.Util;

public class ElementManager {
	
	public static enum ElementType {
		OBJECTIVE(Objective.class), CONDITION(Condition.class), EVENT(Qevent.class), TRIGGER(Trigger.class);
		
		private static final Map<Class<? extends Element>, ElementType> byClass =
				new HashMap<Class<? extends Element>, ElementType>();
		
		private final Class<? extends Element> clss;
		
		private ElementType(final Class<? extends Element> clss) {
			this.clss = clss;
		}
		
		public Class<? extends Element> getAssociatedClass() {
			return clss;
		}
		
		public static ElementType getByClass(final Class<?> clss) {
			return byClass.get(clss);
		}
		
		static {
			for(final ElementType type : values()) {
				byClass.put(type.getAssociatedClass(), type);
			}
		}
	}
	
	final class ElementInfo {
		private Class<? extends Element> clss;
		private String usage;
		private Method method;
		private QCommand command;
	}
	
	private static ElementManager instance = null;
	
	private final Map<ElementType, Map<String, ElementInfo>> elements =
			new EnumMap<ElementType, Map<String, ElementInfo>>(ElementType.class);
	
	public ElementManager() {
		for(final ElementType key : ElementType.values()) {
			elements.put(key, new HashMap<String, ElementInfo>());
		}
	}
	
	public static ElementManager getInstance() {
		return instance;
	}
	
	public static void setInstance(final ElementManager eMan) {
		instance = eMan;
	}
	
	public Class<? extends Element> getElementClass(final ElementType elementType, final String type) {
		Validate.notNull(elementType, "Element type cannot be null.");
		final ElementInfo ei = elements.get(elementType).get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.clss;
	}
	
	public boolean elementExists(final ElementType elementType, final String type) {
		Validate.notNull(elementType, "Element type cannot be null.");
		return elements.get(elementType).containsKey(type.toUpperCase());
	}
	
	public String getElementUsage(final ElementType elementType, final String type) {
		Validate.notNull(elementType, "Element type cannot be null.");
		final ElementInfo ei = elements.get(elementType).get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.usage;
	}
	
	public QCommand getElementCommand(final ElementType elementType, final String type) {
		Validate.notNull(elementType, "Element type cannot be null.");
		final ElementInfo ei = elements.get(elementType).get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.command;
	}
	
	public String getElementList(final ElementType elementType) {
		Validate.notNull(elementType, "Element type cannot be null.");
		final Map<String, ElementInfo> map = elements.get(elementType);
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
	
	private Element getFromCommand(final ElementInfo ei, final QCommandContext context) throws QCommandException, QuesterException {
		Object obj = null;
		try {
			String parent;
			if(context.length() < ei.command.min()) {
				parent = getParentArgs(context.getParentArgs());
				throw new QUsageException(context.getSenderLang().get("ERROR_CMD_ARGS_NOT_ENOUGH"),
						parent + ei.usage);
			}
			if(!(ei.command.max() < 0) && context.length() > ei.command.max()) {
				parent = getParentArgs(context.getParentArgs());
				throw new QUsageException(context.getSenderLang().get("ERROR_CMD_ARGS_TOO_MANY"),
						ei.usage);
			}
			
			obj = ei.method.invoke(null, context);
		}
		catch (final IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (final InvocationTargetException e) {
			if(e.getCause() instanceof QCommandException) {
				throw (QCommandException) e.getCause();
			}
			else if(e.getCause() instanceof QuesterException) {
				throw (QuesterException) e.getCause();
			}
			else if(e.getCause() instanceof IllegalArgumentException) {
				throw new QCommandException(e.getCause().getMessage());
			}
			else {
				e.printStackTrace();
			}
		}
		return (Element) obj;
	}
	
	public Element getElementFromCommand(final ElementType elementType, final String type, final QCommandContext context) throws QCommandException, QuesterException {
		Validate.notNull(elementType, "Element type cannot be null.");
		final ElementInfo ei = elements.get(elementType).get(type.toUpperCase());
		if(ei != null && context != null) {
			return getFromCommand(ei, context);
		}
		return null;
	}
	
	public void register(final Class<? extends Element> clss) throws ElementException {
		if(!clss.isAnnotationPresent(QElement.class)) {
			throw new ElementException("Annotation not present.");
		}
		try {
			final Method load = clss.getDeclaredMethod("load", StorageKey.class);
			if(!Modifier.isStatic(load.getModifiers())
					|| !Modifier.isProtected(load.getModifiers())) {
				throw new ElementException(
						"Incorrect load method modifiers, expected \"protected static\".");
			}
			final ElementType type = ElementType.getByClass(clss.getSuperclass());
			if(type != null) {
				if(load.getReturnType() != type.getAssociatedClass()) {
					throw new ElementException("Load method does not return " + type.name() + ".");
				}
				registerElement(type, clss);
			}
			else {
				throw new ElementException("Unknown element type.");
			}
		}
		catch (final NoSuchMethodException e) {
			throw new ElementException("Missing fromCommand or load method.");
		}
		catch (final SecurityException e) {
			throw new ElementException("Element can't be accessed.");
		}
	}
	
	private void registerElement(final ElementType elementType, final Class<? extends Element> clss) throws NoSuchMethodException, SecurityException, ElementException {
		final Method fromCommand = clss.getDeclaredMethod("fromCommand", QCommandContext.class);
		
		final String type = clss.getAnnotation(QElement.class).value().toUpperCase();
		final Map<String, ElementInfo> map = elements.get(elementType);
		if(map.containsKey(type)) {
			throw new ElementException(elementType.name() + " of the same type already registered.");
		}
		
		if(!Modifier.isStatic(fromCommand.getModifiers())) {
			throw new ElementException("Incorrect fromCommand method modifiers, expected static.");
		}
		if(fromCommand.getReturnType() != elementType.getAssociatedClass()) {
			throw new ElementException("fromCommand method does not return " + elementType.name()
					+ ".");
		}
		final ElementInfo ei = new ElementInfo();
		ei.clss = clss;
		ei.command = fromCommand.getAnnotation(QCommand.class);
		ei.method = fromCommand;
		ei.usage = ei.command.usage();
		map.put(type, ei);
	}
}
