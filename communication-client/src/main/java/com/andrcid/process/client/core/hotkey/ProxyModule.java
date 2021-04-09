package com.andrcid.process.client.core.hotkey;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProxyModule {
	
	String value() default "";
	
    Class<?> from() default void.class;
    
    boolean singleton() default true;
}
