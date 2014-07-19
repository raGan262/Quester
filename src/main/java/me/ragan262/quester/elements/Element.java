package me.ragan262.quester.elements;

public abstract class Element {
	
	public static final Class<Condition> CONDITION = Condition.class;
	public static final Class<Objective> OBJECTIVE = Objective.class;
	public static final Class<Qevent> QEVENT = Qevent.class;
	public static final Class<Trigger> TRIGGER = Trigger.class;
	
	public final String getType() {
		if(this.getClass().isAnnotationPresent(QElement.class)) {
			return this.getClass().getAnnotation(QElement.class).value().toUpperCase();
		}
		return "";
	}
}
