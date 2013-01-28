package com.gmail.molnardad.quester;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.gmail.molnardad.quester.commandbase.QCommand;
import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.commandbase.exceptions.QCommandException;
import com.gmail.molnardad.quester.commandbase.exceptions.QUsageException;
import com.gmail.molnardad.quester.elements.*;
import com.gmail.molnardad.quester.exceptions.ElementException;
import com.gmail.molnardad.quester.exceptions.QuesterException;
import com.gmail.molnardad.quester.utils.Util;

public class ElementManager {

	// static part
	
	private static ElementManager instance = null;
	
	protected static void setInstance(ElementManager elementManager) {
		instance = elementManager;
	}
	
	public static ElementManager getInstance() {
		return instance;
	}
	
	final class ElementInfo<E> {
		private Class<? extends E> clss;
		private String usage;
		private Method method;
		private QCommand command;
	}
	
	// instance

	private Map<String, ElementInfo<Condition>> conditions = new HashMap<String, ElementInfo<Condition>>();
	private Map<String, ElementInfo<Objective>> objectives = new HashMap<String, ElementInfo<Objective>>();
	private Map<String, ElementInfo<Qevent>> events = new HashMap<String, ElementInfo<Qevent>>();

	public Class<? extends Condition> getConditionClass(String type) {
		ElementInfo<Condition> ei = conditions.get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.clss;
	}
	
	public Class<? extends Objective> getObjectiveClass(String type) {
		ElementInfo<Objective> ei = objectives.get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.clss;
	}
	
	public Class<? extends Qevent> getEventClass(String type) {
		ElementInfo<Qevent> ei = events.get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.clss;
	}
	
	public boolean isCondition(String type) {
		return conditions.containsKey(type.toUpperCase());
	}
	
	public boolean isObjective(String type) {
		return objectives.containsKey(type.toUpperCase());
	}
	
	public boolean isEvent(String type) {
		return events.containsKey(type.toUpperCase());
	}
	
	public String getConditionUsage(String type) {
		ElementInfo<Condition> ei = conditions.get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.usage;
	}
	
	public String getObjectiveUsage(String type) {
		ElementInfo<Objective> ei = objectives.get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.usage;
	}
	
	public String getEventUsage(String type) {
		ElementInfo<Qevent> ei = events.get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.usage;
	}
	
	public QCommand getConditionCommand(String type) {
		ElementInfo<Condition> ei = conditions.get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.command;
	}
	
	public QCommand getObjectiveHelp(String type) {
		ElementInfo<Objective> ei = objectives.get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.command;
	}
	
	public QCommand getEventHelp(String type) {
		ElementInfo<Qevent> ei = events.get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.command;
	}
	
	public String getConditionList() {
		return Util.implode(conditions.keySet().toArray(new String[0]), ',');
	}
	
	public String getObjectiveList() {
		return Util.implode(objectives.keySet().toArray(new String[0]), ',');
	}
	
	public String getEventList() {
		return Util.implode(events.keySet().toArray(new String[0]), ',');
	}
	
	private Element getFromCommand(ElementInfo<? extends Element> ei, QCommandContext context) throws QCommandException, QuesterException {
		Object obj = null; 
		try {
			// TODO dynamic language use
			if(context.length() < ei.command.min()) {
				throw new QUsageException("Not enough argmunents.", ei.usage);
			}
			if(!(ei.command.max() < 0) && context.length() > ei.command.max()) {
				throw new QUsageException("Too many argmunents.", ei.usage);
			}
			
			obj = ei.method.invoke(null, context);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
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
	
	public Condition getConditionFromCommand(String type, QCommandContext context) throws QCommandException, QuesterException {
		ElementInfo<Condition> ei = conditions.get(type.toUpperCase());
		if(ei != null && context != null) {
			return (Condition) getFromCommand(ei, context);
		}
		return null;
	}
	
	public Objective getObjectiveFromCommand(String type, QCommandContext context) throws QCommandException, QuesterException {
		ElementInfo<Objective> ei = objectives.get(type.toUpperCase());
		if(ei != null && context != null) {
			return (Objective) getFromCommand(ei, context);
		}
		return null;
	}
	
	public Qevent getEventFromCommand(String type, QCommandContext context) throws QCommandException, QuesterException {
		ElementInfo<Qevent> ei = events.get(type.toUpperCase());
		if(ei != null && context != null) {
			return (Qevent) getFromCommand(ei, context);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void register(Class<? extends Element> clss) throws ElementException {
		if(!clss.isAnnotationPresent(QElement.class)) {
			throw new ElementException("Annotation not present.");
		}
		try {
			if(clss.getSuperclass() == Condition.class) {
				registerCondition((Class<? extends Condition>) clss);
			}
			else if(clss.getSuperclass() == Qevent.class) {
				registerEvent((Class<? extends Qevent>) clss);
			}
			else if(clss.getSuperclass() == Objective.class) {
				registerObjective((Class<? extends Objective>) clss);
			}
			else {
				throw new ElementException("Unknown element type.");
			}
		}
		catch (NoSuchMethodException e) {
			throw new ElementException("Missing or incorrect fromCommand method.");
		}
		catch (SecurityException e) {
			throw new ElementException("Element can't be accessed.");
		}
	}
	
	private void registerCondition(Class<? extends Condition> clss) throws NoSuchMethodException, SecurityException, ElementException {
		Method fromCommand = clss.getMethod("fromCommand", QCommandContext.class); 
		
		String type = clss.getAnnotation(QElement.class).value().toUpperCase();
		if(conditions.containsKey(type)) {
			throw new ElementException("Condition of the same type already registered.");
		}
		
		if(!Modifier.isStatic(fromCommand.getModifiers())) {
			throw new NoSuchMethodException("Method is not static.");
		}
		if(fromCommand.getReturnType() != Condition.class) {
			throw new NoSuchMethodError("Method does not return Condition.");
		}
		ElementInfo<Condition> ei = new ElementInfo<Condition>();
		ei.clss = clss;
		ei.command = fromCommand.getAnnotation(QCommand.class);
		ei.method = fromCommand;
		ei.usage = ei.command.usage();
		conditions.put(type, ei);
	}
	
	private void registerEvent(Class<? extends Qevent> clss) throws NoSuchMethodException, SecurityException, ElementException { 
		Method fromCommand = clss.getMethod("fromCommand", QCommandContext.class);
		
		String type = clss.getAnnotation(QElement.class).value().toUpperCase();
		if(events.containsKey(type)) {
			throw new ElementException("Event of the same type already registered.");
		}

		if(!Modifier.isStatic(fromCommand.getModifiers())) {
			throw new NoSuchMethodException("Method is not static.");
		}
		if(fromCommand.getReturnType() != Qevent.class) {
			throw new NoSuchMethodError("Method does not return Qevent.");
		}
		ElementInfo<Qevent> ei = new ElementInfo<Qevent>();
		ei.clss = clss;
		ei.command = fromCommand.getAnnotation(QCommand.class);
		ei.method = fromCommand;
		ei.usage = ei.command.usage();
		events.put(type, ei);
	}

	private void registerObjective(Class<? extends Objective> clss) throws NoSuchMethodException, SecurityException, ElementException {
		Method fromCommand = clss.getMethod("fromCommand", QCommandContext.class);
		
		String type = clss.getAnnotation(QElement.class).value().toUpperCase();
		if(objectives.containsKey(type)) {
			throw new ElementException("Objective of the same type already registered.");
		}
		
		if(!Modifier.isStatic(fromCommand.getModifiers())) {
			throw new NoSuchMethodException("Method is not static.");
		}
		if(fromCommand.getReturnType() != Qevent.class) {
			throw new NoSuchMethodError("Method does not return Objective.");
		}
		ElementInfo<Objective> ei = new ElementInfo<Objective>();
		ei.clss = clss;
		ei.command = fromCommand.getAnnotation(QCommand.class);
		ei.method = fromCommand;
		ei.usage = ei.command.usage();
		objectives.put(type, ei);
	}
}
