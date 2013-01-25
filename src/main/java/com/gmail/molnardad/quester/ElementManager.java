package com.gmail.molnardad.quester;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.gmail.molnardad.quester.commandbase.QCommandContext;
import com.gmail.molnardad.quester.elements.*;
import com.gmail.molnardad.quester.exceptions.ElementException;

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
		private String help;
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
	
	public String getConditionHelp(String type) {
		ElementInfo<Condition> ei = conditions.get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.help;
	}
	
	public String getObjectiveHelp(String type) {
		ElementInfo<Objective> ei = objectives.get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.help;
	}
	
	public String getEventHelp(String type) {
		ElementInfo<Qevent> ei = events.get(type.toUpperCase());
		if(ei == null) {
			return null;
		}
		return ei.help;
	}
	
	public void register(Class<? extends Element> clss) throws ElementException {
		register(clss, "", "");
	}
	
	public void register(Class<? extends Element> clss, String usage) throws ElementException {
		register(clss, usage, "");
	}
	
	@SuppressWarnings("unchecked")
	public void register(Class<? extends Element> clss, String usage, String help) throws ElementException {
		if(!clss.isAnnotationPresent(QElement.class)) {
			throw new ElementException("Annotation not present.");
		}
		try {
			if(clss.getSuperclass() == Condition.class) {
				registerCondition((Class<? extends Condition>) clss, usage, help);
			}
			else if(clss.getSuperclass() == Qevent.class) {
				registerEvent((Class<? extends Qevent>) clss, usage, help);
			}
			else if(clss.getSuperclass() == Objective.class) {
				registerObjective((Class<? extends Objective>) clss, usage, help);
			}
			else {
				throw new ElementException("Unknown element type.");
			}
		}
		catch (NoSuchMethodException e) {
			throw new ElementException(e.getMessage());
		}
		catch (SecurityException e) {
			throw new ElementException("Element can't be accessed.");
		}
	}
	
	private void registerCondition(Class<? extends Condition> clss, String usage, String help) throws NoSuchMethodException, SecurityException {
		Method fromCommand = clss.getMethod("fromCommand", QCommandContext.class); 
		if(fromCommand != null) {
			if(!Modifier.isStatic(fromCommand.getModifiers())) {
				throw new NoSuchMethodException("Method is not static.");
			}
			if(fromCommand.getReturnType() != Condition.class) {
				throw new NoSuchMethodError("Method does not return Condition.");
			}
			ElementInfo<Condition> ei = new ElementInfo<Condition>();
			ei.clss = clss;
			ei.usage = usage;
			ei.help = help;
			conditions.put(clss.getAnnotation(QElement.class).value(), ei);
		}
		else {
			throw new NoSuchMethodException("Method null.");
		}
	}
	
	private void registerEvent(Class<? extends Qevent> clss, String usage, String help) throws NoSuchMethodException, SecurityException { 
		Method fromCommand = clss.getMethod("fromCommand", QCommandContext.class);
		if(fromCommand != null) {
			if(!Modifier.isStatic(fromCommand.getModifiers())) {
				throw new NoSuchMethodException("Method is not static.");
			}
			if(fromCommand.getReturnType() != Qevent.class) {
				throw new NoSuchMethodError("Method does not return Qevent.");
			}
			ElementInfo<Qevent> ei = new ElementInfo<Qevent>();
			ei.clss = clss;
			ei.usage = usage;
			ei.help = help;
			events.put(clss.getAnnotation(QElement.class).value(), ei);
		}
		else {
			throw new NoSuchMethodException("Method null.");
		}
	}

	private void registerObjective(Class<? extends Objective> clss, String usage, String help) throws NoSuchMethodException, SecurityException {
		Method fromCommand = clss.getMethod("fromCommand", QCommandContext.class);
		if(fromCommand != null) {
			if(!Modifier.isStatic(fromCommand.getModifiers())) {
				throw new NoSuchMethodException("Method is not static.");
			}
			if(fromCommand.getReturnType() != Qevent.class) {
				throw new NoSuchMethodError("Method does not return Objective.");
			}
			ElementInfo<Objective> ei = new ElementInfo<Objective>();
			ei.clss = clss;
			ei.usage = usage;
			ei.help = help;
			objectives.put(clss.getAnnotation(QElement.class).value(), ei);
		}
		else {
			throw new NoSuchMethodException("Method null.");
		}
	}
}
