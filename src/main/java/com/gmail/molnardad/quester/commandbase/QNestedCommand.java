package com.gmail.molnardad.quester.commandbase;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface QNestedCommand {

	Class<?>[] value();
	
}
