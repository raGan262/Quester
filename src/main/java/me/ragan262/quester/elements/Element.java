package me.ragan262.quester.elements;

public abstract class Element {
	
	public final String getType() {
		if(this.getClass().isAnnotationPresent(QElement.class)) {
			return this.getClass().getAnnotation(QElement.class).value().toUpperCase();
		}
		return "";
	}
}
