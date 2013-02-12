package com.gmail.molnardad.quester.elements;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface QElement {
	
	String value();
}
