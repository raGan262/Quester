package com.gmail.molnardad.quester;

import java.util.HashMap;
import java.util.Map;

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
	
	// instance
	
	private Map<String, Class<? extends Condition>> conditions = new HashMap<String, Class<? extends Condition>>();
	private Map<String, Class<? extends Objective>> objectives = new HashMap<String, Class<? extends Objective>>();
	private Map<String, Class<? extends Qevent>> events = new HashMap<String, Class<? extends Qevent>>();

	public Class<? extends Condition> getConditionClass(String type) {
		return conditions.get(type);
	}
	
	public Class<? extends Objective> getObjectiveClass(String type) {
		return objectives.get(type);
	}
	
	public Class<? extends Qevent> getEventClass(String type) {
		return events.get(type);
	}
	
	@SuppressWarnings("unchecked")
	public void register(Class<? extends Element> clss) throws ElementException {
		if(!clss.isAnnotationPresent(QElement.class)) {
			throw new ElementException("Annotation not present.");
		}
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
	
	private void registerCondition(Class<? extends Condition> clss) {
		conditions.put(clss.getAnnotation(QElement.class).value(), clss);
		// TODO register command
	}
	
	private void registerEvent(Class<? extends Qevent> clss) {
		events.put(clss.getAnnotation(QElement.class).value(), clss);
		// TODO register command
	}

	private void registerObjective(Class<? extends Objective> clss) {
		objectives.put(clss.getAnnotation(QElement.class).value(), clss);
		// TODO register command
	}
}
