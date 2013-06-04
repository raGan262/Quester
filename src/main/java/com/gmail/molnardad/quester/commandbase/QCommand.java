package com.gmail.molnardad.quester.commandbase;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface QCommand {
	
	String desc() default "";
	
	int min() default 0;
	
	int max() default -1;
	
	String usage() default "";
	
	String permission() default "";

	String section() default "";
	
	boolean forceExecute() default false;
}
